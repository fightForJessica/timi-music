package com.timi.centre.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.bean.Song

class SearchResultAdapter(
    var songList: List<Song>,
    private val onResultItemClicked: (songId: Long, songName: String, arAndDes: String, picUrl: String) -> Unit,
    private val onItemMoreClicked: () -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val TAG = "SearchResultAdapter"

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mView = view
        val tv_SoneName: TextView = view.findViewById(R.id.tv_Search_resultSong)
        val tv_SingerAndDescription: TextView = view.findViewById(R.id.tv_Search_resultSingerAndDescription)
        val iv_more: ImageView = view.findViewById(R.id.iv_Search_resultMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)).apply {
            mView.setOnClickListener {
                //外抛搜索结果Item的点击事件
                val song = songList[adapterPosition]
                onResultItemClicked(song.id, song.name, tv_SingerAndDescription.text.toString(), song.album.artist.img1v1Url)
            }

            iv_more.setOnClickListener {
                //外抛点击"更多"图标的点击事件
                onItemMoreClicked()
            }
        }

    private var song: Song? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        song = songList[position]
        holder.apply {

            tv_SoneName.text = song!!.name

            //拼接作品歌手以及描述字符串
            StringBuilder().apply {
                val artistList = song!!.artists
                for (i in artistList.indices) {
                    if (i != artistList.size - 1)
                        append("${artistList[i].name}/")
                    else
                        append(artistList[i].name)
                }
                append(" - ${song!!.album.name}")
                tv_SingerAndDescription.text = toString()
                clear()
            }
        }
        song = null
    }

    override fun getItemCount(): Int = songList.size
}