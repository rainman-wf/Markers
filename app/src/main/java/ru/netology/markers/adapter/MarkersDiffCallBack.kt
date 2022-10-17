package ru.netology.markers.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.markers.models.UserMarker

class MarkersDiffCallBack : DiffUtil.ItemCallback<UserMarker>() {

    override fun areItemsTheSame(oldItem: UserMarker, newItem: UserMarker): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: UserMarker, newItem: UserMarker): Boolean {
        return  (oldItem == newItem)
    }
}