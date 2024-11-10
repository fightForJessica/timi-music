package com.timi.centre.found

import com.timi.centre.found.bean.BannerObject
import com.timi.centre.found.bean.BannerService
import com.timi.centre.found.bean.RecommandPlay
import com.timi.centre.found.bean.RecommandPlayService
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoundModel {

    private val TAG = "FoundModel"

    /**
     * 发出轮播图数据请求
     */
    fun bannerRequest(foundVM: FoundViewModel){
        ServiceBuilder.create(BannerService::class.java)
            .getBannerData().enqueue(object: Callback<BannerObject>{
                override fun onResponse(call: Call<BannerObject>, response: Response<BannerObject>) {
                    response.body()?.apply {
                        Logger.i(TAG, "轮播图请求:$this")
                        foundVM.bannerData.value = this
                    }
                }
                override fun onFailure(call: Call<BannerObject>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    /**
     * 发出推荐歌单数据请求
     */
    fun recommandPlayRequest(foundVM: FoundViewModel){
        ServiceBuilder.create(RecommandPlayService::class.java)
            .getRecommandPlayData().enqueue(object: Callback<RecommandPlay>{
                override fun onResponse(call: Call<RecommandPlay>, response: Response<RecommandPlay>) {
                    response.body().apply {
                        Logger.i(TAG, "推荐歌单请求:$this")
                        foundVM.recommandPlayData.value = this
                    }
                }
                override fun onFailure(call: Call<RecommandPlay>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
}