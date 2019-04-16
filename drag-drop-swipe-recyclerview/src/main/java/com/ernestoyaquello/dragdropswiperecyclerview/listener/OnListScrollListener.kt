package com.ernestoyaquello.dragdropswiperecyclerview.listener

/**
 * Listener for the scroll events on the list.
 */
interface OnListScrollListener {

    /**
     * Indicates the direction in which the scroll action is performed.
     */
    enum class ScrollDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * Indicates the state of the scroll.
     */
    enum class ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    /**
     * Callback for whenever the list has been scrolled.
     *
     * @param scrollDirection The direction in which the list has been scrolled.
     * @param distance The distance in pixels that the list has been scrolled.
     */
    fun onListScrolled(scrollDirection: ScrollDirection, distance: Int)

    /**
     * Callback for whenever the scroll state of the list changes.
     *
     * @param scrollState The scroll state of the list.
     */
    fun onListScrollStateChanged(scrollState: ScrollState)
}