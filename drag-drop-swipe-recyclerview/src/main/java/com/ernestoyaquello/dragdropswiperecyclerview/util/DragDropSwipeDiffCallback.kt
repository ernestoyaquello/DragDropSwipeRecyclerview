package com.ernestoyaquello.dragdropswiperecyclerview.util

import androidx.recyclerview.widget.DiffUtil

abstract class DragDropSwipeDiffCallback<T>(private val oldList: List<T>, private val newList: List<T>)
    : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            isSameItem(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
            isSameContent(oldList[oldPosition], newList[newPosition])

    /**
     * Determines whether or not the items are the same item.
     *
     * @param oldItem The old item.
     * @param newItem The new item.
     * @return True if the items are the same one; false otherwise.
     */
    abstract fun isSameItem(oldItem: T, newItem: T): Boolean

    /**
     * Determines whether or not the items have the same content.
     *
     * @param oldItem The old item.
     * @param newItem The new item.
     * @return True if the items have the same content; false otherwise.
     */
    abstract fun isSameContent(oldItem: T, newItem: T): Boolean
}