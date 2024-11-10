package com.timi.centre.found.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timi.centre.found.bean.RecommandPlayResult
import com.timi.centre.R

class RecommandPlayAdapter(
    private val recommandList: List<RecommandPlayResult>,
    private val fragment: Fragment,
    private val onViewClicked: (Long) -> Unit       //推荐歌单item点击事件，按需修改
) : RecyclerView.Adapter<RecommandPlayAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mView = view
        val picture: ImageView = view.findViewById(R.id.iv_PlaylistDetail_picture)
        val text: TextView = view.findViewById(R.id.tv_PlaylistDetail_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_detail, parent, false)).apply {
            mView.setOnClickListener{
                onViewClicked(recommandList[adapterPosition].id)
            }
        }

    private var recommandPlay: RecommandPlayResult? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        recommandPlay = recommandList[position]
        Glide.with(fragment).load(recommandPlay!!.picUrl).into(holder.picture)
        holder.text.text = recommandPlay!!.name
        recommandPlay = null
    }

    override fun getItemCount(): Int = recommandList.size
    
}