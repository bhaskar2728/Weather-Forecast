package com.devdroid.weatherkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListViewAdapter(private var mCtx: Context, var resources: Int, var items: List<ModelForecast>):ArrayAdapter<ModelForecast>(mCtx,resources,items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(resources,null)

        val txtDateRow: TextView = view.findViewById(R.id.txtDateRow)
        val txtTempRow: TextView = view.findViewById(R.id.txtTempRow)

        var mItem:ModelForecast = items[position]

        txtDateRow.text = mItem.date
        txtTempRow.text = mItem.temp


        return view
    }


}