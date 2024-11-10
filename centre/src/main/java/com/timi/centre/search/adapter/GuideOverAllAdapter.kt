package com.timi.centre.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.search.bean.Artist
import com.timi.centre.search.bean.Guess
import com.timi.centre.search.bean.History
import com.timi.centre.search.bean.HotSearchData


class GuideOverAllAdapter(
    private val mContext: Context,
    var history: List<History>,                         //历史搜索
    private val guessList: List<Guess>,                 //猜你喜欢
    private val hotSearchData: List<HotSearchData>,     //热搜榜
    private val hotArtistData: List<Artist>,            //热门歌手榜
    private val onTextCardClicked: (String) -> Unit,
    private val onClearItemClicked: () -> Unit,
    private val onColumnItemClicked: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    companion object{
        private const val TYPE_HISTORY = 1
        private const val TYPE_GUESS = 2
        private const val TYPE_SCROLL = 3
    }

    //搜索历史
    inner class HistoryHolder(view: View) : RecyclerView.ViewHolder(view){
        val rv_history: RecyclerView = view.findViewById(R.id.rv_Guide_history)
        val clear: ImageView = view.findViewById(R.id.iv_Guide_deleteHistory)
    }

    //猜你喜欢
    inner class GuessLikeHolder(view: View) : RecyclerView.ViewHolder(view){
        val rv_guessLike: RecyclerView = view.findViewById(R.id.rv_Guide_guessLike)
    }

    //横向滚动榜单
    inner class ScrollHolder(view: View) : RecyclerView.ViewHolder(view){
        val rv_scroll: RecyclerView = view.findViewById(R.id.rv_Guide_scroll)
    }

    override fun getItemViewType(position: Int): Int =
        if(history.isEmpty()){
            when(position){
                0 -> TYPE_GUESS
                else -> TYPE_SCROLL
            }
        }else{
            when(position){
                0 -> TYPE_HISTORY
                1 -> TYPE_GUESS
                else -> TYPE_SCROLL
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_HISTORY -> HistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_history, parent, false))
            TYPE_GUESS -> GuessLikeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_guesslike, parent, false))
            else -> ScrollHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_guide_scroll, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HistoryHolder -> setHistoryHolder(holder)
            is GuessLikeHolder -> setGuessLikeHolder(holder)
            is ScrollHolder -> setScrollHolder(holder)
        }
    }

    //如果没有历史记录,则不创建HistoryHolder
    override fun getItemCount(): Int  = if (history.isEmpty()) 2 else 3

    private fun setHistoryHolder(holder: HistoryHolder){
        holder.apply {
            rv_history.apply {
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = GuideHistoryAdapter(history) { historyText ->
                    //外抛TextView卡片点击事件
                    onTextCardClicked(historyText)
                }
            }
            clear.setOnClickListener{
                //外抛清除历史记录图像点击事件
                onClearItemClicked()
            }
        }
    }

    private fun setGuessLikeHolder(holder: GuessLikeHolder){
        holder.rv_guessLike.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = GuideGuessLikeAdapter(guessList){ guessText ->
                //外抛TextView卡片点击事件
                onTextCardClicked(guessText)
            }
        }
    }

    private fun setScrollHolder(holder: ScrollHolder){
        holder.rv_scroll.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = GuideScrollAdapter(context, hotSearchData, hotArtistData){ columnContent ->
                //外抛榜单Item点击事件
                onColumnItemClicked(columnContent)
            }
        }
    }

}