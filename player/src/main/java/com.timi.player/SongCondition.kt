package com.timi.player

import com.timi.player.store.bean.Song

object SongCondition {

	internal const val PLAY_MODE_LIST = "list"
	internal const val PLAY_MODE_SHUFFLE = "shuffle"
	internal const val PLAY_MODE_SINGLE = "single"

	internal var playing = false
	internal var currentPlayMode = PLAY_MODE_LIST
	internal lateinit var currentRandomIndexList: List<Int>		//当前随机播放的索引集合
	internal lateinit var previousRandomIndexList: List<Int>	//上一个随机播放的索引集合
	internal var randomListIndex = 0	//指向随机索引集合中元素的索引
	internal var launchFromNotification = false		//MusicActivity的启动是否来源于Notification(如果是则PlayerFragment不需重载音乐)

	/**
	 * 需要播放的歌曲信息默认为空，刷新数据可以调用[SongUtils.refreshSongData]方法
	 */
	internal var songDataList: List<Song> = emptyList()
	internal var currentSongIndex = 0							//当前播放的歌曲位置
}