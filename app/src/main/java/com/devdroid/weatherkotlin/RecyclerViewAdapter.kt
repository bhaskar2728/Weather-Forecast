package com.devdroid.weatherkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_row.view.*

class RecyclerViewAdapter(val context: Context, val arrModelForecast : ArrayList<ModelForecast>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDateRow = itemView.txtDateRow
        val txtTempRow = itemView.txtTempRow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return arrModelForecast.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtDateRow.text = arrModelForecast[position].date
        holder.txtTempRow.text = arrModelForecast[position].temp
    }
}