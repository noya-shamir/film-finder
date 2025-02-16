package com.ponap.filmfinder.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.ponap.filmfinder.R
import com.ponap.filmfinder.databinding.ItemSearchBinding
import com.ponap.filmfinder.model.Media
import com.ponap.filmfinder.ui.onThrottleFirstClickListener

class SearchItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
) {
    private val binding = ItemSearchBinding.bind(itemView)
    private val image: ImageView = binding.image
    private val title: TextView = binding.title
    private val subtitle: TextView = binding.year
    private val card: CardView = binding.itemCard
    private val isFavoriteIcon: ImageView = binding.isFavorite

    fun onBind(
        item: Media?,
        isSelected: Boolean,
        favoritesClickListener: (media: Media) -> Unit,
        clickListener: (media: Media, position: Int) -> Unit
    ) {
        title.text = item?.title
        subtitle.text = item?.year

        image.load(item?.poster) {
            placeholder(android.R.drawable.progress_indeterminate_horizontal)
            error(android.R.drawable.ic_menu_report_image)
        }

        itemView.contentDescription = item?.title

        item?.let { updateIsFavoriteIcon(item.isFavorite) }
        isFavoriteIcon.setOnClickListener {
            item?.let {
                item.isFavorite = !item.isFavorite
                updateIsFavoriteIcon(it.isFavorite)
                favoritesClickListener(item)
            }
        }


        val colorRes = if (isSelected) R.color.selected_item_background else R.color.item_background
        card.setCardBackgroundColor(ContextCompat.getColor(card.context, colorRes))

        itemView.onThrottleFirstClickListener {
            item?.let { media ->
                clickListener(media, bindingAdapterPosition)
            }
        }
    }

    private fun updateIsFavoriteIcon(isFavorite: Boolean) {
        val imageResource = if (isFavorite) android.R.drawable.star_on else
            android.R.drawable.star_off
        val stringResource = if (isFavorite) R.string.is_favorite else R.string.not_favorite
        isFavoriteIcon.setImageResource(imageResource)
        isFavoriteIcon.contentDescription = isFavoriteIcon.resources.getString(stringResource)
    }

}