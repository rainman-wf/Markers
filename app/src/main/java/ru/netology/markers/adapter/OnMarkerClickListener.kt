package ru.netology.markers.adapter

interface OnMarkerClickListener {

    fun onEditClicked(id: Long)
    fun onRemoveClicked(id: Long)
}