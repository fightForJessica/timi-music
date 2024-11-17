package com.timi.player.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.player.R
import com.timi.player.store.bean.Song

class BottomSongListAdapter(
	var songList: List<Song>,
	private val onItemClicked: (Int) -> Unit,
	private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	inner class TitleHolder(view: View) : RecyclerView.ViewHolder(view){
		val tv_songNum: TextView = view.findViewById(R.id.tv_Bottom_songNum)
	}

	inner class SongHolder(view: View) : RecyclerView.ViewHolder(view){
		val mView = view
		val tv_songName: TextView = view.findViewById(R.id.tv_Bottom_songName)
		val tv_artist: TextView = view.findViewById(R.id.tv_Bottom_artist)
		val iv_delete: ImageView = view.findViewById(R.id.iv_Bottom_delete)
	}

	override fun getItemViewType(position: Int): Int = if (position == 0) 0 else 1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
		if (viewType == 0){
			TitleHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_player_bottom_title, parent, false))
		}else{
			SongHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_player_bottom_song, parent, false)).apply {
				mView.setOnClickListener {
					onItemClicked(adapterPosition - 1)
				}
				iv_delete.setImageResource(R.drawable.ic_delete)
				iv_delete.setOnClickListener {
					onDeleteClick(adapterPosition - 1)
				}
			}
		}

	private var song: Song? = null
	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is SongHolder){
			song = songList[position - 1]
			holder.tv_songName.text = song!!.songName
			holder.tv_artist.text = song!!.artistAndDescription.split(" -")[0]
			song = null
		}else if (holder is TitleHolder){
			holder.tv_songNum.text = "(${songList.size})"
		}
	}

	override fun getItemCount(): Int = songList.size + 1

}