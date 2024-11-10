package com.timi.centre.playlist.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaylistDetailService {

    /**
     * @param id 歌单的id
     */
    @GET(WebConstant.General.API_PLAYLIST_DETAIL)
    fun getPlayListData(@Query("id") id: Long): Call<PlaylistDetail>
}