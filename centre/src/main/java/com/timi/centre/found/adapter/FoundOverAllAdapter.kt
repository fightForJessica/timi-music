package com.timi.centre.found.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.found.bean.Banner
import com.timi.centre.found.bean.RecommandPlayResult
import com.timi.centre.R

class FoundOverAllAdapter(
    private val bannerList: List<Banner>,
    private val recommandList: List<RecommandPlayResult>,
    private val fragment: Fragment,
    private val bannerScroll: (RecyclerView) -> Unit,
    private val onBannerClicked: () -> Unit,
    private val onRecommandClicked: (Long) -> Unit,
    private val onRecommandTextClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var bannerCreate = false
    private var recommandListCreate = false

    inner class BannerHolder(view: View) : RecyclerView.ViewHolder(view){
        val banner: RecyclerView = view.findViewById(R.id.rv_Found_banner)
    }

    inner class RecommandPlayHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv_recommand: TextView = view.findViewById(R.id.tv_Found_recommendPlayText)
        val recommand: RecyclerView = view.findViewById(R.id.rv_Found_recommendPlay)
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> 0
            else -> 1
        }
    }

    /**
     * 当添加新的ViewHolder种类时，需要添加元素
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> BannerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_found_banner, parent, false))
            else -> RecommandPlayHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_found_recommand, parent, false)).apply {
                tv_recommand.setOnClickListener {
                    onRecommandTextClicked()
                }
            }
        }
    }

    /**
     * 当添加新的ViewHolder种类时，更改返回值
     */
    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is BannerHolder -> setBannerHolder(holder)
            is RecommandPlayHolder -> setRecommandPlayHolder(holder)
        }
    }

    //绑定banner数据
    private fun setBannerHolder(holder: BannerHolder){
        if (!bannerCreate){
            //当轮播图还未创建时
            bannerCreate = true
            holder.banner.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = BannerAdapter(bannerList){ view ->
                    //轮播图点击事件
                    onBannerClicked()
                }
                setHasFixedSize(true)
                scrollToPosition(bannerList.size * 10)

                onFlingListener = null
                PagerSnapHelper().attachToRecyclerView(this)

                bannerScroll(this) //轮播图滚动外抛到fragment的handler处理
            }
        }
    }

    //绑定推荐歌单数据
    private fun setRecommandPlayHolder(holder: RecommandPlayHolder){
        if (!recommandListCreate){
            recommandListCreate = true
            holder.recommand.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RecommandPlayAdapter(recommandList, fragment){ playlistId ->
                    //推荐歌单点击事件
                    onRecommandClicked(playlistId)
                }
                setHasFixedSize(true)
            }
        }
    }
}