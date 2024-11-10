package com.timi.centre.playlist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.playlist.bean.Ar
import com.timi.centre.playlist.bean.Track
import kotlin.text.StringBuilder

class SongListAdapter(
	private val songList: List<Track>,
	private val onItemClick: (songId: Long, songName: String, arAndDes: String, picUrl: String) -> Unit,
	private val onMoreClick: () -> Unit
) : RecyclerView.Adapter<SongListAdapter.ViewHolder>(){

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
		val mView = view
		val tv_number: TextView = view.findViewById(R.id.tv_Playlist_songNumber)
		val tv_songName: TextView = view.findViewById(R.id.tv_Playlist_songName)
		val tv_artistAndDescription: TextView = view.findViewById(R.id.tv_PlayList_artistAndDescription)
		val iv_more: ImageView = view.findViewById(R.id.iv_PlayList_more)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_song, parent, false)).apply {
			mView.setOnClickListener {
				val song = songList[adapterPosition]
				onItemClick(song.id, song.name, tv_artistAndDescription.text.toString(), song.al.picUrl)
			}
			iv_more.setOnClickListener {
				onMoreClick()
			}
		}

	private var song: Track? = null
	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		song = songList[position]
		holder.apply {
			tv_number.text = (position + 1).toString()
			tv_songName.text = song!!.name

			//拼接名称和歌曲描述
			var artist : Ar
			StringBuilder().apply {
				for (i in song!!.ar.indices) {
					artist = song!!.ar[i]
					append(if (i != song!!.ar.size - 1) "${artist.name}/" else artist.name)
				}
				append(" - ${song!!.al.name}")
				tv_artistAndDescription.text = toString()
				clear()
			}

		}
		song = null
	}

	override fun getItemCount(): Int = songList.size

}