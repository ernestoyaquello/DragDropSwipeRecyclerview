package com.ernestoyaquello.dragdropswiperecyclerviewsample.util

/**
 * Dummy logger created to keep track of all the events that occur in this sample app.
 */
class Logger(listener: OnLogUpdateListener) {

    interface OnLogUpdateListener {
        fun onLogUpdated()
    }

    private val _messages = mutableListOf<String>()
    val messages: List<String>
        get() = _messages.toList()

    private val listeners = mutableListOf(listener)

    companion object {
        var instance: Logger? = null
            private set

        fun init(listener: OnLogUpdateListener) {
            if (instance == null)
                instance = Logger(listener)
            else {
                instance?.listeners?.clear()
                addListener(listener)
            }
        }

        fun addListener(listener: OnLogUpdateListener) {
            if (instance?.listeners?.contains(listener) == false)
                instance?.listeners?.add(listener)
        }

        fun removeListener(listener: OnLogUpdateListener) {
            if (instance?.listeners?.contains(listener) == true)
                instance?.listeners?.remove(listener)
        }

        fun reset() {
            instance?._messages?.clear()
            instance?.listeners?.forEach { it.onLogUpdated() }
        }

        fun log(message: String) {
            instance?._messages?.add(message)
            instance?.listeners?.forEach { it.onLogUpdated() }
        }
    }
}