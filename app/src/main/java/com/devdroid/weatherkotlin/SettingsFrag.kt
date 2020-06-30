package com.devdroid.weatherkotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_city.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFrag : Fragment() {
    lateinit var options : Array<String>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        options = arrayOf("Celsius","Fahrenheit")
        view.spinnerTemp.adapter = ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1,options)
        view.spinnerTemp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //activity!!.supportFragmentManager.beginTransaction().remove(ForecastFrag()).commit()
                sharedPreferences = context!!.getSharedPreferences("temperature",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("temp",options.get(position))
                editor.commit()

            }

        }
        // Inflate the layout for this fragment
        return view
    }

}
