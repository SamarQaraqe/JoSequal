package com.josequal.maps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.josequal.maps.R
import com.josequal.maps.models.Place

class PlacesAdapter(private val onItemClick: (Place) -> Unit) :
    RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private var placeItems: List<Place> = emptyList()

    fun setPlaceItems(items: List<Place>) {
        placeItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_place_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val placeItem = placeItems[position]
        holder.bind(placeItem)
    }

    override fun getItemCount(): Int = placeItems.size

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeTitle: TextView = itemView.findViewById(R.id.title)
        private val placeImage: ImageView = itemView.findViewById(R.id.image)

        fun bind(placeItem: Place) {
            placeTitle.text = placeItem.title
            Glide.with(itemView.context).load(placeItem.image).into(placeImage)

            itemView.setOnClickListener {
                onItemClick.invoke(placeItem)
            }
        }
    }
}
