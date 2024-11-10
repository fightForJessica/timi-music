package com.timi.centre.playlistsquare

import android.content.Context
import com.timi.centre.playlistsquare.PlaylistSquareViewModel
import com.timi.centre.playlistsquare.bean.PlaylistCategory
import com.timi.centre.playlistsquare.bean.PlaylistCategoryService
import com.timi.centre.playlistsquare.bean.SquarePlaylistDetail
import com.timi.centre.playlistsquare.bean.SquarePlaylistDetailService
import com.timi.centre.playlistsquare.room.CategoryDatabase
import com.timi.centre.playlistsquare.room.CategoryTag
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistSquareModel {

	private val TAG = "PlaylistSquareModel"

	/**
	 * 从数据库中读取歌单分类信息，如果没有则发出歌单分类请求
	 * (理论上程序只会在安装程序未使用过时请求一次并写入数据库，后续将全部从数据库中读取)
	 */
	suspend fun requestCategoryTagDate(viewModel: PlaylistSquareViewModel, context: Context){
		withContext(Dispatchers.IO){
			//如果数据库已经有过歌单分类信息，则不再需要进行网络请求
			if (CategoryDatabase.getInstance(context).categoryDao().getAllCategoryData().isNotEmpty()){
				//如果有信息，则读取已经被选择的标签
				Logger.i(TAG, "从数据库读取已被选择歌单分类.")
				val selectedList = CategoryDatabase.getInstance(context).categoryDao().getSelectedTypeData(true)
				viewModel.categorySelectedTagData.postValue(selectedList)
			}else{
				withContext(Dispatchers.Main){
					ServiceBuilder.create(PlaylistCategoryService::class.java)
						.getPlaylistCategoryData().enqueue(object : Callback<PlaylistCategory>{
							override fun onResponse(call: Call<PlaylistCategory>, response: Response<PlaylistCategory>) {
								response.body()?.apply {
									Logger.i(TAG, "歌单分类请求:$this")
									val categoryTagList = mutableListOf<CategoryTag>()
									for (tag in this.sub) {
										categoryTagList.add(
											CategoryTag(tag.name,
											when(tag.category){
												0 -> "语种"
												1 -> "风格"
												2 -> "场景"
												3 -> "情感"
												else -> "主题"	//4
											},  false, tag.hot)
										)
									}
									//提取分类信息后，赋值给viewModel中的LifeData变量，并存入数据库
									CoroutineScope(Dispatchers.IO).launch {
										CategoryDatabase.getInstance(context).categoryDao().addCategoryData(categoryTagList)
										Logger.i(TAG, "歌单分类信息写入数据库.")
									}
									viewModel.categorySelectedTagData.value = categoryTagList
								}
							}
							override fun onFailure(call: Call<PlaylistCategory>, t: Throwable) {
								t.printStackTrace()
							}
						})
				}
			}
		}
	}

	/**
	 * 根据对应标签请求歌单信息
	 * @param cat tag:标签信息
	 */
	fun requestPlaylistData(viewModel: PlaylistSquareViewModel, cat: String){
		ServiceBuilder.create(SquarePlaylistDetailService::class.java)
			.getSquarePlaylistDetailData(cat).enqueue(object : Callback<SquarePlaylistDetail>{
				override fun onResponse(call: Call<SquarePlaylistDetail>, response: Response<SquarePlaylistDetail>) {
					response.body()?.apply {
						Logger.i(TAG, "$cat(标签)的歌单请求:$this")
						viewModel.playlistData.value = this
					}
				}
				override fun onFailure(call: Call<SquarePlaylistDetail>, t: Throwable) {
					t.printStackTrace()
				}
			})
	}

}