package ru.netology.markers.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentMapBinding
import ru.netology.markers.models.MarkerMetaData
import ru.netology.markers.viewmodel.MarkersViewModel

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapKit: MapKit
    private val args: MapFragmentArgs by navArgs()
    private val viewModel: MarkersViewModel by viewModels()
    private lateinit var mapFunctions: MapFunctions

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            binding.myLocation.isVisible = it
        }

    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {}

        override fun onMapLongTap(p0: Map, p1: Point) {
            showInputDescriptionDialog(p1)
        }
    }

    private val moveListener = object : MapObjectDragListener {

        var lat: Double? = null
        var long: Double? = null

        override fun onMapObjectDragStart(p0: MapObject) {
            Log.d("TAG", "onMapObjectDragStart: ")
        }

        override fun onMapObjectDrag(p0: MapObject, p1: Point) {
            Log.d("TAG", "onMapObjectDrag: ")
            lat = p1.latitude
            long = p1.longitude
        }

        override fun onMapObjectDragEnd(p0: MapObject) {
            Log.d("TAG", "onMapObjectDragEnd: ")
            viewModel.updateCurrentMapObject(
                lat!!, long!!, (p0.userData as MarkerMetaData).title
            )
        }

    }

    private val onObjectTapListener = MapObjectTapListener { p0, p1 ->
        showInputDescriptionDialog(p1, (p0.userData as MarkerMetaData).title)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(requireContext())
        super.onCreate(savedInstanceState)
        mapKit = MapKitFactory.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        val mapView = binding.mapView
        mapFunctions = MapFunctions(binding.mapView, requireContext())

        when (args.reason) {

            MapFragmentReason.ADD -> {
                mapView.map.addInputListener(inputListener)
                getMyLocation(object : PointCallBack {
                    override fun callback(point: Point) {
                        mapFunctions.moveCamera(point.latitude, point.longitude)
                    }
                })
            }

            MapFragmentReason.EDIT -> {
                mapView.map.addInputListener(inputListener)
                viewModel.liveDataContainer.currentPointSet.observe(viewLifecycleOwner) { marker ->
                    marker?.let {
                        mapFunctions.moveCamera(it.pointLat, it.pointLong)
                    }
                }
            }

            MapFragmentReason.PREVIEW -> {
                viewModel.liveDataContainer.data.observe(viewLifecycleOwner) { markers ->
                    when {
                        markers.isEmpty() -> return@observe
                        markers.size == 1 -> {
                            mapFunctions.addMapObject(markers.first(), onObjectTapListener)
                            mapFunctions.moveCamera(
                                markers.first().pointLat,
                                markers.first().pointLong
                            )
                        }
                        else -> {
                            markers.forEach {
                                mapFunctions.addMapObject(it, onObjectTapListener)
                            }
                            mapFunctions.moveCamera(markers)
                        }
                    }
                }
            }
        }

        viewModel.liveDataContainer.currentMapObject.observe(viewLifecycleOwner) { marker ->
            marker?.let { mapFunctions.updateMapObject(it, onObjectTapListener, moveListener) }
        }

        binding.myLocation.setOnClickListener {
            getMyLocation(object : PointCallBack {
                override fun callback(point: Point) {
                    mapFunctions.moveCamera(point.latitude, point.longitude)
                }
            })

        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.save_marker_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        try {
                            viewModel.onSaveClicked()
                            viewModel.liveDataContainer.mapObjectCreated.observe(viewLifecycleOwner) {
                                findNavController().navigateUp()
                            }
                        } catch (e: NullPointerException) {
                            Snackbar
                                .make(binding.root, e.message.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner)
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    private fun getMyLocation(callBack: PointCallBack) {
        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.myLocation.isVisible = true
            LocationServices.getFusedLocationProviderClient(requireContext())
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    callBack.callback(Point(loc.latitude, loc.longitude))
                }
        } else requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private interface PointCallBack {
        fun callback(point: Point)
    }

    private fun showInputDescriptionDialog(point: Point, description: String? = null) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Input description")

        val alertDialogLayout =
            layoutInflater.inflate(R.layout.input_desription_dialog, null)
        builder.setView(alertDialogLayout)

        val inputText = alertDialogLayout.findViewById<EditText>(R.id.inputDescriptionText)

        description?.let {
            inputText.setText(it)
        }

        viewModel.liveDataContainer.currentMapObject.value?.let {
            inputText.setText(it.description)
        }

        builder.setPositiveButton("OK") { _, _ ->
            viewModel.updateCurrentMapObject(
                point.latitude, point.longitude,
                inputText.text.toString()
            )
        }
        builder.create().show()
    }
}

