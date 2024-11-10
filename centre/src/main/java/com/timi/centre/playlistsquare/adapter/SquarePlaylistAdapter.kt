package com.timi.centre.playlistsquare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timi.centre.R
import com.timi.centre.playlistsquare.bean.Playlists

class SquarePlaylistAdapter(
	var playlist: List<Playlists>,
	private val fragment: Fragment,
	private val onItemClicked: (Long) -> Unit
) : RecyclerView.Adapter<SquarePlaylistAdapter.ViewHolder>() {

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
		val mView = view
		val iv_pic: ImageView = view.findViewById(R.id.iv_PlaylistDetail_picture)
		val tv_title: TextView = view.findViewById(R.id.tv_PlaylistDetail_title)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_detail, parent, false)).apply {
			mView.setOnClickListener {
				onItemClicked(playlist[adapterPosition].id)
			}
		}

	private var aPlay: Playlists? = null
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		aPlay = playlist[position]
		holder.tv_title.text = aPlay!!.name
		Glide.with(fragment).load(aPlay!!.coverImgUrl).into(holder.iv_pic)
		aPlay = null
	}

	override fun getItemCount(): Int = playlist.size

}