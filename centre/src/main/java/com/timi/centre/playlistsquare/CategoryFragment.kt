package com.timi.centre.playlistsquare

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.centre.databinding.FragmentCategoryBinding
import com.timi.centre.playlistsquare.adapter.ChooseCategoryAdapter
import com.timi.centre.playlistsquare.room.CategoryDatabase
import com.timi.centre.playlistsquare.room.CategoryTag
import com.timi.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryFragment : Fragment() {

	private val TAG = "CategoryFragment"

	private lateinit var binding: FragmentCategoryBinding
	private lateinit var navController: NavController

	private lateinit var categoryAdapter: ChooseCategoryAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentCategoryBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		navController = Navigation.findNavController(view)

		(activity as AppCompatActivity).apply {
			setSupportActionBar(binding.toolbarCategoryFragment)
			supportActionBar?.title = "歌单标签"
			supportActionBar?.setDisplayHomeAsUpEnabled(true)
		}

		showCategory()
	}

	@Deprecated("Deprecated in Java")
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home){
			navController.popBackStack()
		}
		return super.onOptionsItemSelected(item)
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun showCategory(){
		var selectedTags: List<CategoryTag> = emptyList()
		var languageTags: List<CategoryTag> = emptyList()
		var styleTags: List<CategoryTag> = emptyList()
		var sceneTags: List<CategoryTag> = emptyList()
		var emotionTags: List<CategoryTag> = emptyList()
		var topicTags: List<CategoryTag> = emptyList()
		CoroutineScope(Dispatchers.Main).launch {
			withContext(Dispatchers.IO){
				CategoryDatabase.getInstance(requireContext()).categoryDao().apply{
					selectedTags = getSelectedTypeData(true)
					languageTags = getTagTypeData("语种")
					styleTags = getTagTypeData("风格")
					sceneTags = getTagTypeData("场景")
					emotionTags = getTagTypeData("情感")
					topicTags = getTagTypeData("主题")
				}
			}
		}.invokeOnCompletion {
			Logger.i(TAG, "数据读取完成.")

			categoryAdapter = ChooseCategoryAdapter(
				selectedTags, languageTags, styleTags,
				sceneTags, emotionTags, topicTags,{ categoryTag ->
					//onCategoryClicked
					//处于编辑模式时，点击卡片切换挑选状态
					if (categoryAdapter.categorySelecting){
						when(categoryTag.name){
							"官方", "精品", "推荐" -> {}	//默认标签无法更改
							else ->{
								//修改数据库中该标签的信息
								categoryTag.hasSelect = !categoryTag.hasSelect
								CoroutineScope(Dispatchers.Main).launch {
									withContext(Dispatchers.IO){
										CategoryDatabase.getInstance(requireContext()).categoryDao().updateCategoryData(categoryTag)
									}
								}.invokeOnCompletion { throwable ->
									throwable?.printStackTrace()
									CoroutineScope(Dispatchers.Main).launch{
										//修改暂存的集合信息，并刷新adapter
										withContext(Dispatchers.IO){
											CategoryDatabase.getInstance(requireContext()).categoryDao().apply {
												when(categoryTag.name){
													"语种" -> categoryAdapter.languageTags = getTagTypeData("语种")
													"风格" -> categoryAdapter.styleTags = getTagTypeData("风格")
													"场景" -> categoryAdapter.sceneTags = getTagTypeData("场景")
													"情感" -> categoryAdapter.emotionTags = getTagTypeData("情感")
													"主题" -> categoryAdapter.topicTags = getTagTypeData("主题")
												}
												categoryAdapter.selectedTags = getSelectedTypeData(true)
											}
										}
									}.invokeOnCompletion {
										it?.printStackTrace()
										categoryAdapter.notifyDataSetChanged()
									}
								}
							}
						}
					}
				},{
					//onEditClicked
					//卡片编辑模式关闭时，点击打开
					categoryAdapter.categorySelecting = !categoryAdapter.categorySelecting
					categoryAdapter.notifyDataSetChanged()
				})
			binding.rvCategoryFragment.apply {
				layoutManager = LinearLayoutManager(requireContext())
				adapter = categoryAdapter
			}
		}
	}
}