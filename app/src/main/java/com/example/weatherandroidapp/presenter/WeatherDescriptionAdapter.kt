package com.example.weatherandroidapp.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem

class WeatherDescriptionAdapter(private val context: Context, private val dataSet: Array<WeatherDescriptionItem>) :
    RecyclerView.Adapter<WeatherDescriptionAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView
        val icon : ImageView

        init {
            icon = view.findViewById(R.id.item_icon)
            description = view.findViewById(R.id.item_description)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.weather_description_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.description.text = dataSet[position].description
        viewHolder.icon.setImageDrawable(AppCompatResources.getDrawable(context, dataSet[position].icon))

    }


    override fun getItemCount() = dataSet.size

}
