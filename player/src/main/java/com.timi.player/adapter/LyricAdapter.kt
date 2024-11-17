package com.timi.player.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.player.R
import com.timi.player.store.bean.ProcessedLyric

class LyricAdapter(
	private val lyricList: List<ProcessedLyric>,
	private val onItemClicked: () -> Unit
) : RecyclerView.Adapter<LyricAdapter.ViewHolder>() {

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
		val mView = view
		val tv_lyric: TextView = view.findViewById(R.id.tv_Lyric_text)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lyric, parent, false)).apply {
			mView.setOnClickListener {
				onItemClicked()
			}
		}

	override fun onBindViewHolder(holder: ViewHolder, position: Int){
		val effectivePosition = position - 7
		holder.tv_lyric.text =
			if (0 <= effectivePosition && effectivePosition < lyricList.size)
				lyricList[effectivePosition].word
			else ""
	}

	override fun getItemCount(): Int = lyricList.size + 14

}