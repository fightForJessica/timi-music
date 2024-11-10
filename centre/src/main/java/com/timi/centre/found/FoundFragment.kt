package com.timi.centre.found

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.BaseFragment
import com.example.centre.CentreActivity
import com.timi.centre.R
import com.timi.centre.found.adapter.FoundOverAllAdapter
import com.timi.centre.found.bean.Banner
import com.timi.centre.found.bean.RecommandPlayResult
import com.timi.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class FoundFragment : BaseFragment() {

    override val TAG = "FoundFragment"
    private var viewInit = false            //标志该 fragment是否被创建初始化
    private var lastView: View? = null      //标志该 fragment 是否被创建过，防止 fragment 重建再次请求

    private lateinit var binding: FragmentFoundBinding
    private lateinit var navController: NavController
    private val foundViewModel: FoundViewModel by lazy {
        ViewModelProvider(this)[FoundViewModel::class.java]
    }

    companion object{
        private const val BANNER_SCROLL = 0
    }

    private val handler = Handler(Looper.getMainLooper()){ msg ->
        if (msg.what == BANNER_SCROLL) bannerScroll()
        true
    }

    private lateinit var bannerList: List<Banner>                       //轮播图
    private lateinit var recommandPlayList: List<RecommandPlayResult>   //退浆但
    //所需数据是否请求完成的标志变量
    private var bannerRequest = false
    private var recommandRequest = false
    private var bannerTouch = false
    private lateinit var banner: RecyclerView
    private var bannerPosition by Delegates.notNull<Int>()
    private lateinit var cookie: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            cookie = getString("cookie").toString()
            Logger.i(TAG, "cookie=$cookie")
        }
    }
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (lastView == null){
            binding = FragmentFoundBinding.inflate(inflater, container, false)
            lastView = binding.root
        }
        return lastView!!
    }

    override fun onResume() {
        super.onResume()
        if (bannerRequest && recommandRequest){
            handler.removeCallbacksAndMessages(null)
            handler.sendEmptyMessageDelayed(BANNER_SCROLL, 500L)
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun viewCreate(view: View, savedInstanceState: Bundle?) {
        if (!viewInit){
            viewInit = true
            navController = Navigation.findNavController(view)
            binding.apply {
                searchToolbarFoundFragment.apply {
                    initToolbar(activity as AppCompatActivity){ activity ->
                        edt_inputBox.visibility = View.GONE
                        sidewaysFoundFragment.setExitButton {
                            if (isClickEffective()){
                                lastView = null
                                viewInit = false
                                activity.finish()
                            }
                        }
                    }

                    //布置侧滑布局
                    val toggle = ActionBarDrawerToggle(
                        activity,
                        drawerLayoutFoundFragment,
                        toolbar,
                        R.string.main_open,
                        R.string.main_close
                    )
                    toggle.syncState()
                    drawerLayoutFoundFragment.setDrawerListener(toggle)

                    //搜索框点击事件
                    setLayoutOnClickListener{
                        if (isClickEffective()){
                            CentreActivity.hideTab()
                            navController.navigate(R.id.action_foundFragment_to_guideFragment)
                        }
                    }
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                foundViewModel.bannerRequest()
                foundViewModel.recommandPlayRequest()
            }.invokeOnCompletion {
                it?.printStackTrace()
                Logger.i(TAG, "完成请求发送.")
            }.dispose()
        }
    }

    override fun observeChange() {
        foundViewModel.apply {
            //轮播图响应
            bannerData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "轮播图请求成功.")
                        if (this.banners != null){
                            bannerList = this.banners
                            bannerRequest = true
                            showResponse()
                        }else Logger.w(TAG, "请求的轮播图结果为空.")
                    }else Logger.w(TAG, "轮播图请求失败.")
                    bannerData.value = null
                }
            }
            //推荐歌单响应
            recommandPlayData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "推荐歌单请求成功.")
                        if (this.result != null){
                            recommandPlayList = this.result
                            recommandRequest = true
                            showResponse()
                        }else Logger.w(TAG, "请求的推荐歌单数据为空.")
                    }else Logger.w(TAG, "推荐歌单请求失败.")
                    recommandPlayData.value = null
                }
            }

        }

    }

    /**
     * 当所有数据请求完毕后，创建发现页recyclerView
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showResponse(){
        //判断数据是否请求完毕
        if (bannerRequest && recommandRequest){
            Logger.i(TAG, "数据初始化完成,创建发现页recyclerView.")
            bannerPosition = bannerList.size * 10
            binding.rvFoundFragmentOverAll.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = FoundOverAllAdapter(bannerList, recommandPlayList, this@FoundFragment, { recyclerView ->
                    //bannerScroll
                    banner = recyclerView
                    banner.setOnTouchListener { _, event ->
                        bannerTouch = event?.action != MotionEvent.ACTION_UP
                        false
                    }
                },{
                    //onBannerClicked
                  

                },{ playlistId ->
                    //onRecommandClicked    跳转至歌单页面
                    if (isClickEffective()){
                        CentreActivity.hideTab()
                        navController.navigate(R.id.action_foundFragment_to_playlistFragment, Bundle().apply {
                            putLong("playlistId", playlistId)
                            putString("from", TAG)
                        })
                    }
                }, {
                    //onRecommandTextClicked
                    CentreActivity.hideTab()
                    navController.navigate(R.id.action_foundFragment_to_playlistSquareFragment)
                })
            }
            handler.sendEmptyMessageDelayed(BANNER_SCROLL, 1000L)
        }else{
            Logger.i(TAG, "数据未初始化完成,发现页recyclerView创建失败.")
        }
    }

    /**
     * 控制轮播图滑动
     */
    private fun bannerScroll(){
        if (bannerRequest && !bannerTouch){
            bannerPosition++
            banner.smoothScrollToPosition(bannerPosition)
        }
        handler.sendEmptyMessageDelayed(BANNER_SCROLL, 5000L)
    }

}