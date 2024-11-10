package com.timi.centre.playlist

import com.timi.centre.playlist.bean.PlaylistDetail
import com.timi.centre.playlist.bean.PlaylistDetailService
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistModel {

    private val TAG = "RecommandPlayModel"

    /**
     * 发出歌单详情请求
     * @param playlistId 歌单id
     */
    fun playlistDetailRequest(plModel: PlaylistViewModel, playlistId: Long){
        ServiceBuilder.create(PlaylistDetailService::class.java)
            .getPlayListData(playlistId).enqueue(object : Callback<PlaylistDetail>{
                override fun onResponse(call: Call<PlaylistDetail>, response: Response<PlaylistDetail>) {
                    response.body().apply {
                        Logger.i(TAG, "歌单详情:$this")
                        plModel.playListData.value = this
                    }
                }
                override fun onFailure(call: Call<PlaylistDetail>, t: Throwable) {
                    t.printStackTrace()
                }

            })
    }



}