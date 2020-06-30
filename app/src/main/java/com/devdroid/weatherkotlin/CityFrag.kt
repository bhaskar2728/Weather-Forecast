package com.devdroid.weatherkotlin

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.android.synthetic.main.fragment_city.txtDate
import kotlinx.android.synthetic.main.fragment_city.txtDesc
import kotlinx.android.synthetic.main.fragment_city.txtHumidity
import kotlinx.android.synthetic.main.fragment_city.txtLocation
import kotlinx.android.synthetic.main.fragment_city.txtWindSpeed
import kotlinx.android.synthetic.main.fragment_city.view.*
import kotlinx.android.synthetic.main.fragment_current.*
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CityFrag : Fragment(), OnMapReadyCallback {

    lateinit var date_sel : String
    lateinit var options: Array<String>
    lateinit var city: String
    lateinit var sharedPreferences:SharedPreferences
    lateinit var mMap : GoogleMap
    var mapFlag = 0
    lateinit var lat:String
    lateinit var long:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_city, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
        date_sel = sdf.format(Date())
        options = arrayOf("Delhi","Mumbai","Noida")
        view.spinnerCity.adapter = ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1,options)
        view.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                city = options.get(position)
               getWeatherReport(options.get(position))
            }

        }
        view.btnDate.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val format1 = SimpleDateFormat("YYYY-MM-dd",Locale.US)
                val now = Calendar.getInstance()
                val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{
                    view,year,month,dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR,year)
                    selectedDate.set(Calendar.MONTH,month)
                    selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                    val date_str = format1.format(selectedDate.time)
                    Toast.makeText(context,"$date_str",Toast.LENGTH_SHORT).show()

                    date_sel = format1.format(selectedDate.time)
                    getWeatherReport(city)

                },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }

        })
        // Inflate the layout for this fragment
        return view
    }

    private fun getWeatherReport(city: String) {


        var flag: Int

        when (city) {
            "Noida" -> {
                lat = "28.5355"
                long = "77.3910"

            }
            "Delhi" -> {
                lat = "28.7041"
                long = "77.1025"
            }
            else -> {
                lat = "19.0760"
                long = "72.8777"
            }




        }

        if (mapFlag==1) {
            mMap.clear()
            val marker = LatLng(lat.toDouble(), long.toDouble())
            mMap.addMarker(MarkerOptions().position(marker).title("You're in $city"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 5f))

        }

        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&exclude=hourly,minutely&appid=3f54fe6b6b3669e0808a6565ca19f418"
        val jor = JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
            response ->

            try{

                val dateArray = response.getJSONArray("daily")
                flag = 0
                Log.d("size = ","" + dateArray.length())
                Log.d("arr",dateArray.toString())
                for(i in 0 until dateArray.length()){
                    val day1 = dateArray.getJSONObject(i)
                    val date1 = day1.getLong("dt")

                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
                    val date = java.util.Date(date1 * 1000)
                    val date_new = sdf.format(date)
                    Log.d("dates",date_new)

                    if(date_sel == date_new.toString()){

                        val temp = day1.getJSONObject("temp")
                        val day_temp = temp.getString("day")
                        val night_temp = temp.getString("night")
                        val humidity = day1.getString("humidity")
                        val windSpeed = day1.getString("wind_speed")
                        val weatherArray = day1.getJSONArray("weather")
                        val desc = weatherArray.getJSONObject(0).getString("description")

                        val sdf1 = java.text.SimpleDateFormat("yyyy-MMMM-dd")
                        val date1 = java.util.Date(date1 * 1000)
                        val date_new = sdf1.format(date1)

                        sharedPreferences = context!!.getSharedPreferences("temperature", Context.MODE_PRIVATE)
                        val check = sharedPreferences.getString("temp","C")
                        view!!.txtUsrName.text = "Hello, ${sharedPreferences.getString("name", "UserName")}"

                        if(check == "Celsius"){
                            val c1 = day_temp.toDouble() - 273.15
                            val c2 = night_temp.toDouble() - 273.15
                            txtTempDay.text = "${String.format("%.2f",c1)} ℃"
                            txtTempNight.text = "${String.format("%.2f",c2)} ℃"
                        }
                        else{
                            val c1 = ((day_temp.toDouble()*9)/5) - 459.67
                            val c2 = ((night_temp.toDouble()*9)/5) - 459.67
                            txtTempDay.text = "${String.format("%.2f",c1)} ℉"
                            txtTempNight.text = "${String.format("%.2f",c2)} ℉"
                        }

                        txtLocation.text = city
                        txtDate.text = "${date_new.toString().split("-")[2]} ${date_new.toString().split("-")[1]} ${date_new.toString().split("-")[0]}"
                        txtDesc.text = desc.capitalize()
                        txtHumidity.text = humidity
                        txtWindSpeed.text = windSpeed
                        flag = 1
                    }
                }
                if(flag!=1){
                    Toast.makeText(context,"Data not available for this date",Toast.LENGTH_SHORT).show()
                    val sdf2 = java.text.SimpleDateFormat("yyyy-MM-dd")
                    date_sel = sdf2.format(Date())
                    getWeatherReport(city)
                }

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

    override fun onMapReady(p0: GoogleMap?) {
        if (p0!=null) {
            mMap = p0
            mapFlag = 1
        }


    }

}
