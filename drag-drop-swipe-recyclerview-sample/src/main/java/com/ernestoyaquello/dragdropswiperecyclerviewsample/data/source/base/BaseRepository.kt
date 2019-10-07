package com.ernestoyaquello.dragdropswiperecyclerviewsample.data.source.base

/**
 * This is a dummy implementation of a repository to be used by the app to add and retrieve items.
 * It is abstract and generic, so it can be used to implement repositories of different kinds.
 */
abstract class BaseRepository<T> {

    interface OnItemAdditionListener<T> {
        fun onItemAdded(item: T, position: Int)
    }

    private val items = mutableListOf<T>()
    private val listeners = mutableListOf<OnItemAdditionListener<T>>()

    fun getAllItems() = items.toList()

    abstract fun generateNewItem(): T

    fun addItem(item: T): Boolean {
        if (!items.contains(item)) {
            items.add(item)
            notifyItemAddition(item)

            return true
        }

        return false
    }

    fun insertItem(item: T, position: Int): Boolean {
        if (!items.contains(item)) {
            items.add(position, item)
            notifyItemAddition(item)

            return true
        }

        return false
    }

    fun removeItem(item: T): Boolean {
        if (items.contains(item)) {
            items.remove(item)

            return true
        }

        return false
    }

    fun addOnItemAdditionListener(listener: OnItemAdditionListener<T>) {
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    fun removeOnItemAdditionListener(listener: OnItemAdditionListener<T>) {
        if (listeners.contains(listener))
            listeners.remove(listener)
    }

    private fun notifyItemAddition(item: T) {
        val position = items.indexOf(item)
        listeners.forEach { it.onItemAdded(item, position) }
    }
}