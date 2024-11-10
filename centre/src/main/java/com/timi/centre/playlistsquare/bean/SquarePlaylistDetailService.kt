package com.timi.centre.playlistsquare.bean

import com.timi.utils.WebConstant
import com.timi.centre.playlistsquare.bean.SquarePlaylistDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SquarePlaylistDetailService {

	/**
	 * 获取歌单标签标签的相应内容
	 * @param cat tag:歌单标签
	 */
	@GET(WebConstant.PlaylistSquare.API_PLAYLIST_CATEGORY_DETAIL)
	fun getSquarePlaylistDetailData(@Query("cat") cat: String): Call<SquarePlaylistDetail>

	/**
	 * 获取歌单标签标签的相应内容
	 * @param cat tag:歌单标签
	 * @param before 分页参数,取上一页最后一个歌单的 updateTime
	 * @param limit 取出数量，默认为20
	 */
	@GET(WebConstant.PlaylistSquare.API_PLAYLIST_CATEGORY_DETAIL)
	fun getSquarePlaylistDetailData(
		@Query("cat") cat: String,
		@Query("before") before: Long,
		@Query("limit") limit: Int = 20
	): Call<SquarePlaylistDetail>

}