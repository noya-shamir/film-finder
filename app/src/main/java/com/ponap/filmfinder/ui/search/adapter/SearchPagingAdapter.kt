package com.ponap.filmfinder.ui.search.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ponap.filmfinder.model.Movie

class SearchPagingAdapter(private val clickListener: (movie: Movie, position: Int) -> Unit) :
    PagingDataAdapter<Movie, SearchItemViewHolder>(
        diffCallback
    ) {

    private var selectedId: String? = null

    fun updateUserSelection(imdbId: String? = null) {
        val prev = selectedId

        selectedId = imdbId

        // find the views to update:
        val positions = snapshot().items.withIndex()
            .filter { it.value.imdbId == prev || it.value.imdbId == selectedId }
            .map { it.index }

        for (position in positions) {
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = selectedId != null && selectedId == item?.imdbId
        holder.onBind(item, isSelected, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(parent)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.imdbId == newItem.imdbId
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }

        }
    }
}
