package com.timi.centre.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.timi.centre.BaseFragment
import com.timi.centre.CentreActivity
import com.timi.centre.R
import com.timi.centre.databinding.FragmentMineBinding
import com.timi.centre.mine.MineViewModel
import com.timi.utils.isClickEffective


class MineFragment : BaseFragment() {

    override val TAG = "MineFragment"


    companion object{
        private var single: MineFragment? = null
            get() {
                if (field == null) field = MineFragment()
                return field!!
            }
    }

    private lateinit var binding: FragmentMineBinding
    private lateinit var navController: NavController
    private val mineViewModel: MineViewModel by lazy {
        ViewModelProvider(this)[MineViewModel::class.java]
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun viewCreate(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        binding.apply {
            searchToolbarMineFragment.apply {
                initToolbar(activity as AppCompatActivity){ activity ->
                    edt_inputBox.visibility = View.GONE
                    sidewaysMineFragment.setExitButton {
                        if (isClickEffective()){
                            activity.finish()
                        }
                    }
                }

                //布置侧滑布局
                val toggle = ActionBarDrawerToggle(
                    activity,
                    drawerLayoutMineFragment,
                    toolbar,
                    R.string.main_open,
                    R.string.main_close
                )
                toggle.syncState()
                drawerLayoutMineFragment.setDrawerListener(toggle)

                //搜索框点击事件
                setLayoutOnClickListener{
                    if (isClickEffective()){
                        CentreActivity.hideTab()
                        navController.navigate(R.id.action_mineFragment_to_guideFragment)
                    }
                }
            }
        }
    }

    override fun observeChange() {

    }
}