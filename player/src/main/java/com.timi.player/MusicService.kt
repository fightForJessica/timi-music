package com.timi.player

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.timi.player.BroadcastConstant.INTENT_KEY_MUSIC
import com.timi.utils.Logger
import com.timi.utils.BitmapRequest

class MusicService : Service() {

	companion object{
		private const val TAG = "MusicService"
		private const val NOTIFICATION_CHANNEL_ID = "音乐播放通知栏"
		private const val NOTIFICATION_CHANNEL_NAME = "音乐播放通知栏"
		private const val NOTIFICATION_ID = 0
	}

	private lateinit var mediaPlayer: MediaPlayer
	private var serviceCreate = false
	private lateinit var remoteViews: RemoteViews
	private lateinit var manager: NotificationManager
	private lateinit var builder: Notification
	private val notificationReceiver: NotificationReceiver by lazy { NotificationReceiver() }

	inner class MusicBinder: Binder() {

		internal fun play(){
			this@MusicService.play()
		}

		internal fun playControl(){
			this@MusicService.playControl()
		}

		internal fun getDuration(): Int = mediaPlayer.duration

		internal fun getCurrentPosition(): Int = mediaPlayer.currentPosition

		internal fun seekTo(position: Int){
			mediaPlayer.seekTo(position)
		}

		internal fun setLooping(looping: Boolean){
			mediaPlayer.isLooping = looping
		}

		internal fun isPlaying(): Boolean = mediaPlayer.isPlaying

		internal fun changeSong(){
			this@MusicService.changeSong()
		}

		internal fun nextSong(){
			this@MusicService.nextSong()
		}

		internal fun previousSong(){
			this@MusicService.previousSong()
		}

		internal fun initPlayer(url: String, looping: Boolean, onPreparedBack: (() -> Unit)? = null){
			this@MusicService.initPlayer(url, looping, onPreparedBack)
		}
	}

	/**
	 * 通知栏事件的广播接收器
	 */
	inner class NotificationReceiver : BroadcastReceiver(){
		override fun onReceive(context: Context?, intent: Intent) {
			Logger.i(TAG, "Notification broadcast is received in service.")
			intent.getStringExtra(INTENT_KEY_MUSIC)?.apply {
				when(this){
					BroadcastConstant.INTENT_VALUE_MUSIC_CONTROL -> {
						playControl()
						//调整完成播放器的状态后，再发送广播至fragment调整按键与handler
						sendBroadcast(Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
							putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_CONTROL_TO_FRAGMENT)
						})
					}
					BroadcastConstant.INTENT_VALUE_MUSIC_PREVIOUS -> previousSong()
					BroadcastConstant.INTENT_VALUE_MUSIC_NEXT -> nextSong()
					BroadcastConstant.INTENT_VALUE_MUSIC_LIKE -> {

					}
					BroadcastConstant.INTENT_VALUE_MUSIC_LYRIC -> {

					}
				}
			}
		}
	}

	override fun onBind(intent: Intent): IBinder = MusicBinder()

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
	override fun onCreate() {
		super.onCreate()
		Logger.i(TAG, "MusicService created.")
		mediaPlayer = MediaPlayer()
		serviceCreate = true
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			registerReceiver(notificationReceiver, IntentFilter().apply {
				addAction(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION)
			}, RECEIVER_NOT_EXPORTED)
		else
			registerReceiver(notificationReceiver, IntentFilter().apply {
				addAction(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION)
			})
		initRemoteView()
		showNotification()//RemoteView初始化完成后，展示通知
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Logger.i(TAG, "MusicService onStartCommand.")
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onDestroy() {
		super.onDestroy()
		Logger.i(TAG, "MusicService destroy.")
		unregisterReceiver(notificationReceiver)
		if (mediaPlayer.isPlaying) mediaPlayer.stop()
		mediaPlayer.release()
	}

	/**
	 * 部署Notification，完成后给予展示
	 */
	private fun showNotification(){
		//创建通知频道
		manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
			channel.lockscreenVisibility = Notification.VISIBILITY_SECRET
			manager.createNotificationChannel(channel)
		}

		val notificationIntent = PendingIntent.getActivity(this, 0,
			Intent(this, MusicActivity::class.java).apply {
				putExtra("intentFrom", "Notification")
			},
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE
			else PendingIntent.FLAG_UPDATE_CURRENT
		)
		builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setContentTitle("A developed music")
			.setSmallIcon(R.drawable.ic_music)
			.setCustomContentView(remoteViews)
			.setContentIntent(notificationIntent)
			.setAutoCancel(false)
			.build()
		builder.flags = Notification.FLAG_NO_CLEAR
		manager.notify(NOTIFICATION_ID, builder)
	}

	/**
	 * 初始化自定义通知
	 */
	private fun initRemoteView(){
		//自定义Notification布局中控件点击发送Intent
		val controlIntent: PendingIntent
		val previousIntent: PendingIntent
		val nextIntent: PendingIntent
		val likeIntent: PendingIntent
		val lyricIntent: PendingIntent
		//Android12新每个PendingIntent对象都要指定可变性
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
			controlIntent = PendingIntent.getBroadcast(this, 1,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_CONTROL)
				}, PendingIntent.FLAG_MUTABLE)
			previousIntent = PendingIntent.getBroadcast(this, 2,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_PREVIOUS)
				}, PendingIntent.FLAG_IMMUTABLE)
			nextIntent = PendingIntent.getBroadcast(this, 3,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_NEXT)
				}, PendingIntent.FLAG_IMMUTABLE)
			likeIntent = PendingIntent.getBroadcast(this, 4,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_LIKE)
				}, PendingIntent.FLAG_IMMUTABLE)
			lyricIntent = PendingIntent.getBroadcast(this, 5,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_LYRIC)
				}, PendingIntent.FLAG_IMMUTABLE)
		}else{
			controlIntent = PendingIntent.getBroadcast(this, 1,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_CONTROL)
				}, PendingIntent.FLAG_UPDATE_CURRENT)
			previousIntent = PendingIntent.getBroadcast(this, 2,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_PREVIOUS)
				}, PendingIntent.FLAG_UPDATE_CURRENT)
			nextIntent = PendingIntent.getBroadcast(this, 3,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_NEXT)
				}, PendingIntent.FLAG_UPDATE_CURRENT)
			likeIntent = PendingIntent.getBroadcast(this, 4,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_LIKE)
				}, PendingIntent.FLAG_UPDATE_CURRENT)
			lyricIntent = PendingIntent.getBroadcast(this, 5,
				Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_LYRIC)
				}, PendingIntent.FLAG_UPDATE_CURRENT)
		}
		remoteViews = RemoteViews(packageName, R.layout.layout_notifitation)
		remoteViews.apply{
			//播放图标
			setImageViewResource(R.id.iv_Notification_control,
				if (SongCondition.playing) R.drawable.ic_notification_stop else R.drawable.ic_notification_start)
			setOnClickPendingIntent(R.id.iv_Notification_control, controlIntent)
			//上一首图标
			setImageViewResource(R.id.iv_Notification_preSong, R.drawable.ic_notification_previous)
			setOnClickPendingIntent(R.id.iv_Notification_preSong, previousIntent)
			//下一首图标
			setImageViewResource(R.id.iv_Notification_nextSong, R.drawable.ic_notification_next)
			setOnClickPendingIntent(R.id.iv_Notification_nextSong, nextIntent)
			//喜欢图标
			setImageViewResource(R.id.iv_Notification_like, R.drawable.ic_notification_like)
			setOnClickPendingIntent(R.id.iv_Notification_like, likeIntent)
			//歌词图标
			setImageViewResource(R.id.iv_Notification_lyric, R.drawable.ic_notification_lyric)
			setOnClickPendingIntent(R.id.iv_Notification_lyric, lyricIntent)

		}

	}

	/**
	 * 当MediaPlayer播放准备完成时，刷新Notification信息
	 */
	private fun refreshNotification(){
		remoteViews.apply {
			//绑定TextView信息
			val currentSong = SongCondition.songDataList[SongCondition.currentSongIndex]
			setTextViewText(R.id.tv_Notification_songName, currentSong.songName)
			setTextViewText(R.id.tv_Notification_artist, currentSong.artistAndDescription.substringBefore(" "))
			//加入图标
			//加载歌曲图片
			BitmapRequest.getImageBitmap(currentSong.picUrl){
				setImageViewBitmap(R.id.iv_Notification_pic, it)
				manager.notify(NOTIFICATION_ID, builder)
			}
		}
	}

	private fun initPlayer(url: String, looping: Boolean, onPreparedBack: (() -> Unit)? = null){
		if (mediaPlayer.isPlaying) mediaPlayer.stop()
		mediaPlayer.apply {
			reset()
			isLooping = looping
			setDataSource(url)
			prepareAsync()
			setOnPreparedListener {
				Logger.i(TAG, "MediaPlayer was prepared.")
				start()
				refreshNotification()//歌曲加载完毕后刷新Notification

				//发送广播至fragment更新信息
				sendBroadcast(Intent(BroadcastConstant.BROADCAST_NOTIFICATION_ACTION).apply {
					putExtra(INTENT_KEY_MUSIC, BroadcastConstant.INTENT_VALUE_MUSIC_PREPARE)
				})
				onPreparedBack?.invoke()
			}
			setOnCompletionListener {
				nextSong()
			}
		}
	}

	private fun play(){
		if (!mediaPlayer.isPlaying) mediaPlayer.start()
	}

	private fun pause(){
		if (mediaPlayer.isPlaying) mediaPlayer.pause()
	}

	/**
	 * 通知栏控制播放的开始与暂停
	 */
	private fun playControl(){
		if (SongCondition.playing){
			SongCondition.playing = false
			pause()
		}else{
			SongCondition.playing = true
			play()
		}
	}

	private fun changeSong(){
		SongUtils.songUrlRequest(SongUtils.currentSong().songId){
			initPlayer(url = it, looping = SongCondition.currentPlayMode == SongCondition.PLAY_MODE_SINGLE){
				SongCondition.playing = true
			}
		}
	}

	/**
	 * 让索引定位到下一首歌曲，随后调用[changeSong]重新播放
	 */
	private fun nextSong(){
		//如果是列表播放和单曲循环，则索引指向下一首歌曲；
		//如果是随机播放，让索引更改为随机索引集合的下一首歌曲；
		//如果是单曲循环，则不需修改索引，直接播放原来歌曲；
		when(SongCondition.currentPlayMode){
			SongCondition.PLAY_MODE_SHUFFLE -> SongUtils.shuffleSongIndex(SongUtils.INDEX_NEXT)
			else -> SongUtils.nextSongIndex()
		}
		changeSong()
	}

	/**
	 * 让索引定位到上一首歌曲，随后调用[changeSong]重新播放
	 */
	private fun previousSong(){
		//如果是列表播放和单曲序号，则索引指向上一首歌曲；
		//如果是随机播放，让索引更改为随机索引集合的上一首歌曲；
		when(SongCondition.currentPlayMode){
			SongCondition.PLAY_MODE_SHUFFLE -> SongUtils.shuffleSongIndex(SongUtils.INDEX_PREVIOUS)
			else -> SongUtils.previousSongIndex()
		}
		changeSong()
	}

}