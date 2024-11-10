package com.timi.centre.playlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R

class CoverLabelAdapter(
    private val labelList: List<String>
) : RecyclerView.Adapter<CoverLabelAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val labelText: TextView = view.findViewById(R.id.tv_Cover_labelCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cover_labelcard, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.labelText.text = labelList[position]
    }

    override fun getItemCount(): Int = labelList.size
}