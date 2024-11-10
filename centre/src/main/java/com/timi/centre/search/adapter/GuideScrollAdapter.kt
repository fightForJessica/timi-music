package com.timi.centre.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.adapter.GuideScrollDetailAdapter
import com.timi.centre.search.bean.Artist
import com.timi.centre.search.bean.HotSearchData

class GuideScrollAdapter(
    private val mContext: Context,
    private val hotSearchData: List<HotSearchData>,
    private val hotArtistData: List<Artist>,
    private val onColumnItemClicked: (String) -> Unit
) : RecyclerView.Adapter<GuideScrollAdapter.ViewHolder>() {

    //榜单名称集合,在添加栏目时需要更改
    private val columnList = listOf("热搜榜", "热门歌手")
    //各栏目的Item数量
    private val item_empty = 0
    private val item_default = 20

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val column: TextView = view.findViewById(R.id.tv_Guide_scrollColumn)
        val rv_columnDetail: RecyclerView = view.findViewById(R.id.rv_Guide_scrollDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_scrolldetail, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            column.text = columnList[position]
            rv_columnDetail.apply {
                layoutManager = LinearLayoutManager(mContext)

                //根据布局位置修改所需要展示的榜单
                adapter = when(position){
                    //热搜榜
                    0 -> GuideScrollDetailAdapter(item_default, 0, hotSearchData = hotSearchData){ columnContent ->
                        onColumnItemClicked(columnContent)
                    }
                    //热门歌手榜
                    else -> GuideScrollDetailAdapter(item_default, 1, hotArtistData = hotArtistData){ columnContent ->
                        onColumnItemClicked(columnContent)
                    }
                }
            }
        }
    }

    /**
     * 需要根据栏目数进行改变(根据榜单名称数量多少进行同步)
     */
    override fun getItemCount(): Int = columnList.size
}