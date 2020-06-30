package com.devdroid.weatherkotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.txtDate
import kotlinx.android.synthetic.main.fragment_current.txtDesc
import kotlinx.android.synthetic.main.fragment_current.txtHumidity
import kotlinx.android.synthetic.main.fragment_current.txtLocation
import kotlinx.android.synthetic.main.fragment_current.txtWindSpeed
import kotlinx.android.synthetic.main.fragment_forecast.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ForecastFrag : Fragment() {

    lateinit var jor: JsonObjectRequest
    lateinit var url: String
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var lat: String
    lateinit var long: String
    lateinit var rclView: RecyclerView
    lateinit var list:ArrayList<ModelForecast>
    lateinit var adapter_new: RecyclerViewAdapter
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_forecast,container,false)

        rclView = view.rclView
        rclView.layoutManager = LinearLayoutManager(context)
        rclView.setHasFixedSize(true)

        list = ArrayList<ModelForecast>()
        adapter_new = RecyclerViewAdapter(context!!,list)

        rclView.adapter = adapter_new

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
        return view
    }

    private fun getWeather() {
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&exclude=hourly,minutely&appid=3f54fe6b6b3669e0808a6565ca19f418"
        val jor = JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                response ->

            try{

                list.clear()
                val dateArray = response.getJSONArray("daily")
                Log.d("size = ","" + dateArray.length())
                Log.d("arr",dateArray.toString())
                for(i in 0 until dateArray.length()){

                    val day1 = dateArray.getJSONObject(i)
                    val date = day1.getLong("dt")

                    val sdf = java.text.SimpleDateFormat("yyyy-MMMM-dd")
                    val date1 = java.util.Date(date * 1000)
                    val date_new = sdf.format(date1)
                    Log.d("dates",date_new)
                    val temp = day1.getJSONObject("temp")
                    val day_temp = temp.getString("day")
                    val night_temp = temp.getString("night")
                    val weatherArray = day1.getJSONArray("weather")
                    val desc = weatherArray.getJSONObject(0).getString("description")
                    val date_row = "${date_new.toString().split("-")[2]} ${date_new.toString().split("-")[1]} ${date_new.toString().split("-")[0]}"

                    sharedPreferences = context!!.getSharedPreferences("temperature", Context.MODE_PRIVATE)
                    val check = sharedPreferences.getString("temp","C")

                    view!!.txtUsrName.text = "Hello, ${sharedPreferences.getString("name", "UserName")}"

                    if(check == "Celsius"){
                        val c = day_temp.toDouble() - 273.15
                        val temp_new = "${String.format("%.2f",c)} ℃"
                        list.add(ModelForecast(date_row,temp_new))
                    }
                    else{
                        val c = ((day_temp.toDouble()*9)/5) - 459.67
                        val temp_new = "${String.format("%.2f",c)} ℉"
                        list.add(ModelForecast(date_row,temp_new))

                    }
                }
                adapter_new.notifyDataSetChanged()


            }
            catch(e:Exception){

            }
        },Response.ErrorListener {
                error ->
            Toast.makeText(activity,"Error = "+error.message,Toast.LENGTH_LONG).show()

        })

        var queue = Volley.newRequestQueue(activity)
        queue.add(jor)
    }


}
