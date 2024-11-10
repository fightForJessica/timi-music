package com.timi.centre.found

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.centre.found.bean.BannerObject
import com.timi.centre.found.bean.RecommandPlay

class FoundViewModel: ViewModel() {

    private val TAG = "FoundViewModel"
    private val foundModel: FoundModel by lazy { FoundModel() }

    val bannerData = MutableLiveData<BannerObject?>()          //轮播图返回结果
    val recommandPlayData = MutableLiveData<RecommandPlay?>()

    init {
        bannerData.value = null
        recommandPlayData.value = null
    }

    /**
     * 发出轮播图请求
     */
    fun bannerRequest(){
        foundModel.bannerRequest(this)
    }

    /**
     * 发出推荐歌单请求
     */
    fun recommandPlayRequest(){
        foundModel.recommandPlayRequest(this)
    }
}