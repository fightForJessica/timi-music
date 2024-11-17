package com.timi.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.timi.player.SongCondition.currentSongIndex
import com.timi.player.SongCondition.songDataList
import com.timi.player.adapter.LyricAdapter
import com.timi.player.adapter.LyricLayoutManager
import com.timi.player.databinding.FragmentLyricBinding
import com.timi.player.store.bean.ProcessedLyric
import com.timi.utils.Logger
import kotlin.properties.Delegates


class LyricFragment : Fragment() {

	companion object{
		private const val TAG = "LyricFragment"
		private const val LYRIC_UPDATE = 0
	}

	private lateinit var binding: FragmentLyricBinding
	private lateinit var navController: NavController

	private val musicConnection by lazy { MusicConnection() }
	private lateinit var serviceBinder: MusicService.MusicBinder
	private var serviceConnect = false

	private val handler = Handler(Looper.getMainLooper()) { msg ->
		if (msg.what == LYRIC_UPDATE) updateCurrentLyric()
		true
	}

	private var songId by Delegates.notNull<Long>()
	private lateinit var songName: String
	private lateinit var processedLyricList: List<ProcessedLyric>
	private lateinit var lyricAdapter: LyricAdapter
	private lateinit var lyricLayoutManager: LyricLayoutManager
	private var currentLyricPosition = 0

	inner class MusicConnection : ServiceConnection{
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Logger.i(TAG, "ServiceConnection initialized.")
			serviceBinder = service as MusicService.MusicBinder
			serviceConnect = true
			//绑定完成后加载歌词，并开始更新歌词
			createLyric()
			handler.sendEmptyMessageDelayed(LYRIC_UPDATE, 100L)
		}
		override fun onServiceDisconnected(name: ComponentName?) {
			Logger.i(TAG, "Service disconnect.")
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onResume() {
		super.onResume()
		if (SongCondition.playing && serviceConnect){
			handler.removeCallbacksAndMessages(null)
			handler.sendEmptyMessageDelayed(LYRIC_UPDATE, 100L)
		}
	}

	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
	}

	override fun onDestroy() {
		super.onDestroy()
		requireContext().unbindService(musicConnection)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentLyricBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		navController = Navigation.findNavController(view)
		songName = songDataList[currentSongIndex].songName
		songId = songDataList[currentSongIndex].songId

		(activity as AppCompatActivity).apply {
			setSupportActionBar(binding.toolbarLyricFragment)
			supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		}
		binding.tvLyricFragmentSongName.text = songName
		binding.tvLyricFragmentArtist.text = songDataList[currentSongIndex].artistAndDescription.substringBefore(" ")

		SongUtils.songLyricRequest(songId){
			processedLyricList = SongUtils.getLyricObjectList(it)
			requireContext().bindService(Intent(requireContext(), MusicService::class.java), musicConnection, Context.BIND_AUTO_CREATE)
		}
	}

	@Deprecated("Deprecated in Java")
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home){
			navController.popBackStack()
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * 更新当前歌词
	 */
	private fun updateCurrentLyric(){
		val currentPosition = serviceBinder.getCurrentPosition()
		Logger.i(TAG, "currentPlayingPosition=$currentPosition")

//		if ((processedLyricList[currentLyricPosition].startTime < currentPosition && currentPosition < processedLyricList[currentLyricPosition + 1].startTime) ||
//			(currentLyricPosition == processedLyricList.size - 1 && processedLyricList[currentLyricPosition].startTime < currentPosition)){
//			binding.rvPlayerFragmentLyric.scrollToPosition(currentLyricPosition)
//			//lyricLayoutManager.smoothScrollToPosition(binding.rvPlayerFragmentLyric, RecyclerView.State(), i)
//			currentLyricPosition++
//		}
//
		handler.sendEmptyMessageDelayed(LYRIC_UPDATE, 200L)
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun createLyric(){
		lyricLayoutManager = LyricLayoutManager(requireContext())
		lyricAdapter = LyricAdapter(processedLyricList){
			//onItemClicked
		}
		binding.rvPlayerFragmentLyric.apply {
			layoutManager = lyricLayoutManager
			adapter = lyricAdapter

			//在触碰后对进行延迟更新
			setOnTouchListener { v, event ->
				this@LyricFragment.handler.removeCallbacksAndMessages(null)
				this@LyricFragment.handler.sendEmptyMessageDelayed(LYRIC_UPDATE, 3000L)
				false
			}
		}
	}

}