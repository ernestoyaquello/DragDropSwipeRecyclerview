package com.ernestoyaquello.dragdropswiperecyclerview.listener

/**
 * Listener for the swiping events of list items.
 */
interface OnItemSwipeListener<T> {

    /**
     * Indicates the direction in which the swipe action is performed.
     */
    enum class SwipeDirection {
        RIGHT_TO_LEFT,
        LEFT_TO_RIGHT,
        DOWN_TO_UP,
        UP_TO_DOWN
    }

    /**
     * Callback for whenever an item has been swiped.
     *
     * @param position The position of the swiped item.
     * @param direction The direction in which the item has been swiped.
     * @param item The item has been swiped.
     */
    fun onItemSwiped(position: Int, direction: SwipeDirection, item: T)
}