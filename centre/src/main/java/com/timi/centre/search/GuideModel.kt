package com.timi.centre.search

import android.content.Context
import com.timi.centre.search.bean.GuessLike
import com.timi.centre.search.bean.GuessLikeService
import com.timi.centre.search.bean.History
import com.timi.centre.search.bean.HotArtist
import com.timi.centre.search.bean.HotArtistService
import com.timi.centre.search.bean.HotSearch
import com.timi.centre.search.bean.HotSearchService
import com.timi.centre.search.room.HistoryDatabase
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuideModel {

    private val TAG = "GuideModel"

    /**
     * 加载数据库中历史搜索数据
     */
    fun loadHistory(guideVM: GuideViewModel, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            HistoryDatabase.getInstance(context).historyDao().getAllHistory().apply {
                Logger.i(TAG, "历史记录:$this")
                guideVM.historyData.postValue(this)
            }
        }
    }

    /**
     * 将搜索过的记录保存到数据库中，并重新更新fragment中的搜索历史数据
     * @param content 搜索内容
     */
    fun storeHistory(guideVM: GuideViewModel, content: String, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            HistoryDatabase.getInstance(context).historyDao().apply {
                searchHistory(content).apply {
                    if (this == null){
                        //未找到该搜索内容,进行记录保存
                        Logger.i(TAG, "保存搜索记录:$content")
                        addHistory(History(content))
                    }else Logger.i(TAG, "该搜索记录已被保存,不再继续保存.")
                }
            }
        }.invokeOnCompletion {
            it?.printStackTrace()
            loadHistory(guideVM, context)
        }
    }

    /**
     * 清除历史搜索记录
     */
    fun clearHistory(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            Logger.i(TAG, "清除历史记录.")
            HistoryDatabase.getInstance(context).historyDao().clearHistory()
        }
    }

    /**
     * 发出猜你喜欢数据请求
     */
    fun guessLikeRequest(guideVM: GuideViewModel){
        ServiceBuilder.create(GuessLikeService::class.java)
            .getGuessLikeData().enqueue(object : Callback<GuessLike>{
                override fun onResponse(call: Call<GuessLike>, response: Response<GuessLike>) {
                    response.body()?.apply {
                        Logger.i(TAG, "猜你喜欢请求:$this")
                        guideVM.guessLikeData.value = this
                    }
                }
                override fun onFailure(call: Call<GuessLike>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    /**
     * 发出热搜榜数据请求
     */
    fun hotSearchRequest(guideVM: GuideViewModel){
        ServiceBuilder.create(HotSearchService::class.java)
            .getHotSearchData().enqueue(object : Callback<HotSearch>{
                override fun onResponse(call: Call<HotSearch>, response: Response<HotSearch>) {
                    response.body()?.apply {
                        Logger.i(TAG, "热搜榜请求:$this")
                        guideVM.hotSearchData.value = this
                    }
                }
                override fun onFailure(call: Call<HotSearch>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    /**
     * 发出热门歌手榜数据请求
     */
    fun hotArtistsRequest(guideVM: GuideViewModel){
        ServiceBuilder.create(HotArtistService::class.java)
            .getHotArtistsData().enqueue(object : Callback<HotArtist>{
                override fun onResponse(call: Call<HotArtist>, response: Response<HotArtist>) {
                    response.body()?.apply {
                        Logger.i(TAG, "热门歌手请求:$this")
                        guideVM.hotArtistData.value = this
                    }
                }
                override fun onFailure(call: Call<HotArtist>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

}