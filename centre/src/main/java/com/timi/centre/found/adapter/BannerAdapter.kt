package com.timi.centre.found.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.found.bean.Banner
import com.squareup.picasso.Picasso
import com.timi.centre.R

class BannerAdapter(
    private val bannerList: List<Banner>,
    private val onBannerClicked: (view: View) -> Unit
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mView = view;
        val picture: ImageView = view.findViewById(R.id.iv_Found_bannerPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_found_banneritem, parent, false)).apply {
            mView.setOnClickListener { onBannerClicked(it) }
        }

    private var aBanner: Banner? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        aBanner = bannerList[position % bannerList.size]
        Picasso.get().load(aBanner!!.pic).into(holder.picture)
        aBanner = null
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

}