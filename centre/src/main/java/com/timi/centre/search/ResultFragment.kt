package com.timi.centre.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.BaseFragment
import com.timi.centre.databinding.FragmentResultBinding
import com.timi.centre.search.adapter.SearchResultAdapter
import com.timi.centre.search.bean.Song
import com.timi.player.MusicActivity
import com.timi.player.SongUtils
import com.timi.utils.Logger
import com.timi.utils.hideKeyboard
import com.timi.utils.isClickEffective
import com.timi.utils.makeToast

class ResultFragment : BaseFragment() {

    private companion object{
        private const val OFFSET_DEFAULT = 0        //默认偏移量
        private const val OFFSET_INCREMENT = 15     //偏移量单次增量
    }

    private var viewInit = false
    private var lastView: View? = null

    override val TAG = "ResultFragment"
    private lateinit var binding: FragmentResultBinding
    private lateinit var navController: NavController

    private val resultViewModel: ResultViewModel by lazy {
        ViewModelProvider(this)[ResultViewModel::class.java]
    }

    private lateinit var songList: MutableList<Song>        //歌曲信息
    private var showing = false     //搜索结果recyclerView是否创建

    private lateinit var resultAdapter: SearchResultAdapter

    private lateinit var searchContent: String  //搜索内容
    private var offset = OFFSET_DEFAULT         //搜索偏移量
    private var hasMoreResult = true            //是否还有更多搜索内容

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireArguments().getString("searchContent", "").apply {
            Logger.i(TAG, "搜索内容:$this")
            searchContent = this
            resultViewModel.searchRequest(this, offset)
        }

    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (lastView == null){
            binding = FragmentResultBinding.inflate(inflater, container, false)
            lastView = binding.root
        }
        return lastView!!
    }

    override fun viewCreate(view: View, savedInstanceState: Bundle?) {
        if (!viewInit){
            viewInit = true
            navController = Navigation.findNavController(view)
            binding.apply {

                searchToolbarResultFragment.apply {
                    initToolbar(activity as AppCompatActivity)

                    //设置"搜索"TextView点击事件
                    setSearchTextOnClickListener{
                       if (isClickEffective()){
                           //EditText中内容为空或与上一次搜索内容一致时不执行搜索
                           if (edt_inputBox.text.toString().isNotEmpty()){
                               hideKeyboard()
                               if (searchContent != edt_inputBox.text.toString()){
                                   //搜索新内容时重置页数
                                   offset = OFFSET_DEFAULT
                                   hasMoreResult = true
                                   searchContent = edt_inputBox.text.toString()
                                   resultViewModel.searchRequest(searchContent, offset)
                               }
                           }else{
                               makeToast("搜索内容不能为空！")
                           }
                       }
                    }

                    //设置EditText默认文本
                    edt_inputBox.setText(searchContent)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            navController.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeChange() {
        resultViewModel.apply {

            //搜索结果
            searchData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "搜索成功.")
                        hasMoreResult = this.result.hasMore
                        if (this.result.songs != null){
                            if (!showing){
                                songList = this.result.songs.toMutableList()
                                //如果是第一次加载搜索结果,创建recyclerView
                                showing = true
                                showResult()
                            }else{
                                //如果搜索内容没有改变,则判断为分页加载操作
                                if (offset != OFFSET_DEFAULT){
                                    songList.addAll(this.result.songs)
                                    resultAdapter.songList = songList
                                    resultAdapter.notifyItemRangeChanged(songList.size - 15, 15)
                                }else{
                                    //搜索内容改变则刷新所有数据
                                    songList.clear()
                                    songList = this.result.songs.toMutableList()
                                    resultAdapter.songList = songList
                                    resultAdapter.notifyDataSetChanged()
                                }
                            }
                        }else Logger.w(TAG, "搜索的歌曲为空.")
                    }else Logger.w(TAG, "搜索失败.")
                    searchData.value = null
                }
            }

        }
    }

    /**
     * 展示搜索结果
     */
    private fun showResult(){
        binding.rvResultSearchResult.apply {
            layoutManager = LinearLayoutManager(context)
            resultAdapter = SearchResultAdapter(songList, { songId, songName, arAndDes, picUrl ->
                //onResultItemClicked
                if (isClickEffective()){
                    //将点击播放的歌曲信息先写入数据库，完成后启动MusicActivity
                    SongUtils.storeSongData(requireContext(), com.timi.player.store.bean.Song(songId, songName, arAndDes, picUrl)) {
                        startActivity(Intent(context, MusicActivity::class.java))
                    }
                }
            }, {
                //onItemMoreClicked

            })
            adapter = resultAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    var lastPosition = -1
                    if (newState == RecyclerView.SCROLL_STATE_IDLE){
                        recyclerView.layoutManager.apply {
                            if (this is LinearLayoutManager)
                                lastPosition = this.findLastVisibleItemPosition()
                            if (lastPosition == this!!.itemCount - 1){
                                if (hasMoreResult){
                                    offset += OFFSET_INCREMENT
                                    Logger.i(TAG, "进行翻页加载,目前offset=$offset.")
                                    resultViewModel.searchRequest(searchContent, offset)
                                }else {
                                    makeToast("已经到底了哦~~~")
                                }
                            }
                        }
                    }
                }
            })

        }
    }

}
