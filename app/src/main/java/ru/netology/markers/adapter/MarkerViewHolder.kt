package ru.netology.markers.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.markers.R
import ru.netology.markers.databinding.MarkerCardBinding
import ru.netology.markers.models.UserMarker

class MarkerViewHolder(
    private val binding: MarkerCardBinding,
    private val onMarkerClickListener: OnMarkerClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(userMarker: UserMarker) {
        binding.apply {
            description.text = userMarker.description
            more.setOnClickListener { showPopupMenu(more, userMarker.id) }
        }
    }

    private fun showPopupMenu(view: View, id: Long) {
        with(PopupMenu(view.context, view)) {
            inflate(R.menu.location_item_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit -> setListener(onMarkerClickListener.onEditClicked(id))
                    R.id.remove -> setListener(onMarkerClickListener.onRemoveClicked(id))
                    else -> false
                }
            }
            show()
        }
    }

    private val setListener: (Unit) -> Boolean = { true }
}