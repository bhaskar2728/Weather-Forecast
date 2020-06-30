package com.devdroid.weatherkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.TextLinks
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.txtDate
import kotlinx.android.synthetic.main.fragment_current.txtDesc
import kotlinx.android.synthetic.main.fragment_current.txtHumidity
import kotlinx.android.synthetic.main.fragment_current.txtWindSpeed
import kotlinx.android.synthetic.main.fragment_current.view.*
import org.json.JSONObject
import java.lang.Exception
import java.security.Permission
import java.security.PermissionCollection
import java.util.*
import java.util.jar.Manifest

/**
 * A simple [Fragment] subclass.
 */
class CurrentFrag : Fragment() {

    var permission = "no"
    lateinit var latlong: String
    lateinit var lat: String
    lateinit var long: String
    lateinit var jor: JsonObjectRequest
    lateinit var url: String
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        Dexter.withActivity(activity).withPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object: PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(activity,"Permission granted",Toast.LENGTH_LONG).show()

                    val mLocationRequest = LocationRequest.create()
                    mLocationRequest.interval = 60000
                    mLocationRequest.fastestInterval = 5000
                    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    val mLocationCallback = object : LocationCallback(){
                        override fun onLocationResult(p0: LocationResult?) {
                            if(p0==null){
                                return
                            }

                        }

                    }
                    LocationServices.getFusedLocationProviderClient(context!!).requestLocationUpdates(mLocationRequest,mLocationCallback,null)
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener{location ->
                        if(location!=null){

                            lat = location.latitude.toString()
                            long = location.longitude.toString()
                            Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show()
                            getWeather()
                        }


                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    Toast.makeText(activity,"Permission denied",Toast.LENGTH_LONG).show()
                }

            }).check()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current, container, false)
    }

    private fun getWeather() {

        url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=3f54fe6b6b3669e0808a6565ca19f418"
        jor = JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener {response ->

                try{
                    val dt = response.getLong("dt")
                    val main = response.getJSONObject("main")
                    val array = response.getJSONArray("weather")
                    val wind = response.getJSONObject("wind")
                    val weather = array.getJSONObject(0)
                    val windSpeed = wind.getString("speed")
                    var description = weather.getString("description")
                    var temperature = main.getString("temp")
                    var humidity = main.getString("humidity")


                    val sdf = java.text.SimpleDateFormat("yyyy-MMMM-dd")
                    val date = java.util.Date(dt * 1000)
                    val date_new = sdf.format(date)

                    sharedPreferences = context!!.getSharedPreferences("temperature",Context.MODE_PRIVATE)
                    val check = sharedPreferences.getString("temp","C")

                    view!!.txtUsrName.text = "Hello, ${sharedPreferences.getString("name", "UserName")}"

                    if(check == "Celsius"){
                        val c = temperature.toDouble() - 273.15
                        txtTemp.text = "${String.format("%.2f",c)} ℃"
                    }
                    else{
                        val c = ((temperature.toDouble()*9)/5) - 459.67
                        txtTemp.text = "${String.format("%.2f",c)} ℉"
                    }

                    txtDate.text = "${date_new.toString().split("-")[2]} ${date_new.toString().split("-")[1]} ${date_new.toString().split("-")[0]}"
                    txtWindSpeed.text = windSpeed
                    txtDesc.text = description.capitalize()
                    txtHumidity.text = humidity
                }
                catch(e: Exception){
                    Toast.makeText(activity,"Error = "+e,Toast.LENGTH_LONG).show()
                }
            },Response.ErrorListener {error ->
                Toast.makeText(activity,"Error = "+error.message,Toast.LENGTH_LONG).show()

            })

        var queue = Volley.newRequestQueue(activity)
        queue.add(jor)
    }

}
