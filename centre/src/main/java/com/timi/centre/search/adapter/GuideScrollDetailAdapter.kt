package com.timi.centre.search.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.bean.Artist
import com.timi.centre.search.bean.HotSearchData

class GuideScrollDetailAdapter(
    private val itemCount: Int,
    private val currentColumn: Int,
    private val hotSearchData: List<HotSearchData>? = null,
    private val hotArtistData: List<Artist>? = null,
    private val onColumnItemClicked: ((String) -> Unit)? = null
) : RecyclerView.Adapter<GuideScrollDetailAdapter.ViewHolder>() {

    private val TAG = "GuideScrollDetailAdapter"

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mView = view
        val number: TextView = view.findViewById(R.id.tv_Guide_scrollColumn_number)
        val content: TextView = view.findViewById(R.id.tv_Guide_scrollColumn_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_scrolldetail_content, parent, false)).apply {
            mView.setOnClickListener {
                //外抛榜单Item点击事件
                onColumnItemClicked?.let { it1 -> it1(content.text.toString()) }
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {

            number.apply {
                text = (position + 1).toString()
                //设置字体颜色，排行1-3的项为红色，其余为浅灰色
                setTextColor(Color.parseColor(if (position in 0..2) "#DC1708" else "#919191"))
            }

            //根据榜单位置设定榜单内容
            when(currentColumn){
                //热搜榜单数据绑定
                0 -> content.text = hotSearchData!![position].searchWord

                //热门歌手榜单数据绑定
                1 -> content.text = hotArtistData!![position].name
            }

        }
    }

    /**
     * 大多数热榜的item子项数量都为20
     */
    override fun getItemCount(): Int = itemCount
}