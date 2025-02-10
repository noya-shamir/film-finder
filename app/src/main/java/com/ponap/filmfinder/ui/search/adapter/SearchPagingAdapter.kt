package com.ponap.filmfinder.ui.search.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ponap.filmfinder.model.Media

class SearchPagingAdapter(
    private val favoritesClickListener: (media: Media) -> Unit,
    private val clickListener: (media: Media, position: Int) -> Unit
) :
    PagingDataAdapter<Media, SearchItemViewHolder>(
        diffCallback
    ) {

    private var selectedId: String? = null

    fun updateUserSelection(imdbId: String? = null) {
        val ids: MutableSet<String> = mutableSetOf()
        selectedId?.let { ids.add(it) }
        imdbId?.let { ids.add(it) }

        selectedId = imdbId

        if (ids.isNotEmpty()) {
            // find the views to update:
            val positions = snapshot().items.withIndex()
                .filter { it.value.imdbId in ids }
                .map { it.index }

            for (position in positions) {
                notifyItemChanged(position)
            }
        }
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = selectedId != null && selectedId == item?.imdbId
        holder.onBind(item, isSelected, favoritesClickListener, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(parent)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.imdbId == newItem.imdbId
            }

            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem == newItem
            }

        }
    }
}
