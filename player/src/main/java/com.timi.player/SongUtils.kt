package com.timi.player

import android.content.Context
import com.timi.player.SongCondition.currentRandomIndexList
import com.timi.player.SongCondition.currentSongIndex
import com.timi.player.SongCondition.previousRandomIndexList
import com.timi.player.SongCondition.randomListIndex
import com.timi.player.SongCondition.songDataList
import com.timi.player.store.SongDatabase
import com.timi.player.store.bean.Lyric
import com.timi.player.store.bean.LyricService
import com.timi.player.store.bean.ProcessedLyric
import com.timi.player.store.bean.Song
import com.timi.player.store.bean.SongUrl
import com.timi.player.store.bean.SongUrlService
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 存放控制歌曲播放的相关方法
 */
object SongUtils {

	private const val TAG = "SongUtils"
	private const val INVOKE_FROM_DEFAULT = 1
	private const val INVOKE_FROM_STORE = 2
	internal const val INDEX_NEXT = 3
	internal const val INDEX_PREVIOUS = 4


	/**
	 * 通过歌曲id获取歌曲播放的url
	 * @param songId 歌曲id
	 * @param onSuccess 成功后包含歌曲url参数的回调
	 */
	internal fun songUrlRequest(songId: Long, onSuccess:(String) -> Unit){
		ServiceBuilder.create(SongUrlService::class.java)
			.getSongUrlData(songId).enqueue(object : Callback<SongUrl> {
				override fun onResponse(call: Call<SongUrl>, response: Response<SongUrl>) {
					response.body()?.apply {
						Logger.i(TAG, "歌曲链接请求:$this")
						onSuccess(this.data[0].url)
					}
				}
				override fun onFailure(call: Call<SongUrl>, t: Throwable) {
					t.printStackTrace()
				}
			})
	}

	/**
	 * 通过歌曲id获取歌曲歌词
	 * @param songId 歌曲id
	 * @param onSuccess 成功后包含歌词参数的回调
	 */
	internal fun songLyricRequest(songId: Long, onSuccess: (String) -> Unit){
		ServiceBuilder.create(LyricService::class.java)
			.getSongLyricData(songId).enqueue(object : Callback<Lyric>{
				override fun onResponse(call: Call<Lyric>, response: Response<Lyric>) {
					response.body()?.apply {
						Logger.i(TAG, "歌曲歌词请求:$this")
						onSuccess(this.lrc.lyric)
					}
				}
				override fun onFailure(call: Call<Lyric>, t: Throwable) {
					t.printStackTrace()
				}
			})
	}

	/**
	 * 获取歌曲的总时长转化成 mm:ss 形式写入 TextView 展示
	 * @param duration 歌曲总时长
	 * @return mm:ss 形式时长字符串
	 */
	internal fun updateDuration(duration: Int): String{
		val sec = duration / 1000
		val minute = sec / 60
		val second = sec % 60
		val result: String
		var stringBuilder: StringBuilder? = StringBuilder()
		stringBuilder!!.apply {
			append(if (minute < 10) "0$minute:" else minute)
			append(if (second < 10) "0$second" else second)
			result = toString()
			clear()
		}
		stringBuilder = null
		return result
	}

	/**
	 * 将整串的歌词字符串分割成歌词部分与时间部分
	 * <p>
	 *
	 * 歌词举例: "[00:27.48]不曾见过风的形状\n[00:33.28]风在你白衣裳里震荡\n"
	 *
	 * @param lyricString 包含歌曲时间以及歌词的整串字符
	 * @return 返回处理后的歌词对象[ProcessedLyric]集合
	 */
	internal fun getLyricObjectList(lyricString: String): List<ProcessedLyric>{
		//
		val resultList = mutableListOf<ProcessedLyric>()
		val aLineList = lyricString.split("\n")	//每一行的歌词
		for (i in 0 .. aLineList.size - 2){
			val word = aLineList[i].substringAfter("]").substringAfter(" ")
			val time = aLineList[i].substringAfter('[').substringBefore(']')
			val times = time.split(':')
			val startTime = calculateTime(times[0], times[1])
			resultList.add(ProcessedLyric(word, startTime))
		}
		return resultList
	}

	/**
	 * 将分割出的字符串时间计算成整型时间返回
	 * @param min 分钟字符串
	 * @param sec 秒字符串
	 * @return 时间(整型)
	 */
	private fun calculateTime(min: String, sec: String): Int =
		((min.toFloat() * 60 + sec.toFloat()) * 1000).toInt()


	/**
	 * 将每次点击播放歌曲的部分信息写入数据库中(如果先前没有写入过该歌曲信息)，写入后自动更新歌曲信息
	 * @param context 上下文
	 * @param song 播放歌曲的部分信息
	 * @param onFinished 储存完成后的回调
	 */
	fun storeSongData(context: Context, song: Song, onFinished: (() -> Unit)? = null){
		CoroutineScope(Dispatchers.IO).launch {
			SongDatabase.getInstance(context).songDataDao().apply {
				searchSongData(song.songId).apply {
					if (this == null){
						Logger.i(TAG, "歌曲信息写入数据库.")
						addSongData(song)
					}else {
						//如果已经写入过数据库，将该歌曲先删除再添加，让其处于播放首位
						deleteSongData(context, song, {
							addSongData(song)
						}, INVOKE_FROM_STORE)
					}
				}
			}
		}.invokeOnCompletion {
			it?.printStackTrace()
			refreshSongData(context, onFinished, INVOKE_FROM_STORE)
		}
	}

	/**
	 * 从数据库中更新 SongCondition 单例中的歌曲信息
	 * @param context 上下文
	 * @param onFinished 刷新歌曲信息后的回调
	 * @param invokeFrom 调用源头。默认为 INVOKE_FROM_DEFAULT
	 */
	internal fun refreshSongData(context: Context, onFinished: (() -> Unit)? = null, invokeFrom: Int = INVOKE_FROM_DEFAULT){
		//如果从其他数据库操作方法中调用该方法，则不需要再次开启协程域
		if (invokeFrom == INVOKE_FROM_STORE){
			//由于输入数据库的数据会顺序输进，而用户一般会收听后加入的歌曲，因此将其进行反转符合用户需求
			songDataList = SongDatabase.getInstance(context).songDataDao().getAllSongData().reversed()
			Logger.i(TAG, "歌曲信息更新完成.\ncurrent songData size=${songDataList.size}")
			onFinished?.invoke()
		}else{
			CoroutineScope(Dispatchers.IO).launch {
				songDataList = SongDatabase.getInstance(context).songDataDao().getAllSongData()
				Logger.i(TAG, "歌曲信息更新完成.\ncurrent songData size=${songDataList.size}")
			}.invokeOnCompletion {
				it?.printStackTrace()
				onFinished?.invoke()
			}
		}
	}

	/**
	 * 清除数据库中歌曲信息
	 * @param context 上下文
	 * @param onFinished 清除歌曲信息后的回调
	 */
	internal fun clearSongData(context: Context, onFinished: (() -> Unit)? = null){
		CoroutineScope(Dispatchers.IO).launch{
			Logger.i(TAG, "清除歌曲信息.")
			SongDatabase.getInstance(context).songDataDao().clearSongData()
		}.invokeOnCompletion {
			it?.printStackTrace()
			onFinished?.invoke()
		}
	}

	/**
	 * 删除某一首歌曲在数据库的信息
	 * @param context 上下文
	 * @param song 删除的歌曲
	 * @param onFinished 删除歌曲信息后的回调
	 * @param invokeFrom 调用源头。默认为 INVOKE_FROM_DEFAULT
	 */
	internal fun deleteSongData(context: Context, song: Song, onFinished: (() -> Unit)? = null, invokeFrom: Int = INVOKE_FROM_DEFAULT){
		//如果从其他数据库操作方法中调用该方法，则不需要再次开启协程域
		if (invokeFrom == INVOKE_FROM_STORE){
			SongDatabase.getInstance(context).songDataDao().deleteSongData(song.songId)
			onFinished?.invoke()
		}else{
			CoroutineScope(Dispatchers.IO).launch {
				SongDatabase.getInstance(context).songDataDao().deleteSongData(song.songId)
			}.invokeOnCompletion {
				it?.printStackTrace()
				onFinished?.invoke()
			}
		}
	}

	/**
	 * 当更改下一首歌曲时，将索引后移一位；如果是最后一首歌曲则将索引移到队头
	 */
	internal fun nextSongIndex(){
		if (currentSongIndex != songDataList.size - 1) currentSongIndex++
			else currentSongIndex = 0
	}

	/**
	 * 当更改上一首歌曲时，将索引进行调整
	 */
	internal fun previousSongIndex(){
		if (currentSongIndex != 0) currentSongIndex--
			else currentSongIndex = songDataList.size - 1
	}

	/**
	 * 当在歌曲列表跳转播放歌曲时，将索引进行调整
	 * @param newIndex 新歌曲的索引
	 */
	internal fun changeSongIndex(newIndex: Int){
		currentSongIndex = newIndex
	}

	/**
	 * 当处于随机播放模式时，更新指向索引，将随即索引集合的元素取出赋给当前歌曲索引进行音乐变更
	 * 如果随机索引集合未被初始化，则该方法将会对其初始化
	 * @param direction 随机索引往前或往后移动的标志变量
	 */
	internal fun shuffleSongIndex(direction: Int){
		if (direction == INDEX_NEXT){
			//当指向索引为当前随机索引集合的最后一位元素，则保存并更新当前随机索引集合，指向索引变更为新索引的第一位
			if (randomListIndex == currentRandomIndexList.size - 1){
				previousRandomIndexList = currentRandomIndexList
				currentRandomIndexList = randomIndexList(songDataList.size)
				randomListIndex = 0
			}else{
				randomListIndex++
			}
		}else if (direction == INDEX_PREVIOUS){
			//当指向索引为当前随机索引集合的第一位元素，则还原并更新上一随即索引集合，指向索引变更为旧索引的最后一位
			if (randomListIndex == 0){
				currentRandomIndexList = previousRandomIndexList
				previousRandomIndexList = randomIndexList(songDataList.size)
				randomListIndex = songDataList.size - 1
			}else{
				randomListIndex--
			}
		}
		//指向索引变更后，将随即索引集合的元素赋给当前歌曲索引
		currentSongIndex = currentRandomIndexList[randomListIndex]
	}

	/**
	 * 每次进入playerFragment，都让随机索引进行刷新，确保和歌曲数目保持一致
	 */
	internal fun initRandomIndexList(){
		randomListIndex = 0
		currentRandomIndexList = randomIndexList(songDataList.size)
		previousRandomIndexList = randomIndexList(songDataList.size)
	}

	/**
	 * 生成一个装载 0~size 索引数的随机整型集合
	 * @param size 集合长度
	 * @return 随机索引整型集合
	 */
	private fun randomIndexList(size: Int): List<Int>{
		val result = mutableListOf<Int>()
		for (i in 0 until size){
			result.add(i)
		}
		result.shuffle()
		result.shuffle()
		return result
	}

	/**
	 * 获取当前歌曲数据
	 * @return 当前索引位置的歌曲
	 */
	internal fun currentSong() = songDataList[currentSongIndex]
}