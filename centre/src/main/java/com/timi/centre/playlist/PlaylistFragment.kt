package com.timi.centre.playlist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.timi.centre.BaseFragment
import com.timi.centre.CentreActivity
import com.timi.centre.R
import com.timi.centre.databinding.FragmentPlaylistBinding
import com.timi.centre.playlist.PlaylistViewModel
import com.timi.centre.playlist.adapter.SongListAdapter
import com.timi.centre.playlist.bean.Playlist
import com.timi.player.MusicActivity
import com.timi.player.SongUtils
import com.timi.player.store.bean.Song
import com.timi.utils.Logger
import com.timi.utils.getCompressBitmap
import com.timi.utils.isClickEffective
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class PlaylistFragment : BaseFragment() {

    override val TAG: String = "PlayListFragment"
    private var lastView: View? = null  //防止点入歌单封面时view重建
    private var viewInit = false

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var navController: NavController
    private val playListViewModel: PlaylistViewModel by lazy {
        ViewModelProvider(this)[PlaylistViewModel::class.java]
    }

    private lateinit var cookie: String
    private var playlistId by Delegates.notNull<Long>()
    private lateinit var navFrom: String

    private lateinit var playlist: Playlist
    //所需数据是否请求完成的标志变量
    private var playlistDetailRequest = false
    //view 是否被销毁的标志变量，防止 view 销毁后 Glide 的异步加载引发异常
    private var viewDestroy by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) //让 fragment 中 toolbar 的按钮响应 onOptionItemSelected
        arguments?.apply {
            cookie = getString("cookie").toString()
            playlistId = getLong("playlistId")
            navFrom = getString("from").toString()
            Logger.i(TAG, "cookie=$cookie")
            Logger.i(TAG, "playlistId=$playlistId")
        }
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (lastView == null){
            binding = FragmentPlaylistBinding.inflate(inflater, container, false)
            lastView = binding.root
        }
        return lastView!!
    }

    override fun viewCreate(view: View, savedInstanceState: Bundle?) {
        viewDestroy = false
        if (!viewInit){
            viewInit = true
            navController = Navigation.findNavController(view)

            initToolbar()

            binding.apply {
                ivPlaylistFragmentCover.setOnClickListener {
                    if (it.isClickEffective()){
                        if (playlistDetailRequest){
                            navController.navigate(R.id.action_playlistFragment_to_coverFragment, Bundle().apply {
                                putString("title", playlist.name)
                                putString("backgroundUrl", playlist.coverImgUrl)
                                putStringArrayList("label", ArrayList(playlist.tags))
                                putString("description", playlist.description)
                            })
                        }
                    }
                }
                //请求前的默认展示
                btnMorePlaylistFragmentForward.setIcon(R.drawable.ic_forward)
                btnMorePlaylistFragmentForward.setDefaultText("分享")
                btnMorePlaylistFragmentComments.setIcon(R.drawable.ic_comment)
                btnMorePlaylistFragmentComments.setDefaultText("评论")
                btnMorePlaylistFragmentCollection.setIcon(R.drawable.ic_collection)
                btnMorePlaylistFragmentCollection.setDefaultText("收藏")
            }

            CoroutineScope(Dispatchers.Main).launch {
                if (playlistId != (-1).toLong())
                    playListViewModel.playlistDetailRequest(playlistId)
                else
                    Logger.w(TAG, "歌单id未接收.")
            }.invokeOnCompletion {
                it?.printStackTrace()
                Logger.i(TAG, "完成请求发送.")
            }.dispose()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDestroy = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (navFrom == "FoundFragment"){
            CentreActivity.showTab()
        }
    }

    override fun observeChange() {
        playListViewModel.apply {
            //歌单详情
            playListData.observe(viewLifecycleOwner){
                it?.apply {
                    if (this.code == 200){
                        Logger.i(TAG, "歌单详情请求成功.")
                        if (this.playlist != null){
                            this@PlaylistFragment.playlist = this.playlist
                            playlistDetailRequest = true
                            showResponse()
                        }else
                            Logger.i(TAG, "请求的歌单详情为空.")
                    }else
                        Logger.i(TAG, "歌单详情请求失败")
                    playListData.value = null
                }
            }
        }
    }

    private fun initToolbar(){
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarPlaylistFragment)
            supportActionBar?.apply {
                title = "歌单"
                setDisplayHomeAsUpEnabled(true)
            }
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

    /**
     * 当所有数据请求完毕后，创建推荐歌单的内容
     */
    private fun showResponse(){
        if (playlistDetailRequest){
            binding.apply {
                Picasso.get().load(playlist.creator.avatarUrl).into(ivPlaylistFragmentArtist)
                //使用Glide异步获取bitmap，提供bitmap加载图片的同时，让palette进行颜色获取
                Glide.with(activity as AppCompatActivity).asBitmap().load(playlist.coverImgUrl).into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        //防止该 Fragment 已经销毁后Glide异步加载，导致崩溃
                        if (!viewDestroy) {
                            Logger.i(TAG, "Bitmap获取成功.")
                            val compressBitmap = getCompressBitmap(0.5f, 0.5f, bitmap)
                            ivPlaylistFragmentCover.setImageBitmap(compressBitmap)
                            Palette.from(compressBitmap).generate { palette ->
                                val darkMutedColor = palette?.getDarkMutedColor(Color.GRAY) ?: Color.GRAY
                                val mutedColor = palette?.getMutedColor(Color.GRAY) ?: Color.GRAY
                                //背景设置渐变
                                val gradientDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    arrayOf(darkMutedColor, mutedColor).toIntArray()
                                )
                                appBarLayoutPlaylistFragment.background = gradientDrawable
                                collapsingToolbarLayoutPlaylistFragment.setContentScrimColor((mutedColor + darkMutedColor) / 2)
                                Logger.i(TAG, "图片及Palette背景加载完成.")
                            }
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Logger.i(TAG, "onLoadCleared.")
                        if (!viewDestroy) Glide.with(requireContext()).clear(this)
                    }
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Logger.w(TAG, "Bitmap获取加载失败.")
                    }
                })

                //获取信息后设置信息栏信息
                tvPlaylistFragmentListName.text = playlist.name
                tvPlaylistFragmentArtist.text = playlist.creator.nickname
                tvPlaylistFragmentDescription.text = playlist.description
                btnMorePlaylistFragmentForward.setCount(playlist.shareCount)
                btnMorePlaylistFragmentComments.setCount(playlist.commentCount)
                btnMorePlaylistFragmentCollection.setCount(playlist.subscribedCount)
                rvPlaylistFragment.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = SongListAdapter(playlist.tracks, { songId, songName, arAndDes, picUrl ->
                        //onItemClick
                        //将点击播放的歌曲信息先写入数据库，完成后启动MusicActivity
                        if (isClickEffective()){
                            SongUtils.storeSongData(requireContext(), Song(songId, songName, arAndDes, picUrl)) {
                                startActivity(Intent(context, MusicActivity::class.java))
                            }
                        }
                    }, {
                        //onMoreClick

                    })
                }
            }
        }else{
            Logger.i(TAG,"数据未初始化完成,歌单页面加载失败.")
        }
    }

}