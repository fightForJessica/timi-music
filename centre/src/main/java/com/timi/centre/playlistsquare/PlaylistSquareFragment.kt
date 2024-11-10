package com.timi.centre.playlistsquare

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.timi.centre.BaseFragment
import com.timi.centre.CentreActivity
import com.timi.centre.R
import com.timi.centre.databinding.FragmentPlaylistSquareBinding
import com.timi.centre.playlistsquare.PlaylistSquareViewModel
import com.timi.centre.playlistsquare.adapter.SquarePlaylistAdapter
import com.timi.centre.playlistsquare.bean.Playlists
import com.timi.centre.playlistsquare.room.CategoryTag
import com.timi.utils.Logger
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaylistSquareFragment : BaseFragment() {

	override val TAG: String = "PlaylistSquareFragment"
	private var lastView: View? = null
	private var viewInit = false

	private lateinit var binding: FragmentPlaylistSquareBinding
	private lateinit var navController: NavController
	private val playlistSquareViewModel by lazy {
		ViewModelProvider(this)[PlaylistSquareViewModel::class.java]
	}

	private lateinit var categorySelectedTagList: List<CategoryTag>
	private lateinit var playlist: List<Playlists>
	private lateinit var playlistAdapter: SquarePlaylistAdapter
	private var lastTabPosition = 0
	// 信息加载/请求完成的标志变量
	private var playlistRequest = false
	private var playlistShow = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		if (lastView == null){
			binding = FragmentPlaylistSquareBinding.inflate(inflater, container, false)
			lastView = binding.root
		}
		return lastView!!
	}

	override fun onDestroy() {
		super.onDestroy()
		CentreActivity.showTab()
	}

	override fun viewCreate(view: View, savedInstanceState: Bundle?) {
		navController = Navigation.findNavController(view)

		if (!viewInit){
			viewInit = true
			binding.apply {
				(activity as AppCompatActivity).apply {
					setSupportActionBar(toolbarPlaylistSquareFragment)
					supportActionBar?.title = "歌单广场"
					supportActionBar?.setDisplayHomeAsUpEnabled(true)
				}
				ivPlaylistSquareFragmentCategory.setImageResource(R.drawable.ic_playlistsquare_more)
				ivPlaylistSquareFragmentCategory.setOnClickListener {
					navController.navigate(R.id.action_playlistSquareFragment_to_categoryFragment)
				}
				playlistSquareViewModel.requestPlaylistData("推荐")	//默认进入时第一栏目为"推荐"
			}
		}
		CoroutineScope(Dispatchers.Main).launch {
			//每次进入页面都刷新tab
			playlistSquareViewModel.requestCategoryTagDate(requireContext())
		}.invokeOnCompletion {
			it?.printStackTrace()
			Logger.i(TAG, "完成请求发送.")
		}.dispose()
	}

	@Deprecated("Deprecated in Java")
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home){
			lastView = null
			viewInit = false
			playlistRequest = false
			playlistShow = false
			navController.popBackStack()
		}
		return super.onOptionsItemSelected(item)
	}

	override fun observeChange() {
		playlistSquareViewModel.apply {
			categorySelectedTagData.observe(viewLifecycleOwner){
				it?.let {
					if (it.isNotEmpty()) {
						Logger.i(TAG, "歌单分类加载成功.")
						if (::categorySelectedTagList.isInitialized && categorySelectedTagList == it) {
							Logger.i(TAG, "标签没有改变，不需要重置tab.")
							categorySelectedTagData.value = null
							return@observe
						}
						categorySelectedTagList = it
					}
					showTabs()
					categorySelectedTagData.value = null
				}
			}

			playlistData.observe(viewLifecycleOwner){
				it?.apply {
					if (this.code == 200){
						Logger.i(TAG, "歌单信息请求成功.")
						playlist = this.playlists
						playlistRequest = true
						showPlaylist()
					}
					playlistData.value = null
				}
			}
		}
	}

	/**
	 * 创建/刷新选项卡
	 */
	private fun showTabs(){
		binding.tabPlaylistSquareFragmentCategory.apply {
			removeAllTabs()
			clearOnTabSelectedListeners()
			var tab :TabLayout.Tab
			val tagNameList = listOf("推荐", "官方", "精品")
			//将默认分类以及自选的分类加到tab上
			for (tag in tagNameList) {
				tab = newTab()
				tab.text = tag
				addTab(tab)
			}
			if (::categorySelectedTagList.isInitialized){
				for (categoryTag in categorySelectedTagList) {
					tab = newTab()
					tab.text = categoryTag.name
					addTab(tab)
				}
			}

			//返回上一次所在的tab位置
			getTabAt(lastTabPosition)?.let {
				selectTab(it)
			} ?:{
				lastTabPosition--
				//如果上一次的tab标签被移除，则选取当前最后一个tab
				var lastTab = getTabAt(lastTabPosition)
				while (lastTab == null){
					lastTabPosition--
					lastTab = getTabAt(lastTabPosition)
				}
				selectTab(lastTab)
				playlistSquareViewModel.requestPlaylistData(lastTab.text.toString())
			}

			addOnTabSelectedListener(object : OnTabSelectedListener{
				override fun onTabSelected(tab: TabLayout.Tab?) {
					//选择对应tab后，进行歌单信息请求并刷新页面
					playlistSquareViewModel.requestPlaylistData(tab!!.text.toString())
					lastTabPosition = tab.position
				}
				override fun onTabUnselected(tab: TabLayout.Tab?) {}
				override fun onTabReselected(tab: TabLayout.Tab?) {}
			})
		}
	}

	/**
	 * 创建/刷新展示歌单的recyclerView
	 */
	@SuppressLint("NotifyDataSetChanged")
	private fun showPlaylist(){
		if (playlistRequest){
			//如果recyclerView未创建，则先创建；否则则直接刷新数据
			if (!playlistShow){
				playlistShow = true
				playlistAdapter = SquarePlaylistAdapter(playlist, this){ playlistId ->
					//onItemClicked
					navController.navigate(R.id.action_playlistSquareFragment_to_playlistFragment, Bundle().apply {
						putLong("playlistId", playlistId)
						putString("from", TAG)
					})
				}
				binding.rvPlaylistSquareFragmentPlaylist.apply {
					layoutManager = GridLayoutManager(requireContext(), 3)
					adapter = playlistAdapter
				}
			}else{
				binding.rvPlaylistSquareFragmentPlaylist.smoothScrollToPosition(0)
				playlistAdapter.playlist = playlist
				playlistAdapter.notifyDataSetChanged()
			}
		}else{
			Logger.i(TAG, "数据未加载完成，歌单展示失败.")
		}
	}

}