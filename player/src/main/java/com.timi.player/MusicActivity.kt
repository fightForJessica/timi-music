package com.timi.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MusicActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_music)
		//是否通过Notification启动该Activity
		intent?.apply {
			SongCondition.launchFromNotification = getStringExtra("intentFrom") == "Notification"
		}
	}

}