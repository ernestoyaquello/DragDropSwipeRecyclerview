package com.ernestoyaquello.dragdropswiperecyclerview

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnListScrollListener

/**
 * Extension of RecyclerView that detects when the user scrolls.
 */
open class ScrollAwareRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /**
     * Listener for the scrolling events.
     */
    var scrollListener: OnListScrollListener? = null

    private val internalListScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            when (newState) {
                SCROLL_STATE_IDLE ->
                    scrollListener?.onListScrollStateChanged(OnListScrollListener.ScrollState.IDLE)
                SCROLL_STATE_DRAGGING ->
                    scrollListener?.onListScrollStateChanged(OnListScrollListener.ScrollState.DRAGGING)
                SCROLL_STATE_SETTLING ->
                    scrollListener?.onListScrollStateChanged(OnListScrollListener.ScrollState.SETTLING)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            when {
                dy > 0 ->
                    scrollListener?.onListScrolled(OnListScrollListener.ScrollDirection.DOWN, dy)
                dy < 0 ->
                    scrollListener?.onListScrolled(OnListScrollListener.ScrollDirection.UP, -dy)
                dx > 0 ->
                    scrollListener?.onListScrolled(OnListScrollListener.ScrollDirection.RIGHT, dx)
                dx < 0 ->
                    scrollListener?.onListScrolled(OnListScrollListener.ScrollDirection.LEFT, -dx)
            }
        }
    }

    init {
        super.addOnScrollListener(internalListScrollListener)
    }

    @Deprecated("Use the property scrollListener instead.", ReplaceWith("scrollListener"))
    override fun addOnScrollListener(listener: OnScrollListener) {
        throw UnsupportedOperationException(
                "Only the property scrollListener can be used to add a scroll listener here.")
    }
}