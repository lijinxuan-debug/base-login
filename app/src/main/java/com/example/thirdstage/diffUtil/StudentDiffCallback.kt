package com.example.thirdstage.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.thirdstage.dataModel.Student

class StudentDiffCallback(
    private val oldList: List<Student>,
    private val newList: List<Student>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].studentId == newList[newItemPosition].studentId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}