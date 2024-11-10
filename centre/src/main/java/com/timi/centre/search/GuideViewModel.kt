package com.timi.centre.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.centre.search.bean.GuessLike
import com.timi.centre.search.bean.History
import com.timi.centre.search.bean.HotArtist
import com.timi.centre.search.bean.HotSearch

class GuideViewModel : ViewModel() {

    private val guideModel: GuideModel by lazy { GuideModel() }

    val historyData = MutableLiveData<List<History>?>()      //历史搜索
    val guessLikeData = MutableLiveData<GuessLike?>()        //猜你喜欢
    val hotSearchData = MutableLiveData<HotSearch?>()        //热搜榜
    val hotArtistData = MutableLiveData<HotArtist?>()        //热门歌手榜


    init {
        historyData.value = null
        guessLikeData.value = null
        hotSearchData.value = null
        hotArtistData.value = null
    }

    /**
     * 加载数据库中的历史搜索数据
     */
    fun loadHistory(context: Context){
        guideModel.loadHistory(this, context)
    }

    /**
     * 将搜索内容保存至数据库
     */
    fun storeHistory(content: String, context: Context){
        guideModel.storeHistory(this, content, context)
    }

    /**
     * 清除历史搜索记录
     */
    fun clearHistory(context: Context){
        guideModel.clearHistory(context)
    }


    /**
     * 发出猜你喜欢数据请求
     */
    fun guessLikeRequest(){
        guideModel.guessLikeRequest(this)
    }

    /**
     * 发出热搜榜数据请求
     */
    fun hotSearchRequest(){
        guideModel.hotSearchRequest(this)
    }

    /**
     * 发出热门歌手榜数据请求
     */
    fun hotArtistsRequest(){
        guideModel.hotArtistsRequest(this)
    }

}