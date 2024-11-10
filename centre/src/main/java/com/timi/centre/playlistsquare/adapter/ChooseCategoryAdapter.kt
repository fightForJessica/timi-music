package com.timi.centre.playlistsquare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timi.centre.R
import com.timi.centre.playlistsquare.adapter.ColumnCategoryAdapter
import com.timi.centre.playlistsquare.room.CategoryTag

class ChooseCategoryAdapter(
	var selectedTags: List<CategoryTag>,
	var languageTags: List<CategoryTag>,
	var styleTags: List<CategoryTag>,
	var sceneTags: List<CategoryTag>,
	var emotionTags: List<CategoryTag>,
	var topicTags: List<CategoryTag>,
	private val onCategoryClicked:(CategoryTag) -> Unit,
	private val onEditClicked:() -> Unit
) : RecyclerView.Adapter<ChooseCategoryAdapter.ViewHolder>() {

	var categorySelecting = false
	private val defaultTagList = listOf(
		CategoryTag("推荐", "默认", hasSelect = true, false),
		CategoryTag("官方", "默认", hasSelect = true, false),
		CategoryTag("精品", "默认", hasSelect = true, false)
	)

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
		val tv_column: TextView = view.findViewById(R.id.tv_Category_column)
		val iv_edit: ImageView = view.findViewById(R.id.iv_Category_edit)
		val rv_tag: RecyclerView = view.findViewById(R.id.rv_Category_tag)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category_column, parent, false))

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.tv_column.text = when(position){
			0 -> "我的歌单广场"
			1 -> "语种"
			2 -> "风格"
			3 -> "场景"
			4 -> "情感"
			else -> "主题"
		}
		if (position == 0) {
			holder.iv_edit.visibility = View.VISIBLE
			holder.iv_edit.setImageResource(R.drawable.ic_edit)
			holder.iv_edit.setOnClickListener {
				onEditClicked()
			}
		}
		holder.rv_tag.apply {
			layoutManager = object : GridLayoutManager(context, 4){
				override fun canScrollVertically(): Boolean = false
			}
			adapter = ColumnCategoryAdapter(when(position){
				0 -> selectedTagShow()
				1 -> languageTags
				2 -> styleTags
				3 -> sceneTags
				4 -> emotionTags
				else -> topicTags
			}, categorySelecting, position == 0){ category, position ->
				//onCategoryClicked
				onCategoryClicked(category)
			}
		}
	}

	override fun getItemCount(): Int = 6

	/**
	 * 将数据库中保存的已经被选择的categoryTag集合与默认集合合并
	 * @return 被选择的categoryTag集合
	 */
	private fun selectedTagShow(): List<CategoryTag>{
		val resultList = mutableListOf<CategoryTag>()
		resultList.addAll(defaultTagList)
		resultList.addAll(selectedTags)
		return resultList
	}

}