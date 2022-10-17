package ru.netology.markers.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.markers.R
import ru.netology.markers.adapter.MarkerAdapter
import ru.netology.markers.adapter.OnMarkerClickListener
import ru.netology.markers.databinding.FragmentMarkerListBinding
import ru.netology.markers.viewmodel.MarkersViewModel

@AndroidEntryPoint
class MarkerListFragment : Fragment(R.layout.fragment_marker_list) {

    private val viewModel: MarkersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMarkerListBinding.bind(view)
        val navController = findNavController()

        val markerAdapter = MarkerAdapter(
            object : OnMarkerClickListener {
                override fun onEditClicked(id: Long) {
                    viewModel.setCurrentMapObject(id)
                    navController.navigate(
                        MarkerListFragmentDirections.actionLocationsListFragmentToMapFragment(
                            MapFragmentReason.EDIT
                        )
                    )
                }

                override fun onRemoveClicked(id: Long) {
                    viewModel.onRemoveClicked(id)
                }
            }
        )

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.show_on_map_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.showOnMap -> {
                        navController.navigate(
                            MarkerListFragmentDirections.actionLocationsListFragmentToMapFragment(
                                MapFragmentReason.PREVIEW
                            )
                        )
                        true
                    }
                    else ->false
                }
            }
        }, viewLifecycleOwner)

        binding.itemList.adapter = markerAdapter

        binding.addNew.setOnClickListener {
            viewModel.clearCurrentMapObject()
            navController.navigate(
                MarkerListFragmentDirections.actionLocationsListFragmentToMapFragment(
                    MapFragmentReason.ADD
                )
            )
        }

        viewModel.liveDataContainer.data.observe(viewLifecycleOwner) {
            markerAdapter.submitList(it.toList().reversed())
        }

    }
}
