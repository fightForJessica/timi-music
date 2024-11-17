package com.timi.player

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.player.BroadcastConstant.INTENT_KEY_MUSIC
import com.timi.player.databinding.FragmentPlayerBinding
import com.timi.player.SongCondition.PLAY_MODE_LIST
import com.timi.player.SongCondition.PLAY_MODE_SHUFFLE
import com.timi.player.SongCondition.PLAY_MODE_SINGLE
import com.timi.player.SongCondition.currentSongIndex
import com.timi.player.SongCondition.songDataList
import com.timi.player.adapter.BottomSongListAdapter
import com.timi.player.store.bean.Song
import com.timi.utils.Logger
import com.timi.utils.isClickEffective
import com.timi.utils.makeToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlayerFragment : Fragment() {

	private var initView = false		//界面信息加载完成标志
	private var lastView: View? = null

	companion object{
		private const val PROGRESS_UPDATE = 0
		private const val TAG = "PlayerFragment"
	}

	private lateinit var binding: FragmentPlayerBinding
	private lateinit var navController: NavController

	private val handler = Handler(Looper.getMainLooper()) { msg ->
		if (msg.what == PROGRESS_UPDATE) updateCurrentPosition()
		true
	}

	private lateinit var song: Song
	private lateinit var songUrl: String

	private var serviceCreate = false
	private val musicConnection by lazy { MusicConnection() }
	private lateinit var serviceBinder: MusicService.MusicBinder
	private val notificationReceiver: NotificationReceiver by lazy { NotificationReceiver() }

	private var playEnd = false			//播放是否结束

	private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
	private lateinit var songAdapter: BottomSongListAdapter

	inner class MusicConnection : ServiceConnection{
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Logger.i(TAG, "ServiceConnection initialized.")
			serviceBinder = service as MusicService.MusicBinder

			//当与 service 链接后，开始初始化 service 中 mediaPlayer，播放音乐并且更新ui
			if (!SongCondition.launchFromNotification){
				serviceBinder.initPlayer(url = songUrl, looping = SongCondition.currentPlayMode == PLAY_MODE_SINGLE)
			}else{
				initSeekBar()
				SongCondition.playing = true
				SongCondition.launchFromNotification = false
			}
		}
		override fun onServiceDisconnected(name: ComponentName?) {
			Logger.i(TAG, "Service disconnect.")
		}
	}

	inner class NotificationReceiver: BroadcastReceiver(){
		override fun onReceive(context: Context?, intent: Intent) {
			intent.getStringExtra(INTENT_KEY_MUSIC)?.apply {
				when(this){
					BroadcastConstant.INTENT_VALUE_MUSIC_CONTROL_TO_FRAGMENT -> {
						//在Service播放器的状态已经经过调整，调整按键与handler需要相反调整
						if (!SongCondition.playing){
							handler.removeCallbacksAndMessages(null)
							binding.ivPlayerFragmentControl.setImageResource(R.drawable.ic_start)
						}else{
							handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 100L)
							binding.ivPlayerFragmentControl.setImageResource(R.drawable.ic_stop)
						}
					}
					BroadcastConstant.INTENT_VALUE_MUSIC_PREPARE -> {
						song = SongUtils.currentSong()
						setSongInformation()
						initSeekBar()
						SongCondition.playing = true
					}
				}
			}
		}
	}

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)	//让 fragment 中 toolbar 的按钮响应 onOptionItemSelected
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			requireContext().registerReceiver(notificationReceiver, IntentFilter().apply {
				addAction(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION)
			}, RECEIVER_NOT_EXPORTED)
		else
			requireContext().registerReceiver(notificationReceiver, IntentFilter().apply {
				addAction(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION)
			})
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if (lastView == null){
			binding = FragmentPlayerBinding.inflate(inflater, container, false)
			lastView = binding.root
		}
		return lastView!!
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		playEnd = false
		navController = Navigation.findNavController(view)

		if (!initView){
			song = SongUtils.currentSong()
			SongUtils.initRandomIndexList()
			setSongInformation()
			initBottomSheet()
			initWidget()
		}
		//如果从歌词fragment重新跳转时，重新加载handler
		if (SongCondition.playing) handler.sendEmptyMessage(PROGRESS_UPDATE)
	}

	override fun onResume() {
		super.onResume()
		//fragment准备好到前台后，开始发送 message 让 handle 刷新进度条
		if (initView && !playEnd){
			handler.removeCallbacksAndMessages(null)
			handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 100L)
		}
	}

	override fun onStop() {
		super.onStop()
		//fragment停止后，取消所有 message 和 callback，handle停止工作(不再刷新进度条)
		handler.removeCallbacksAndMessages(null)
	}

	override fun onDestroy() {
		super.onDestroy()
		requireContext().unregisterReceiver(notificationReceiver)
	}

	/**
	 * 初始化部分控件
	 */
	private fun initWidget(){
		binding.apply {
			(activity as AppCompatActivity).apply {
				setSupportActionBar(toolbarPlayerFragment)
				supportActionBar?.setDisplayHomeAsUpEnabled(true)
			}
			ivPlayerFragmentComments.setImageResource(R.drawable.ic_comments)
			ivPlayerFragmentLyric.setImageResource(R.drawable.ic_lyric)
			ivPlayerFragmentControl.setImageResource(if (SongCondition.playing) R.drawable.ic_stop else R.drawable.ic_start)
			ivPlayerFragmentNextSong.setImageResource(R.drawable.ic_next)
			ivPlayerFragmentPreSong.setImageResource(R.drawable.ic_previous)
			ivPlayerFragmentWatchList.setImageResource(R.drawable.ic_songlist)
			ivPlayerFragmentPlayMode.setImageResource(
				when(SongCondition.currentPlayMode){
					PLAY_MODE_LIST -> R.drawable.ic_listplay
					PLAY_MODE_SHUFFLE -> R.drawable.ic_shuffleplay
					else -> R.drawable.ic_singleplay	// PLAY_MODE_SINGLE
				})
			ivPlayerFragmentControl.setOnClickListener {
				if (!playEnd && it.isClickEffective(100)){
					playControl()
				}
			}
			ivPlayerFragmentPlayMode.setOnClickListener {
				if (!playEnd) playModeChange()
			}
			ivPlayerFragmentNextSong.setOnClickListener {
				if (!playEnd && it.isClickEffective(250)) {
					serviceBinder.nextSong()
				}else makeToast("你点得太快咯~~~")
			}
			ivPlayerFragmentPreSong.setOnClickListener {
				if (!playEnd && it.isClickEffective(250)){
					serviceBinder.previousSong()
				}else makeToast("你点得太快咯~~~")
			}
			ivPlayerFragmentLyric.setOnClickListener {
				if (!playEnd && it.isClickEffective()){
					//该跳转不传递信息，直接从SongCondition处获取
					navController.navigate(R.id.action_playerFragment_to_lyricFragment)
				}
			}
			ivPlayerFragmentWatchList.setOnClickListener{
				if (it.isClickEffective()){
					if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
						bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
					}else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
						bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
					}
				}
			}
			seekBarPlayerFragment.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
				override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
				override fun onStartTrackingTouch(seekBar: SeekBar?) {
					handler.removeCallbacksAndMessages(null)
				}
				override fun onStopTrackingTouch(seekBar: SeekBar) {
					if (!playEnd){
						serviceBinder.seekTo(seekBar.progress)
						if (!serviceBinder.isPlaying()){
							serviceBinder.play()
						}
						SongCondition.playing = true
						handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 100L)
					}
				}
			})
		}
	}

	/**
	 * 歌曲加载后，设置fragment中歌曲名称、歌手信息、图片与颜色等，随后开启服务并开始音乐播放
	 */
	private fun setSongInformation(){
		binding.apply {
			tvPlayerFragmentSongName.text = song.songName
			tvPlayerFragmentArtist.text = song.artistAndDescription
			ivPlayerFragmentLike.setImageResource(R.drawable.ic_like)	//喜欢按钮需要按需更改
			(activity as AppCompatActivity).supportActionBar?.title = song.songName

			//加载图片完成后，表示界面初始化完成，播放逻辑开始
			CoroutineScope(Dispatchers.Main).launch {
				Picasso.get().load(song.picUrl).into(ivPlayerFragmentSongPic)
			}.invokeOnCompletion {
				it?.printStackTrace()
				Logger.i(TAG, "图片加载完成.")
				if (!initView){
					seekBarPlayerFragment.visibility = View.VISIBLE
					//界面加载后，开始执行播放逻辑
					SongUtils.songUrlRequest(song.songId){ url ->
						songUrl = url
						initView = true
						startPlayerService()
					}
				}
			}
		}
	}

	@Deprecated("Deprecated in Java")
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home){
			initView = false
			lastView = null
			activity?.finish()
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * 初始化底部歌单列表
	 */
	@SuppressLint("NotifyDataSetChanged")
	private fun initBottomSheet(){
		bottomSheetBehavior = BottomSheetBehavior.from(binding.constraintLayoutPlayerFragmentBottom)
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
		binding.rvPlayerFragmentBottomPlaylist.apply {
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
			songAdapter = BottomSongListAdapter(songDataList, { position ->
				//onItemClicked
				//点击歌曲切换音乐
				//如果点击的是当前正在播放的歌曲，则不进行切换
				if (isClickEffective()){
					if (position != currentSongIndex){
						SongUtils.changeSongIndex(position)
						serviceBinder.changeSong()
					}
					if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
						bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
					}
				}
			}){ position ->
				//onDeleteClick
				//删除某歌曲
				if (isClickEffective()){
					val operateList = songDataList.toMutableList()
					operateList.removeAt(position)
					SongUtils.deleteSongData(requireContext(), songDataList[position])
					songDataList = operateList.toList()
					//如果其正在播放，那么切换下一首歌曲
					if (currentSongIndex == position){
						//如果被删除的歌曲不是原来列表中的最后一项，索引不需要变动即可播放下一首；如果是最后一项，则返回到第一首
						if (position == songDataList.size){
							SongUtils.changeSongIndex(0)
						}
						//删除后歌曲列表不为空，则切换下一首
						if (songDataList.isNotEmpty()){
							serviceBinder.changeSong()
						}else{
							playEnd()
						}
					}
					songAdapter.songList = songDataList
					songAdapter.notifyDataSetChanged()
				}
			}
			adapter = songAdapter
		}
	}

	/**
	 * 初始化音乐播放
	 * @param onInitCallBack 初始化后的回调
	 */
	private fun startPlayerService(onInitCallBack: (() -> Unit)? = null){
		requireContext().apply {
			if (!serviceCreate){
				serviceCreate = true
				startService(Intent(this, MusicService::class.java))
			}
			bindService(Intent(this, MusicService::class.java), musicConnection, BIND_AUTO_CREATE)
		}
		onInitCallBack?.invoke()
	}

	/**
	 * 接收实时歌曲播放进度更新当前时长TextView
	 */
	private fun updateCurrentPosition(){
		if (::serviceBinder.isInitialized){
			val currentPlayingPosition = serviceBinder.getCurrentPosition()
			Logger.i(TAG, "currentPlayingPosition=$currentPlayingPosition")
			binding.tvPlayerFragmentCurTime.text = SongUtils.updateDuration(currentPlayingPosition)
			binding.seekBarPlayerFragment.progress = currentPlayingPosition
			handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 1000L)
		}
	}

	/**
	 * 控制播放的 imageView 点击逻辑
	 */
	private fun playControl(){
		if (SongCondition.playing){
			handler.removeCallbacksAndMessages(null)
			binding.ivPlayerFragmentControl.setImageResource(R.drawable.ic_start)
		}else{
			handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 100L)
			binding.ivPlayerFragmentControl.setImageResource(R.drawable.ic_stop)
		}
		serviceBinder.playControl()
	}

	/**
	 * 修改当前播放模式
	 */
	private fun playModeChange(){
		when(SongCondition.currentPlayMode){
			PLAY_MODE_LIST -> {
				//列表播放 -> 随机播放
				SongCondition.currentPlayMode = PLAY_MODE_SHUFFLE
				binding.ivPlayerFragmentPlayMode.setImageResource(R.drawable.ic_shuffleplay)
				makeToast("随机播放")
			}
			PLAY_MODE_SHUFFLE -> {
				//随机播放 -> 单曲循环
				serviceBinder.setLooping(true)
				SongCondition.currentPlayMode = PLAY_MODE_SINGLE
				binding.ivPlayerFragmentPlayMode.setImageResource(R.drawable.ic_singleplay)
				makeToast("单曲循环")
			}
			PLAY_MODE_SINGLE -> {
				//单曲循环 -> 列表播放
				serviceBinder.setLooping(false)
				SongCondition.currentPlayMode = PLAY_MODE_LIST
				binding.ivPlayerFragmentPlayMode.setImageResource(R.drawable.ic_listplay)
				makeToast("列表播放")
			}
		}
	}

	/**
	 * 每次歌曲重新加载好后，初始化seekBar
	 */
	private fun initSeekBar(){
		val duration = serviceBinder.getDuration()
		Logger.i(TAG, "duration=$duration")
		binding.seekBarPlayerFragment.max = duration
		binding.tvPlayerFragmentEndTime.text = SongUtils.updateDuration(duration)
		binding.ivPlayerFragmentControl.setImageResource(R.drawable.ic_stop)
		handler.removeCallbacksAndMessages(null)	//先清除之前更新进度条的信息
		handler.sendEmptyMessageDelayed(PROGRESS_UPDATE, 100L)	//更新seekbar进度条
	}

	/**
	 * 当集合中没有任何歌曲，将页面信息置空
	 */
	@SuppressLint("SetTextI18n")
	private fun playEnd(){
		playEnd = true
		SongCondition.playing = false
		handler.removeCallbacksAndMessages(null)
		binding.apply {
			ivPlayerFragmentSongPic.setImageBitmap(null)
			tvPlayerFragmentSongName.text = ""
			tvPlayerFragmentArtist.text = ""
			tvPlayerFragmentCurTime.text = "00:00"
			tvPlayerFragmentEndTime.text = "00:00"
			ivPlayerFragmentControl.setImageResource(R.drawable.ic_start)
			seekBarPlayerFragment.progress = 0
		}
		if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
			bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
		}
		(activity as AppCompatActivity).supportActionBar?.title = ""
		requireContext().apply {
			unbindService(musicConnection)
			stopService(Intent(this, MusicService::class.java))
		}
	}

}