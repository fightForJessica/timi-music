package com.timi.centre.playlistsquare

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.centre.playlistsquare.bean.SquarePlaylistDetail
import com.timi.centre.playlistsquare.room.CategoryTag

class PlaylistSquareViewModel : ViewModel() {

	private val playlistSquareModel by lazy { PlaylistSquareModel() }

	val categorySelectedTagData = MutableLiveData<List<CategoryTag>?>()
	val playlistData = MutableLiveData<SquarePlaylistDetail?>()

	init {
		categorySelectedTagData.value = null
		playlistData.value = null
	}

	/**
	 * 发出歌单分类请求
	 */
	suspend fun requestCategoryTagDate(context: Context){
		playlistSquareModel.requestCategoryTagDate(this, context)
	}

	/**
	 * 根据对应标签请求歌单信息
	 * @param cat tag:标签信息
	 */
	fun requestPlaylistData(cat: String){
		playlistSquareModel.requestPlaylistData(this, cat)
	}

}