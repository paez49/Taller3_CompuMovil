package com.example.nuevo_parchaosr.activities.map


import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nuevo_parchaosr.activities.BasicFragment
import com.example.nuevo_parchaosr.databinding.MapaBinding
import com.example.nuevo_parchaosr.utils.PermissionHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections


class MapaFragment : BasicFragment(), SensorEventListener, OnMapReadyCallback,OnMarkerClickListener,
    GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener,GoogleMap.OnMapLongClickListener{
    private lateinit var binding: MapaBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastTime: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val mMarkerArray = ArrayList<Marker>()
    private val shakeThreshold = 5500 // umbral de sacudida
    private lateinit var map: GoogleMap
    private var locationButton: View? = null
    //temporales
    private var counter=1
    private var poly: Polyline? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
     var permissionHelper = PermissionHelper()
    @SuppressLint("MissingPermission", "ResourceType")
    override fun onMapReady(p0: GoogleMap) {
        map=p0
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnMarkerClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(com.example.nuevo_parchaosr.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationButton = mapFragment.view?.findViewById(0x2);
        locationButton?.visibility = View.GONE;
        map.uiSettings.isMapToolbarEnabled =false
    }

     @SuppressLint("MissingPermission")
     private fun enableLocation() {
         permissionHelper.getLocationPermission(activity)
         if(!::map.isInitialized) return
         if (permissionHelper.mLocationPermissionGranted) {
             map.isMyLocationEnabled=true
         }
         else
         {
             //activar permisos toast
             Toast.makeText(requireContext(), "Activa permisos de ubicacion.", Toast.LENGTH_SHORT).show()
         }
     }

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MapaBinding.inflate(inflater)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(com.example.nuevo_parchaosr.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         binding.locate.setOnClickListener(View.OnClickListener {
             locationButton?.callOnClick()
         })
         binding.filter.setOnClickListener(View.OnClickListener {

         })

         return binding.root
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapaFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    private fun createMarker(coordinates: LatLng, lugar:String) {
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title(lugar)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates,18f),
            4000,
            null
        )

    }
    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastTime
        if (timeDifference > 100) {
            lastTime = currentTime
            val x = event?.values?.get(0)
            val y = event?.values?.get(1)
            val z = event?.values?.get(2)
            val speed = Math.abs(x!! + y!! + z!! - lastX - lastY - lastZ) / timeDifference * 10000
            if (speed > shakeThreshold) {
                Log.d("TAG", "Sacudida detectada")
                Toast.makeText(requireContext(), "Sacudida", Toast.LENGTH_SHORT).show()
                poly?.remove()
                poly = null
                Toast.makeText(requireContext(), "Encontrando ruta a tu parcero mas cercano", Toast.LENGTH_SHORT).show()
                val p1= findNearest()
                if (p1 != null)
                {
                    //ruta a p1
                    Toast.makeText(requireContext(), "Dirigiendo a "+p1.title, Toast.LENGTH_SHORT).show()
                    createRoute("${map.myLocation.longitude},${map.myLocation.latitude}","${p1.position.longitude},${p1.position.latitude}")
                }
                else
                    Toast.makeText(requireContext(), "No tienes amigos vro", Toast.LENGTH_SHORT).show()
            }
            lastX = x
            lastY = y
            lastZ = z
        }

    }


    override fun onMapLongClick(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("dummy "+counter++).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        val marker = map.addMarker(markerOptions)
        if (marker != null) {
            mMarkerArray.add(marker)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // No se usa
    }
    override fun onMyLocationClick(p0: Location) {
        poly?.remove()
        poly = null
        Toast.makeText(requireContext(), "Encontrando ruta a tu parcero mas cercano", Toast.LENGTH_SHORT).show()
        val p1= findNearest()
        if (p1 != null)
        {
            //ruta a p1
            Toast.makeText(requireContext(), "Dirigiendo a "+p1.title, Toast.LENGTH_SHORT).show()
            createRoute("${map.myLocation.longitude},${map.myLocation.latitude}","${p1.position.longitude},${p1.position.latitude}")
        }
        else
            Toast.makeText(requireContext(), "No tienes amigos vro", Toast.LENGTH_SHORT).show()
    }
     fun findNearest(): Marker? {
         val miLocation = map.myLocation
         val mapDistance: MutableMap<Double, Marker> = HashMap(20)
         for (Marker in mMarkerArray) {
             val distance: Double = distFrom(Marker.position.latitude,Marker.position.longitude, miLocation.latitude,miLocation.longitude)
             mapDistance[distance] = Marker
         }
         return if(mapDistance.isNotEmpty()) {
             mapDistance[Collections.min(mapDistance.keys)]
         } else
             null
     }
    fun distFrom(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        val earthRadius = 6371000.0 //meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(
            Math.toRadians(
                lat2
            )
        ) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c =
            2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadius * c)
    }
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "Centrando", Toast.LENGTH_SHORT).show()
        return false
    }

     override fun onMarkerClick(p0: Marker): Boolean {
         return true
     }
     //Rutas retrofeed

        fun getRetrofid():Retrofit{
            return Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    fun createRoute(s:String,e:String){
        CoroutineScope(Dispatchers.IO).launch {
            val k = "5b3ce3597851110001cf624811f6f7cc80db4b04961baca95f7a286d"
            val call = getRetrofid().create(retrofit2Api::class.java)
                .getRoute(k,s,e )
            if (call.isSuccessful)
            {
                drawRoute(call.body())
            }
            else
            {Toast.makeText(requireContext(), "No fue posible ubicar una ruta", Toast.LENGTH_SHORT).show()}
        }
    }
    fun drawRoute(routeResponse: RouteResponse?){
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1],it[0]))
        }
        activity?.runOnUiThread{
            poly = map.addPolyline(polyLineOptions)
        }
    }
 }
