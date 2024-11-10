package com.timi.centre.playlist

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.centre.databinding.FragmentCoverBinding
import com.timi.centre.playlist.adapter.CoverLabelAdapter
import com.timi.utils.Logger
import com.timi.utils.getCompressBitmap
import com.timi.utils.isClickEffective
import kotlin.properties.Delegates


class CoverFragment : Fragment() {

    private val TAG = "CoverFragment"

    companion object{
        private var single: CoverFragment? = null
            get(){
                if (field == null) field = CoverFragment()
                return field
            }
    }

    private lateinit var binding: FragmentCoverBinding
    private lateinit var navController: NavController

    private lateinit var title: String
    private lateinit var backgroundUrl: String
    private lateinit var labelList: List<String>
    private lateinit var description: String

    private var viewDestroy by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            title = getString("title", "")
            backgroundUrl = getString("backgroundUrl", "")
            labelList = getStringArrayList("label")?.toList() ?: listOf()
            description = getString("description", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoverBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDestroy = false
        navController = Navigation.findNavController(view)
        binding.apply {
            //点击任意一处返回上一 fragment
            ivCoverFragmentBack.setOnClickListener {
                if (it.isClickEffective()){
                    navController.popBackStack()
                }
            }

            if (backgroundUrl.isNotEmpty()){
                Glide.with(activity as AppCompatActivity).asBitmap().load(backgroundUrl).into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        //防止该 Fragment 已经销毁后Glide异步加载，导致崩溃
                        Logger.i(TAG, "Bitmap获取成功.")
                        val compressBitmap = getCompressBitmap(0.5f, 0.5f, bitmap)
                        ivCoverFragmentBackground.setImageBitmap(compressBitmap)
                        Palette.from(compressBitmap).generate { palette ->
                            val darkMutedColor = palette?.getDarkMutedColor(Color.GRAY) ?: Color.GRAY
                            val mutedColor = palette?.getMutedColor(Color.GRAY) ?: Color.GRAY
                            //背景设置渐变
                            val gradientDrawable1 = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, arrayOf(darkMutedColor, mutedColor).toIntArray())
                            val gradientDrawable2 = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, arrayOf(mutedColor, darkMutedColor).toIntArray())
                            appBarLayoutCoverFragment.background = gradientDrawable1
                            nestedScrollViewCoverFragment.background = gradientDrawable2
                            Logger.i(TAG, "图片及Palette背景加载完成.")
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Logger.i(TAG, "onLoadCleared.")
                        Glide.with(requireContext()).clear(this)
                    }
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Logger.w(TAG, "Bitmap获取失败.")
                    }
                })
            }

            tvCoverFragmentTitle.text = title
            tvCoverFragmentDescription.text = description
            rvCoverFragmentLabel.apply {
                layoutManager = object : LinearLayoutManager(requireContext(), HORIZONTAL, false){
                    override fun canScrollHorizontally(): Boolean = false
                }
                adapter = CoverLabelAdapter(labelList)
            }
            tvCoverFragmentDescription.text = description.ifEmpty { "暂无描述" }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDestroy = true
    }
}