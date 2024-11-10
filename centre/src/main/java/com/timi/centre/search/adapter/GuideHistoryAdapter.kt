package com.timi.centre.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.bean.History

class GuideHistoryAdapter(
    private val history: List<History>,
    private val onTextCardClicked: (String) -> Unit,
) : RecyclerView.Adapter<GuideHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mView = view
        val historyText: TextView = view.findViewById(R.id.tv_Guide_textCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_textcard, parent, false)).apply {
            mView.setOnClickListener{
                //外抛TextView卡片点击事件
                onTextCardClicked(historyText.text.toString())
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.historyText.text = history[position].content
    }

    override fun getItemCount(): Int = history.size
    
}