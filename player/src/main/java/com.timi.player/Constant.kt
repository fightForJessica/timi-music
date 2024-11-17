package com.timi.player

/**
 * 存放广播常量
 */
object BroadcastConstant {

	//通知栏音乐播放器控件点击发出的广播
	internal const val BROADCAST_NOTIFICATION_ACTION = "com.example.player.NOTIFICATION_ACTION"
	const val INTENT_KEY_MUSIC = "action"
	const val INTENT_VALUE_MUSIC_CONTROL = "notification_click_playControl_toService"
	const val INTENT_VALUE_MUSIC_CONTROL_TO_FRAGMENT = "notification_click_playControl_toFragment"
	const val INTENT_VALUE_MUSIC_PREVIOUS = "notification_click_previousSong"
	const val INTENT_VALUE_MUSIC_NEXT = "notification_click_nextSong"
	const val INTENT_VALUE_MUSIC_LIKE = "notification_click_like"
	const val INTENT_VALUE_MUSIC_LYRIC = "notification_click_lyric"
	const val INTENT_VALUE_MUSIC_PREPARE = "mediaPlayer prepared"



}