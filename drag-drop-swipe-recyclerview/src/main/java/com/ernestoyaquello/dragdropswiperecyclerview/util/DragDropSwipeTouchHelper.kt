package com.ernestoyaquello.dragdropswiperecyclerview.util

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView.ListOrientation
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView.ListOrientation.DirectionFlag
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener.SwipeDirection

internal class DragDropSwipeTouchHelper(
        private val itemDragListener: OnItemDragListener,
        private val itemSwipeListener: OnItemSwipeListener,
        private val itemStateChangeListener: OnItemStateChangeListener,
        private val itemLayoutPositionChangeListener: OnItemLayoutPositionChangeListener,
        internal var recyclerView: DragDropSwipeRecyclerView?
) : ItemTouchHelper.Callback() {

    /**
     * Similar to the public interface of the library that has the same name, but for internal use only.
     * It will help pass the events from this class down to the adapter.
     */
    interface OnItemDragListener {
        fun onItemDragged(previousPosition: Int, newPosition: Int)
        fun onItemDropped(initialPosition: Int, finalPosition: Int)
    }

    /**
     * Similar to the public interface of the library that has the same name, but for internal use only.
     * It will help pass the events from this class down to the adapter.
     */
    interface OnItemSwipeListener {
        fun onItemSwiped(position: Int, direction: SwipeDirection)
    }

    /**
     * Similar to the public interface of the library that has the same name, but for internal use only.
     * It will help pass the events from this class down to the adapter.
     */
    interface OnItemStateChangeListener {

        enum class StateChangeType {
            DRAG_STARTED,
            DRAG_FINISHED,
            SWIPE_STARTED,
            SWIPE_FINISHED
        }

        fun onStateChanged(newState: StateChangeType, viewHolder: RecyclerView.ViewHolder) {
        }
    }

    interface OnItemLayoutPositionChangeListener {

        enum class Action {
            DRAGGING,
            SWIPING
        }

        fun onPositionChanged(
                action: Action,
                viewHolder: RecyclerView.ViewHolder,
                offsetX: Int,
                offsetY: Int,
                canvasUnder: Canvas?,
                canvasOver: Canvas?,
                isUserControlled: Boolean) {
        }
    }

    internal var orientation: ListOrientation? = null
    private val mOrientation: ListOrientation
        get() = orientation ?: throw NullPointerException("The orientation of the DragDropSwipeRecyclerView is not defined.")

    internal var disabledDragFlagsValue: Int = 0
    internal var disabledSwipeFlagsValue: Int = 0

    private var isDragging = false
    private var isSwiping = false
    private var initialItemPositionForOngoingDraggingEvent = -1

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        var threshold = super.getSwipeThreshold(viewHolder)

        // We have to adjust the threshold to act on the width or height of the item layout
        // because by default it applies to the entire width or height or the recycler view
        val recyclerViewWidth = recyclerView?.measuredWidth
        val recyclerViewHeight = recyclerView?.measuredHeight
        val itemWidth = viewHolder.itemView.measuredWidth
        val itemHeight = viewHolder.itemView.measuredHeight
        if (recyclerViewWidth != null && recyclerViewHeight != null) {
            val isSwipingHorizontally = (mOrientation.swipeFlagsValue and DirectionFlag.RIGHT.value == DirectionFlag.RIGHT.value)
                    || (mOrientation.swipeFlagsValue and DirectionFlag.LEFT.value == DirectionFlag.LEFT.value)
            threshold *= if (isSwipingHorizontally)
                (itemWidth.toFloat() / recyclerViewWidth.toFloat())
            else
                (itemHeight.toFloat() / recyclerViewHeight.toFloat())
        }

        return threshold
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder is DragDropSwipeAdapter.ViewHolder) {
            return makeMovementFlags(
                    if (viewHolder.canBeDragged?.invoke() == true) mOrientation.dragFlagsValue xor disabledDragFlagsValue else 0,
                    if (viewHolder.canBeSwiped?.invoke() == true) mOrientation.swipeFlagsValue xor disabledSwipeFlagsValue else 0)
        }

        return 0
    }

    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {
        itemDragListener.onItemDragged(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        return true
    }

    override fun canDropOver(
            recyclerView: RecyclerView,
            current: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder) =
            (target as? DragDropSwipeAdapter.ViewHolder)?.canBeDroppedOver?.invoke() == true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        val swipeDirection =
                when (direction) {
                    ItemTouchHelper.LEFT -> SwipeDirection.RIGHT_TO_LEFT
                    ItemTouchHelper.RIGHT -> SwipeDirection.LEFT_TO_RIGHT
                    ItemTouchHelper.UP -> SwipeDirection.DOWN_TO_UP
                    else -> SwipeDirection.UP_TO_DOWN
                }

        itemSwipeListener.onItemSwiped(position, swipeDirection)
    }

    override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        onChildDrawImpl(c, null, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean) {

        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        onChildDrawImpl(null, c, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (viewHolder != null) {
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> onStartedDragging(viewHolder)
                ItemTouchHelper.ACTION_STATE_SWIPE -> onStartedSwiping(viewHolder)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        onFinishedDraggingOrSwiping(viewHolder)
    }

    private fun onChildDrawImpl(
            canvasUnder: Canvas?,
            canvasOver: Canvas?,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean) {

        val action = when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> OnItemLayoutPositionChangeListener.Action.SWIPING
            ItemTouchHelper.ACTION_STATE_DRAG -> OnItemLayoutPositionChangeListener.Action.DRAGGING
            else -> null
        }

        if (action != null) {
            val offsetX = dX.toInt()
            val offsetY = dY.toInt()
            itemLayoutPositionChangeListener.onPositionChanged(
                    action,
                    viewHolder,
                    offsetX,
                    offsetY,
                    canvasUnder,
                    canvasOver,
                    isCurrentlyActive)
        }
    }

    private fun onStartedDragging(viewHolder: RecyclerView.ViewHolder) {
        isDragging = true
        initialItemPositionForOngoingDraggingEvent = viewHolder.bindingAdapterPosition
        itemStateChangeListener.onStateChanged(
                OnItemStateChangeListener.StateChangeType.DRAG_STARTED, viewHolder)
    }

    private fun onStartedSwiping(viewHolder: RecyclerView.ViewHolder) {
        isSwiping = true
        itemStateChangeListener.onStateChanged(
                OnItemStateChangeListener.StateChangeType.SWIPE_STARTED, viewHolder)
    }

    private fun onFinishedDraggingOrSwiping(viewHolder: RecyclerView.ViewHolder) {
        if (isDragging)
            onFinishedDragging(viewHolder)

        if (isSwiping)
            onFinishedSwiping(viewHolder)
    }

    private fun onFinishedDragging(viewHolder: RecyclerView.ViewHolder) {
        // At this point, the user has dropped the item
        val initialItemPositionForFinishedDraggingEvent = initialItemPositionForOngoingDraggingEvent
        val finalItemPositionForFinishedDraggingEvent = viewHolder.bindingAdapterPosition
        isDragging = false
        initialItemPositionForOngoingDraggingEvent = -1
        itemDragListener.onItemDropped(initialItemPositionForFinishedDraggingEvent, finalItemPositionForFinishedDraggingEvent)
        itemStateChangeListener.onStateChanged(
                OnItemStateChangeListener.StateChangeType.DRAG_FINISHED, viewHolder)
    }

    private fun onFinishedSwiping(viewHolder: RecyclerView.ViewHolder) {
        isSwiping = false
        itemStateChangeListener.onStateChanged(
                OnItemStateChangeListener.StateChangeType.SWIPE_FINISHED, viewHolder)
    }
}