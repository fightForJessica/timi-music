package com.timi.centre

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.timi.utils.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView

class CentreActivity : AppCompatActivity() {

    private val TAG = "CentreActivity"
    private lateinit var navController: NavController
    private lateinit var cookie: String

    companion object{
        private var bottomNavigationView: BottomNavigationView? = null

        fun hideTab(){
            if (bottomNavigationView!!.visibility != View.GONE)
                bottomNavigationView!!.visibility = View.GONE
        }

        fun showTab(){
            if (bottomNavigationView!!.visibility != View.VISIBLE)
                bottomNavigationView!!.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_centre)

        getLoginInformation()//获取登录cookie

        bottomNavigationView = findViewById(R.id.bottomNavigation_CentreActivity)
        //初始化navigation
        navController = Navigation.findNavController(this, R.id.fragmentNav_CentreActivity)
        NavigationUI.setupWithNavController(bottomNavigationView!!, navController)
    }

    private fun getLoginInformation(){
        cookie = intent.getStringExtra("cookie").toString()
        Logger.i(TAG, "cookie=$cookie")
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNavigationView = null
    }

}