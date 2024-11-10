package com.timi.centre.playlistsquare.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface PlaylistCategoryService {

	@GET(WebConstant.PlaylistSquare.API_PLAYLIST_CATEGORY)
	fun getPlaylistCategoryData(): Call<PlaylistCategory>

}