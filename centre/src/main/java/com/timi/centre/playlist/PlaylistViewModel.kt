package com.timi.centre.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.centre.playlist.bean.PlaylistDetail

class PlaylistViewModel : ViewModel(){

    private val TAG = "RecommandPlayViewModel"
    private val playlistModel: PlaylistModel by lazy { PlaylistModel() }

    val playListData = MutableLiveData<PlaylistDetail?>()

    init {
        playListData.value = null
    }

    /**
     * 发出歌单详情请求
     * @param playlistId 歌单id
     */
    fun playlistDetailRequest(playlistId: Long){
        playlistModel.playlistDetailRequest(this, playlistId)
    }

}