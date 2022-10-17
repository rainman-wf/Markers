package ru.netology.markers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.markers.databinding.MarkerCardBinding
import ru.netology.markers.models.UserMarker

class MarkerAdapter(private val onMarkerClickListener: OnMarkerClickListener) :
    ListAdapter<UserMarker, MarkerViewHolder>(MarkersDiffCallBack()) {

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        val marker = getItem(position) ?: return
        holder.bind(marker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MarkerCardBinding.inflate(inflater, parent, false)
        return MarkerViewHolder(binding, onMarkerClickListener)
    }
}