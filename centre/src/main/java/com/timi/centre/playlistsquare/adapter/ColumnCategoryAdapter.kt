package com.timi.centre.playlistsquare.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.playlistsquare.room.CategoryTag

class ColumnCategoryAdapter(
	var categoryTagList: List<CategoryTag>,
	private var categorySelecting: Boolean,
	private val isSelectedList: Boolean,
	private val onCategoryClicked: (categoryTag: CategoryTag, position: Int) -> Unit
) : RecyclerView.Adapter<ColumnCategoryAdapter.ViewHolder>() {

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
		val mView = view
		val constraint: ConstraintLayout = view.findViewById(R.id.constrainLayout_Category_tagItem)
		val iv_hot: ImageView = view.findViewById(R.id.iv_Category_hot)
		val tv_name: TextView = view.findViewById(R.id.tv_Category_name)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_catagory_tag, parent, false)).apply {
			mView.setOnClickListener {
				onCategoryClicked(categoryTagList[adapterPosition], adapterPosition)
			}
		}

	private var categoryTag: CategoryTag? = null
	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		categoryTag = categoryTagList[position]
		holder.apply {
			//如果是热门标签，则添加热门标志
			if (categoryTag!!.hot)
				iv_hot.setImageResource(R.drawable.ic_hot)
			else
				iv_hot.visibility = View.GONE
			//点击编辑按键后显示的更改
			if (categorySelecting){
				if (categoryTag!!.hasSelect){
					//默认的"推荐"、"官方"、"精品"编辑时显示为无法变更
					if (isSelectedList && position <= 2){
						constraint.alpha = 0.5f
						tv_name.text = categoryTag!!.name
					}else{
						tv_name.text = "-  ${categoryTag!!.name}"
					}
				}else{
					tv_name.text = "+  ${categoryTag!!.name}"
				}
			}else{
				tv_name.text = categoryTag!!.name
			}
		}
		categoryTag = null
	}

	override fun getItemCount(): Int = categoryTagList.size

}