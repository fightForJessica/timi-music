package com.timi.centre.search

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.centre.BaseFragment
import com.timi.centre.CentreActivity
import com.timi.centre.R
import com.timi.centre.databinding.FragmentGuideBinding
import com.timi.centre.search.GuideViewModel
import com.timi.centre.search.adapter.GuideOverAllAdapter
import com.timi.centre.search.bean.Artist
import com.timi.centre.search.bean.Guess
import com.timi.centre.search.bean.History
import com.timi.centre.search.bean.HotSearchData
import com.timi.utils.Logger
import com.timi.utils.isClickEffective
import com.timi.utils.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuideFragment : BaseFragment() {

    override val TAG = "GuideFragment"
    private var lastView: View? = null
    private var viewInit = false

    private lateinit var binding: FragmentGuideBinding
    private lateinit var navController: NavController

    private val guideViewModel: GuideViewModel by lazy {
        ViewModelProvider(this)[GuideViewModel::class.java]
    }

    private lateinit var historySearch: List<History>           //历史搜素记录
    private lateinit var guessList: List<Guess>                 //猜你喜欢
    private lateinit var hotSearchList: List<HotSearchData>     //热搜榜
    private lateinit var hotArtistList: List<Artist>            //热门歌手榜
    //所需数据是否请求完成的标志变量
    private var historyLoad = false
    private var guessRequest = false
    private var hotSearchRequest = false
    private var hotArtistRequest = false
    private var showing = false     //导航recyclerView是否创建

    private lateinit var guideAdapter: GuideOverAllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) //让 fragment 中 toolbar 中按钮响应 onOptionItemSelected
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (lastView == null) {
            binding = FragmentGuideBinding.inflate(inflater, container, false)
            lastView = binding.root
        }
        return lastView!!
    }

    override fun viewCreate(view: View, savedInstanceState: Bundle?) {
        if (!viewInit){
            navController = Navigation.findNavController(view)

            //初始化布局
            binding.apply {

                searchToolbarGuideFragment.apply {
                    initToolbar(activity as AppCompatActivity)
                    tv_search.setOnClickListener {
                        if (isClickEffective()){
                            search(edt_inputBox.text.toString())
                        }
                    }
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                guideViewModel.apply {
                    loadHistory(requireContext())//加载数据库中的历史数据
                    guessLikeRequest()//请求猜你喜欢数据
                    hotSearchRequest()//请求热搜榜数据
                    hotArtistsRequest()//请求热门歌手榜数据
                }
            }.invokeOnCompletion {
                it?.printStackTrace()
                Logger.i(TAG, "完成请求发送.")
            }.dispose()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            navController.popBackStack()
            lastView = null
            viewInit = false
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeChange() {
        guideViewModel.apply {
            //历史搜索响应
            historyData.observe(viewLifecycleOwner){
                it?.apply {
                    historyLoad = true
                    Logger.i(TAG, "历史搜索记录读取成功.")
                    if (this.isNotEmpty()){
                        historySearch = this
                    }else {
                        historySearch = emptyList()
                        Logger.w(TAG, "历史搜索记录为空.")
                    }
                    //第一次接到数据后用于创建recyclerView
                    if (!showing){
                        showGuide()
                    }else{
                        //后来接到的数据用于刷新recyclerView
                        guideAdapter.history = this
                        guideAdapter.notifyDataSetChanged()
                    }
                    historyData.value = null
                }
            }
            //猜你喜欢响应
            guessLikeData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "猜你喜欢请求成功.")
                        if (this.result.hots != null){
                            guessList = this.result.hots
                            guessRequest = true
                            showGuide()
                        }else Logger.w(TAG, "请求的猜你喜欢结果为空.")
                    }else Logger.w(TAG, "猜你喜欢请求失败.")
                    guessLikeData.value = null
                }
            }
            //热搜榜响应
            hotSearchData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "热搜榜请求成功.")
                        if (this.data != null){
                            hotSearchList = this.data
                            hotSearchRequest = true
                            showGuide()
                        }else Logger.w(TAG, "请求的热搜榜为空.")
                    }else Logger.w(TAG, "热搜榜请求失败.")
                    hotSearchData.value = null
                }
            }
            //热门歌手榜响应
            hotArtistData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "热门歌手榜请求成功.")
                        if (this.artists != null){
                            hotArtistList = this.artists
                            hotArtistRequest = true
                            showGuide()
                        }else Logger.w(TAG, "请求的热门歌手榜为空.")
                    }else Logger.w(TAG, "热门歌手榜请求失败.")
                    hotArtistData.value = null
                }
            }
        }
    }

    /**
     * 当所有数据请求完毕后，创建导航recyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun showGuide(){
        //判断数据是否请求完毕
        if (historyLoad && guessRequest && hotSearchRequest && hotArtistRequest){
            showing = true
            Logger.i(TAG, "数据初始化完成,创建导航recyclerView.")
            binding.rvGuideFragmentOverAll.apply {
                layoutManager = LinearLayoutManager(context)
                guideAdapter = GuideOverAllAdapter(
                    requireContext(), historySearch, guessList, hotSearchList, hotArtistList, { cardText ->
                        //onTextCardClicked
                        if (isClickEffective()) search(cardText)
                    },{
                        //onClearItemClicked
                        if (isClickEffective()){
                            guideAdapter.history = emptyList()
                            guideAdapter.notifyDataSetChanged()
                            guideViewModel.clearHistory(requireContext())
                        }
                    },{ columnContent ->
                        //onColumnItemClicked
                        if (isClickEffective()) search(columnContent)
                    })
                adapter = guideAdapter
            }
        }else{
            Logger.i(TAG, "数据未初始化完成,导航recyclerView创建失败.")
        }
    }

    /**
     * @param content 搜索内容
     */
    private fun search(content: String){
        if (content.isNotEmpty()){
            //发出搜索请求,并将搜索词汇添加到数据库中
            binding.searchToolbarGuideFragment.edt_inputBox.setText(content)
            guideViewModel.storeHistory(content, requireContext())

            //跳转至ResultFragment进行搜索
            navController.navigate(R.id.action_guideFragment_to_resultFragment, Bundle().apply {
                putString("searchContent", content)
            })
        }else{
            makeToast("请先输入搜索内容!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CentreActivity.showTab()
    }
}