package com.ponap.filmfinder.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ponap.filmfinder.R
import com.ponap.filmfinder.databinding.FragmentSearchBinding
import com.ponap.filmfinder.model.Media
import com.ponap.filmfinder.ui.search.adapter.PagingLoadStateAdapter
import com.ponap.filmfinder.ui.search.adapter.SearchPagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels<SearchViewModel>()

    private val snapHelper = LinearSnapHelper()

    private val adapter = SearchPagingAdapter(::onItemFavoritesIconClicked, ::onItemClicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchView()
        initRecycler()

        // observe the paging data and pass it to the adapter:
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // observe the adapter's loadState, to know if we need to shoe the empty view:
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes, such as NotLoading.
                .filter { it.refresh is LoadState.NotLoading || it.refresh is LoadState.Error }
                // Show empty view as needed
                .collect {
                    val isEmpty = adapter.itemCount == 0 && viewModel.getCurrentQuery().isNotEmpty()
                    binding.emptyResults.isVisible = isEmpty
                    (it.refresh as? LoadState.Error)?.let { errorState ->
                        context?.let { ctx ->
                            Toast.makeText(ctx, errorState.error.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

        // observe the selected item:
        viewModel.selectedMediaLiveData.observe(viewLifecycleOwner) { media ->
            adapter.updateUserSelection(media?.imdbId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSearchView() {

        binding.searchView.isSubmitButtonEnabled = true

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onNewQuery(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // for now, we ignore and wait for the user to submit the query
                // to consider - adding a runnable to submit from here, if the user hasn't changed the text for 500 ms
                return false
            }
        })
    }

    private fun onNewQuery(query: String?) {
        if (viewModel.isNewQuery(query)) {
            // we want new results to display from position 0:
            binding.recycler.scrollToPosition(0)
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.submitData(PagingData.empty())
            }
            query?.let { viewModel.submitQuery(it) }
        }
    }

    private fun initRecycler() {
        // attach snap helper:
        snapHelper.attachToRecyclerView(binding.recycler)
        // add layout manager:
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        // add adapter:
        binding.recycler.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(retry = { adapter::retry }),
            footer = PagingLoadStateAdapter(retry = { adapter::retry })
        )
    }

    private fun onItemClicked(media: Media, position: Int) {

        // pass the selection to viewModel, which will also check if the media is selected or un-selected
        if (viewModel.setSelectedMediaAsNeeded(media)) {
            // this is a new selected media (i.e. not un-selecting already selected one), so
            // snap to position, then navigate to details:
            binding.recycler.smoothScrollToPosition(position)
            binding.recycler.post {
                val lm = binding.recycler.layoutManager
                val view = lm?.findViewByPosition(position) ?: return@post
                val snapDistance = snapHelper.calculateDistanceToFinalSnap(lm, view)
                snapDistance?.let {
                    if (it[0] != 0 || it[1] != 0) {
                        binding.recycler.smoothScrollBy(it[0], it[1])
                    }
                }
                // navigate to detailsFragment, but first check isAdded() because we are in post()
                _binding?.recycler?.postDelayed(100) {
                    safelyNavigateToDetails()
                }
            }
        }

    }

    private fun onItemFavoritesIconClicked(media: Media) {
        viewModel.onMediaIsFavoriteChanged(media)
    }

    private fun safelyNavigateToDetails() {
        // first check isAdded, in case this method was called
        // from post(), postDelayed() or liveData change:
        if (isAdded) {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build()
            findNavController().navigate(R.id.detailsFragment, null, navOptions)
        }
    }

}