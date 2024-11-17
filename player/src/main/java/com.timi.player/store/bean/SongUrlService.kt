package com.timi.player.store.bean


import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongUrlService {

    /**
     * 通过歌曲 id 获取歌曲播放 Url
     * @param id 歌曲 id
     */
    @GET(WebConstant.Song.API_SONG_URL)
    fun getSongUrlData(@Query("id") id: Long): Call<SongUrl>
}