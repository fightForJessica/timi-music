package com.timi.centre.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.bean.Guess

class GuideGuessLikeAdapter(
    private val guessList: List<Guess>,
    private val onTextCardClicked: (String) -> Unit
) : RecyclerView.Adapter<GuideGuessLikeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mView = view
        val guessText: TextView = view.findViewById(R.id.tv_Guide_textCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_textcard, parent, false)).apply {
            mView.setOnClickListener{
                //外抛TextView卡片点击事件
                onTextCardClicked(guessText.text.toString())
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.guessText.text = guessList[position].first
    }

    override fun getItemCount(): Int = guessList.size

}