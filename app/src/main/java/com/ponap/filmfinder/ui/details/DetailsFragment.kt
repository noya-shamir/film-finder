package com.ponap.filmfinder.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.ponap.filmfinder.R
import com.ponap.filmfinder.databinding.FragmentDetailsBinding
import com.ponap.filmfinder.model.Media
import com.ponap.filmfinder.model.MediaDetails
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setIsFavoriteListener()

        // observe selectedMediaLiveData to display the media and additional details once fetched:
        viewModel.selectedMediaLiveData.observe(viewLifecycleOwner) { media ->
            media?.let {
                displayMediaInfo(media)
            } ?: kotlin.run { findNavController().popBackStack() }
        }

        // observe loading state
        viewModel.loadingUiState.observe(viewLifecycleOwner) { uiState ->

            binding.loader.isVisible = uiState.isLoading

            binding.errorMessage.isVisible = uiState.error != null
            uiState.error?.let { errorMessage ->
                context?.let { ctx ->
                    Timber.w("oy, error getting the media details: $errorMessage")
                    Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayMediaInfo(media: Media) {
        binding.title.text = media.title
        binding.year.text = getString(R.string.separated_text, media.year, media.type)
        binding.image.load(media.poster) {
            placeholder(android.R.drawable.progress_indeterminate_horizontal)
            error(android.R.drawable.ic_menu_report_image)
        }
        binding.image.contentDescription = media.title
        displayAdditionalDetails(media.additionalDetails)
        updateIsFavoriteIcon(media.isFavorite)
    }

    private fun displayAdditionalDetails(details: MediaDetails?) {
        if (details != null) {
            binding.genre.text = details.genre
            binding.rating.text = getString(R.string.ratings, details.imdbRating)
            binding.director.text = getString(R.string.director, details.director)
            binding.writers.text = getString(R.string.writers, details.writer)
            binding.cast.text = getString(R.string.cast, details.actors)
            binding.plot.text = getString(R.string.plot, details.plot)
            binding.detailsContainer.visibility = View.VISIBLE
            binding.loader.visibility = View.GONE
            binding.errorMessage.visibility = View.GONE
        } else {
            binding.detailsContainer.visibility = View.GONE
        }
    }

    private fun updateIsFavoriteIcon(isFavorite: Boolean) {
        val imageResource = if (isFavorite) android.R.drawable.star_on else
            android.R.drawable.star_off
        val stringResource = if (isFavorite) R.string.is_favorite else R.string.not_favorite
        binding.isFavorite.setImageResource(imageResource)
        binding.isFavorite.contentDescription = resources.getString(stringResource)
    }

    private fun setIsFavoriteListener() {
        binding.isFavorite.setOnClickListener {
            viewModel.toggleMediaIsFavorite()
        }
    }

}