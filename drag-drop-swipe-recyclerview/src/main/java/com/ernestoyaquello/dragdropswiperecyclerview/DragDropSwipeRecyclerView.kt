package com.ernestoyaquello.dragdropswiperecyclerview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.ernestoyaquello.dragdropswiperecyclerview.util.DragDropSwipeItemDecoration
import android.os.Bundle

/**
 * Extension of RecyclerView that detects swipe, drag & drop and scrolling.
 */
open class DragDropSwipeRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ScrollAwareRecyclerView(context, attrs, defStyleAttr) {

    /**
     * Indicates the orientation of a recycler view of type DragDropSwipeRecyclerView.
     */
    enum class ListOrientation(
            internal var dragFlagsValue: Int,
            internal var swipeFlagsValue: Int) {

        /**
         * The list will be scrolled vertically and its items will be dragged only vertically.
         */
        VERTICAL_LIST_WITH_VERTICAL_DRAGGING(
                DirectionFlag.UP.value or DirectionFlag.DOWN.value,
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value),

        /**
         * The list will be scrolled vertically and its items will be dragged in any direction.
         */
        VERTICAL_LIST_WITH_UNCONSTRAINED_DRAGGING(
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value or DirectionFlag.UP.value or DirectionFlag.DOWN.value,
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value),

        /**
         * The list will be scrolled horizontally and its items will be dragged only horizontally.
         */
        HORIZONTAL_LIST_WITH_HORIZONTAL_DRAGGING(
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value,
                DirectionFlag.UP.value or DirectionFlag.DOWN.value),

        /**
         * The list will be scrolled horizontally and its items will be dragged in any direction.
         */
        HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING(
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value or DirectionFlag.UP.value or DirectionFlag.DOWN.value,
                DirectionFlag.UP.value or DirectionFlag.DOWN.value),

        /**
         * The list will be arranged as a grid and its items will be swiped only horizontally.
         */
        GRID_LIST_WITH_HORIZONTAL_SWIPING(
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value or DirectionFlag.UP.value or DirectionFlag.DOWN.value,
                DirectionFlag.LEFT.value or DirectionFlag.RIGHT.value),

        /**
         * The list will be arranged as a grid and its items will be swiped only vertically.
         */
        GRID_LIST_WITH_VERTICAL_SWIPING(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.UP or ItemTouchHelper.DOWN);

        /**
         * Direction flags for the drag action. Determines in which direction the user gestures
         * will cause dragging to happen.
         */
        val dragDirectionFlags: List<DirectionFlag>
            get() {
                val dragFlagsList = mutableListOf<DirectionFlag>()

                if (dragFlagsValue and DirectionFlag.UP.value == DirectionFlag.UP.value)
                    dragFlagsList.add(DirectionFlag.UP)
                if (dragFlagsValue and DirectionFlag.DOWN.value == DirectionFlag.DOWN.value)
                    dragFlagsList.add(DirectionFlag.DOWN)
                if (dragFlagsValue and DirectionFlag.LEFT.value == DirectionFlag.LEFT.value)
                    dragFlagsList.add(DirectionFlag.LEFT)
                if (dragFlagsValue and DirectionFlag.RIGHT.value == DirectionFlag.RIGHT.value)
                    dragFlagsList.add(DirectionFlag.RIGHT)

                return dragFlagsList
            }

        /**
         * Direction flags for the swipe action. Determines in which direction the user gestures
         * will cause swiping to happen.
         */
        val swipeDirectionFlags: List<DirectionFlag>
            get() {
                val swipeFlagsList = mutableListOf<DirectionFlag>()

                if (swipeFlagsValue and DirectionFlag.UP.value == DirectionFlag.UP.value)
                    swipeFlagsList.add(DirectionFlag.UP)
                if (swipeFlagsValue and DirectionFlag.DOWN.value == DirectionFlag.DOWN.value)
                    swipeFlagsList.add(DirectionFlag.DOWN)
                if (swipeFlagsValue and DirectionFlag.LEFT.value == DirectionFlag.LEFT.value)
                    swipeFlagsList.add(DirectionFlag.LEFT)
                if (swipeFlagsValue and DirectionFlag.RIGHT.value == DirectionFlag.RIGHT.value)
                    swipeFlagsList.add(DirectionFlag.RIGHT)

                return swipeFlagsList
            }

        /**
         * Removes the specified direction flag from the drag flags.
         * Please note that the the next time you select the affected value of the enum type,
         * this change will still be applied to it.
         *
         * @param flag The flag to be removed.
         */
        fun removeDragDirectionFlag(flag: DirectionFlag) {
            val newValue = dragFlagsValue xor flag.value

            // To make sure we don't perform this operation twice, which will restore the original flag
            // value, we only assign the new flag value if it is lower than the previous one.
            dragFlagsValue = if (newValue < dragFlagsValue) newValue else dragFlagsValue
        }

        /**
         * Removes the specified direction flag from the swipe flags.
         * Please note that the the next time you select the affected value of the enum type,
         * this change will still be applied to it.
         *
         * @param flag The flag to be removed.
         */
        fun removeSwipeDirectionFlag(flag: DirectionFlag) {
            val newValue = swipeFlagsValue xor flag.value

            // To make sure we don't perform this operation twice, which will restore the original flag
            // value, we only assign the new flag value if it is lower than the previous one.
            swipeFlagsValue = if (newValue < swipeFlagsValue) newValue else swipeFlagsValue
        }

        /**
         * Adds the specified direction flag to the drag flags.
         * Please note that the the next time you select the affected value of the enum type,
         * this change will still be applied to it.
         *
         * @param flag The flag to be added.
         */
        fun addDragDirectionFlag(flag: DirectionFlag) {
            dragFlagsValue = dragFlagsValue or flag.value
        }

        /**
         * Adds the specified direction flag to the swipe flags.
         * Please note that the the next time you select the affected value of the enum type,
         * this change will still be applied to it.
         *
         * @param flag The flag to be added.
         */
        fun addSwipeDirectionFlag(flag: DirectionFlag) {
            swipeFlagsValue = swipeFlagsValue or flag.value
        }

        internal fun restoreFlags(dragFlagsValue: Int, swipeFlagsValue: Int) {
            this.dragFlagsValue = dragFlagsValue
            this.swipeFlagsValue = swipeFlagsValue
        }

        enum class DirectionFlag(internal val value: Int) {

            /**
             * Up direction, used for swipe & drag control.
             */
            UP(ItemTouchHelper.UP),

            /**
             * Down direction, used for swipe & drag control.
             */
            DOWN(ItemTouchHelper.DOWN),

            /**
             * Left direction, used for swipe & drag control.
             */
            LEFT(ItemTouchHelper.LEFT),

            /**
             * Right direction, used for swipe & drag control.
             */
            RIGHT(ItemTouchHelper.RIGHT);
        }
    }

    private var itemDecoration: DragDropSwipeItemDecoration? = null

    internal var dividerDrawable: Drawable? = null
        get() {
            val dividerId = dividerDrawableId
            if (field == null && dividerId != null && dividerId != 0)
                field = AppCompatResources.getDrawable(context, dividerId)
            else if (dividerId == null || dividerId == 0)
                field = null

            // Pass the value down to the item decoration so it can use it
            val fieldValue = field
            if (fieldValue != null)
                itemDecoration?.divider = fieldValue

            return field
        }
        private set(value) {
            if (value != field) {
                field = value

                // Pass the divider down to the item decoration, which is where it will be used
                if (itemDecoration == null) {
                    if (value != null) {
                        val newItemDecoration = DragDropSwipeItemDecoration(value)
                        itemDecoration = newItemDecoration
                        this.addItemDecoration(newItemDecoration, 0)
                    }
                } else {
                    if (value != null)
                        itemDecoration?.divider = value
                    else {
                        val currentItemDecoration = itemDecoration
                        if (currentItemDecoration != null)
                            this.removeItemDecoration(currentItemDecoration)
                        itemDecoration = null
                    }
                }
            }
        }

    internal var behindSwipedItemIconDrawable: Drawable? = null
        get() {
            val behindSwipedItemId = behindSwipedItemIconDrawableId
            if (field == null && behindSwipedItemId != null && behindSwipedItemId != 0)
                field = AppCompatResources.getDrawable(context, behindSwipedItemId)
            else if (behindSwipedItemId == null || behindSwipedItemId == 0)
                field = null

            return field
        }
        private set(value) { field = value }

    internal var behindSwipedItemIconSecondaryDrawable: Drawable? = null
        get() {
            val behindSwipedItemSecondaryId = behindSwipedItemIconSecondaryDrawableId
            if (field == null && behindSwipedItemSecondaryId != null && behindSwipedItemSecondaryId != 0)
                field = AppCompatResources.getDrawable(context, behindSwipedItemSecondaryId)
            else if (behindSwipedItemSecondaryId == null || behindSwipedItemSecondaryId == 0)
                field = null

            return field
        }
        private set(value) { field = value }

    internal var behindSwipedItemSecondaryLayout: View? = null
        get() {
            val behindSwipedItemSecondaryViewId = behindSwipedItemSecondaryLayoutId
            if (field == null && behindSwipedItemSecondaryViewId != null && behindSwipedItemSecondaryViewId != 0)
                field = LayoutInflater.from(context).inflate(behindSwipedItemSecondaryViewId, null, false)
            else if (behindSwipedItemSecondaryViewId == null || behindSwipedItemSecondaryViewId == 0)
                field = null

            return field
        }
        private set(value) { field = value }

    internal var behindSwipedItemLayout: View? = null
        get() {
            val behindSwipedItemViewId = behindSwipedItemLayoutId
            if (field == null && behindSwipedItemViewId != null && behindSwipedItemViewId != 0)
                field = LayoutInflater.from(context).inflate(behindSwipedItemViewId, null, false)
            else if (behindSwipedItemViewId == null || behindSwipedItemViewId == 0)
                field = null

            return field
        }
        private set(value) { field = value }

    /**
     * The ID of the item layout that will be used to populate each list item.
     *
     * Can be set up in XML using the attribute "item_layout".
     */
    var itemLayoutId: Int = 0

    /**
     * The ID of the drawable that will be displayed as a divider between list items.
     * If set to null, no divider will be drawn. Null by default.
     *
     * Please note that, for vertical lists, the divider drawable's height must be defined.
     * In the same way, horizontal lists need a divider drawable with a defined width,
     * and grid lists need a divider drawable with both width and height defined.
     * Also note that the divider should have no transparency to be drawn correctly at all times.
     *
     * Can be set up in XML using the attribute "divider".
     */
    var dividerDrawableId: Int? = null
        set(value) {
            if (value != field) {
                field = value

                if (value != null && value != 0) {
                    val divider = AppCompatResources.getDrawable(context, value)
                    if (divider != null)
                        dividerDrawable = divider
                } else dividerDrawable = null
            }
        }

    /**
     * The ID of the drawable of the icon to display behind an item that is being swiped.
     * If there is a secondary icon defined, this one will only be displayed when swiping left (or down).
     * If there isn't a secondary icon defined, it will be displayed when swiping in any direction.
     * If it is not specified (i.e., if null), no icon will be displayed behind swiped items.
     * Null by default.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_icon".
     */
    var behindSwipedItemIconDrawableId: Int? = null
        set(value) {
            if (value != field) {
                field = value

                if (value != null && value != 0) {
                    val behindSwipedItemIcon = AppCompatResources.getDrawable(context, value)
                    if (behindSwipedItemIcon != null)
                        behindSwipedItemIconDrawable = behindSwipedItemIcon
                } else behindSwipedItemIconDrawable = null
            }
        }

    /**
     * The ID of the drawable of the secondary icon to display behind an item that is being swiped.
     * Because it is the secondary one, it will be displayed only when swiping right (or up).
     * If not specified (i.e., if null), the main icon will be the one displayed when swiping right (or up).
     * Null by default.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_icon_secondary".
     */
    var behindSwipedItemIconSecondaryDrawableId: Int? = null
        set(value) {
            if (value != field) {
                field = value

                if (value != null && value != 0) {
                    val behindSwipedSecondaryItemIcon = AppCompatResources.getDrawable(context, value)
                    if (behindSwipedSecondaryItemIcon != null)
                        behindSwipedItemIconSecondaryDrawable = behindSwipedSecondaryItemIcon
                } else behindSwipedItemIconSecondaryDrawable = null
            }
        }

    /**
     * The distance between the icon displayed behind an item that is being swiped and the side of
     * the item from which the swiping started.
     * 0 if not specified; ignored if behindSwipedItemCenterIcon is true.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_icon_margin".
     */
    var behindSwipedItemIconMargin: Float = 0f

    /**
     * Determines whether the icon displayed behind an item that is being swiped should be centered.
     * If false, the icon will be displayed near the side from which the swiping started.
     * False by default.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_icon_centered".
     */
    var behindSwipedItemCenterIcon: Boolean = false

    /**
     * The background color to be displayed behind an item that is being swiped.
     * If there is a secondary color defined, this one will only be displayed when swiping left (or down).
     * If there isn't a secondary color defined, it will be displayed when swiping in any direction.
     * If not specified (i.e., if null), no color will be displayed behind items that are being swiped.
     * Null by default.
     *
     * Note that this must be the actual value of the color and not a reference to a color resource.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_bg_color".
     */
    var behindSwipedItemBackgroundColor: Int? = null

    /**
     * The secondary background color to be displayed behind an item that is being swiped.
     * Because it is the secondary one, it will be displayed only when swiping right (or up).
     * If not specified (i.e., if null), the main color will be the one displayed when swiping right (or up).
     * Null by default.
     *
     * Note that this must be the actual value of the color and not a reference to a color resource.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_bg_color_secondary".
     */
    var behindSwipedItemBackgroundSecondaryColor: Int? = null

    /**
     * The ID of the custom layout to be displayed behind an item that is being swiped.
     * If there is a secondary layout defined, this one will only be displayed when swiping left (or down).
     * If there isn't a secondary layout defined, it will be displayed when swiping in any direction.
     * If it is not specified (i.e., if null), no custom layout will be displayed behind swiped items
     * (but background colors and icons will still be displayed behind swiped items if specified).
     * Null by default.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_custom_layout".
     */
    var behindSwipedItemLayoutId: Int? = null
        set(value) {
            if (value != field) {
                field = value

                if (value != null && value != 0) {
                    val behindSwipedItemView = LayoutInflater.from(context).inflate(value, null, false)
                    if (behindSwipedItemView != null)
                        behindSwipedItemLayout = behindSwipedItemView
                } else behindSwipedItemLayout = null
            }
        }

    /**
     * The ID of the secondary custom layout to be displayed behind an item that is being swiped.
     * Because it is the secondary one, it will be displayed only when swiping right (or up).
     * If it is not specified, the main custom layout will be the one displayed when swiping right (or up).
     * Null by default.
     *
     * Can be set up in XML using the attribute "behind_swiped_item_custom_layout_secondary".
     */
    var behindSwipedItemSecondaryLayoutId: Int? = null
        set(value) {
            if (value != field) {
                field = value

                if (value != null && value != 0) {
                    val behindSwipedItemSecondaryView = LayoutInflater.from(context).inflate(value, null, false)
                    if (behindSwipedItemSecondaryView != null)
                        behindSwipedItemSecondaryLayout = behindSwipedItemSecondaryView
                } else behindSwipedItemSecondaryLayout = null
            }
        }

    /**
     * Determines whether the item that is being swiped should be make more transparent the further
     * it gets from its original position.
     * False by default.
     *
     * Can be set up in XML using the attribute "swiped_item_opacity_fades_on_swiping".
     */
    var reduceItemAlphaOnSwiping: Boolean = false

    /**
     * The number of columns of each row of the grid-arranged, vertically-scrollable list.
     * This value will be used to avoid drawing dividers in wrong positions. 1 by default.
     * To be ignored in non-grid-arranged lists.
     */
    var numOfColumnsPerRowInGridList: Int = 1

    /**
     * The number of rows of each column of the grid-arranged, horizontally-scrollable list.
     * This value will be used to avoid drawing dividers in wrong positions. 1 by default.
     * To be ignored in non-grid-arranged lists.
     */
    var numOfRowsPerColumnInGridList: Int = 1

    /**
     * Listener for dragging events.
     */
    var dragListener: OnItemDragListener<*>? = null
        set(value) {
            if (value != field) {
                field = value

                // Pass down this listener to the adapter, which is where it will be used
                adapter?.setInternalDragListener(value)
            }
        }

    /**
     * Listener for swiping events.
     */
    var swipeListener: OnItemSwipeListener<*>? = null
        set(value) {
            if (value != field) {
                field = value

                // Pass down this listener to the adapter, which is where it will be used
                adapter?.setInternalSwipeListener(value)
            }
        }

    /**
     * Adapter of the recycler view.
     *
     * @throws IllegalArgumentException If the adapter to set is null.
     */
    var adapter: DragDropSwipeAdapter<*,*>? = null
        set(value) {
            if (value != null) {
                if (value != field) {
                    field = value

                    // Pass down all these variables to the new adapter to set it up
                    value.setInternalDragListener(dragListener)
                    value.setInternalSwipeListener(swipeListener)
                    value.swipeAndDragHelper.orientation = orientation

                    super.setAdapter(value)
                }
            } else throw IllegalArgumentException("A null adapter cannot be set.")
        }

    /**
     * The orientation of the recycler view. It is mandatory to set it; the list won't work otherwise.
     * Null by default.
     *
     * If the orientation of the list hasn't been set but the list is set up with a linear layout manager,
     * the orientation will be inferred automatically from the linear layout manager:
     * - VERTICAL_LIST_WITH_VERTICAL_DRAGGING for linear layout managers with vertical orientation.
     * - HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING for linear layout managers with horizontal orientation.
     *
     * In the same way, if the list is configured with a grid layout manager, the orientation will
     * be set to GRID_LIST_WITH_HORIZONTAL_SWIPING automatically.
     * If the layout manager is not one of the above and/or a different orientation is required,
     * this property must be set manually.
     *
     * Please note that this property is only used as a reference to handle actions of swiping and
     * drag & drop; a layout manager still needs to be set in this recycler view.
     * Note also that a grid orientation will disable the support for item dividers.
     */
    var orientation: ListOrientation? = null
        set(value) {
            if (value != field) {
                field = value

                // Pass down this value to the adapter, which is where it will be used
                adapter?.swipeAndDragHelper?.orientation = value
            }
        }

    init {
        if (attrs != null) {
            val vars = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.DragDropSwipeRecyclerView,
                    defStyleAttr,
                    0)

            try {
                itemLayoutId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_item_layout, 0)
                dividerDrawableId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_divider, 0)
                behindSwipedItemIconDrawableId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_icon, 0)
                behindSwipedItemIconSecondaryDrawableId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_icon_secondary, 0)
                behindSwipedItemIconMargin = vars.getDimension(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_icon_margin, 0f)
                behindSwipedItemCenterIcon = vars.getBoolean(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_icon_centered, false)
                behindSwipedItemBackgroundColor = vars.getColor(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_bg_color, Color.TRANSPARENT)
                behindSwipedItemBackgroundSecondaryColor = vars.getColor(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_bg_color_secondary, Color.TRANSPARENT)
                behindSwipedItemLayoutId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_custom_layout, 0)
                behindSwipedItemSecondaryLayoutId = vars.getResourceId(R.styleable.DragDropSwipeRecyclerView_behind_swiped_item_custom_layout_secondary, 0)
                reduceItemAlphaOnSwiping = vars.getBoolean(R.styleable.DragDropSwipeRecyclerView_swiped_item_opacity_fades_on_swiping, false)
            } finally {
                vars.recycle()
            }
        }
    }

    /**
     * Sets the adapter of the recycler view.
     *
     * @param adapter The adapter to set. It must be an extension of the type DragDropSwipeAdapter.
     * @throws TypeCastException If the adapter is not an extension of the type DragDropSwipeAdapter.
     */
    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        when (adapter) {
            !is DragDropSwipeAdapter<*,*>? ->
                throw TypeCastException("The adapter must be an extension of DragDropSwipeAdapter.")
            else -> this.adapter = adapter
        }
    }

    /**
     * Sets the layout manager of the recycler view, which is mandatory to make it work.
     *
     * @param layoutManager The layout manager to be used by the list to arrange the item layouts.
     */
    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)

        // If the orientation is undefined, we try to guess it
        if (orientation == null) {
            when (layoutManager) {

                is LinearLayoutManager -> orientation =
                        when (layoutManager.orientation) {
                            LinearLayoutManager.VERTICAL -> ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
                            LinearLayoutManager.HORIZONTAL -> ListOrientation.HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING
                            else -> orientation
                }

                is GridLayoutManager -> orientation =
                        when (layoutManager.orientation) {
                            LinearLayoutManager.VERTICAL -> ListOrientation.GRID_LIST_WITH_HORIZONTAL_SWIPING
                            LinearLayoutManager.HORIZONTAL -> ListOrientation.GRID_LIST_WITH_VERTICAL_SWIPING
                            else -> orientation
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        if (isSaveEnabled) {
            val bundle = Bundle()

            bundle.putParcelable(SUPER_STATE_KEY, superState)
            bundle.putInt(ITEM_LAYOUT_ID_KEY, itemLayoutId)
            bundle.putInt(DIVIDER_DRAWABLE_ID_KEY, dividerDrawableId ?: 0)
            bundle.putInt(BEHIND_SWIPED_ITEM_ICON_DRAWABLE_ID_KEY, behindSwipedItemIconDrawableId ?: 0)
            bundle.putInt(BEHIND_SWIPED_ITEM_ICON_SECONDARY_DRAWABLE_ID_KEY, behindSwipedItemIconSecondaryDrawableId ?: 0)
            bundle.putFloat(BEHIND_SWIPED_ITEM_ICON_MARGIN_KEY, behindSwipedItemIconMargin)
            bundle.putBoolean(BEHIND_SWIPED_ITEM_CENTER_ICON_KEY, behindSwipedItemCenterIcon)
            bundle.putInt(BEHIND_SWIPED_ITEM_BACKGROUND_COLOR_KEY, behindSwipedItemBackgroundColor ?: Color.TRANSPARENT)
            bundle.putInt(BEHIND_SWIPED_ITEM_BACKGROUND_SECONDARY_COLOR_KEY, behindSwipedItemBackgroundSecondaryColor ?: Color.TRANSPARENT)
            bundle.putInt(BEHIND_SWIPED_ITEM_LAYOUT_ID_KEY, behindSwipedItemLayoutId ?: 0)
            bundle.putInt(BEHIND_SWIPED_ITEM_SECONDARY_LAYOUT_ID_KEY, behindSwipedItemSecondaryLayoutId ?: 0)
            bundle.putBoolean(REDUCE_ITEM_ALPHA_ON_SWIPING_KEY, reduceItemAlphaOnSwiping)
            bundle.putInt(NUM_OF_COLUMNS_PER_ROW_IN_GRID_LIST_KEY, numOfColumnsPerRowInGridList)
            bundle.putInt(NUM_OF_ROWS_PER_COLUMN_IN_GRID_LIST_KEY, numOfRowsPerColumnInGridList)
            bundle.putString(ORIENTATION_NAME_KEY, orientation?.name)
            bundle.putInt(ORIENTATION_DRAG_FLAGS_KEY, orientation?.dragFlagsValue ?: 0)
            bundle.putInt(ORIENTATION_SWIPE_FLAGS_KEY, orientation?.swipeFlagsValue ?: 0)

            return bundle
        }

        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state

        if (isSaveEnabled && state is Bundle) {
            superState = state.getParcelable(SUPER_STATE_KEY)
            itemLayoutId = state.getInt(ITEM_LAYOUT_ID_KEY, 0)
            dividerDrawableId = state.getInt(DIVIDER_DRAWABLE_ID_KEY, 0)
            behindSwipedItemIconDrawableId = state.getInt(BEHIND_SWIPED_ITEM_ICON_DRAWABLE_ID_KEY, 0)
            behindSwipedItemIconSecondaryDrawableId = state.getInt(BEHIND_SWIPED_ITEM_ICON_SECONDARY_DRAWABLE_ID_KEY, 0)
            behindSwipedItemIconMargin = state.getFloat(BEHIND_SWIPED_ITEM_ICON_MARGIN_KEY, 0f)
            behindSwipedItemCenterIcon = state.getBoolean(BEHIND_SWIPED_ITEM_CENTER_ICON_KEY, false)
            behindSwipedItemBackgroundColor = state.getInt(BEHIND_SWIPED_ITEM_BACKGROUND_COLOR_KEY, Color.TRANSPARENT)
            behindSwipedItemBackgroundSecondaryColor = state.getInt(BEHIND_SWIPED_ITEM_BACKGROUND_SECONDARY_COLOR_KEY, Color.TRANSPARENT)
            behindSwipedItemLayoutId = state.getInt(BEHIND_SWIPED_ITEM_LAYOUT_ID_KEY, 0)
            behindSwipedItemSecondaryLayoutId = state.getInt(BEHIND_SWIPED_ITEM_SECONDARY_LAYOUT_ID_KEY, 0)
            reduceItemAlphaOnSwiping = state.getBoolean(REDUCE_ITEM_ALPHA_ON_SWIPING_KEY, false)
            numOfColumnsPerRowInGridList = state.getInt(NUM_OF_COLUMNS_PER_ROW_IN_GRID_LIST_KEY, 1)
            numOfRowsPerColumnInGridList = state.getInt(NUM_OF_ROWS_PER_COLUMN_IN_GRID_LIST_KEY, 1)
            val savedOrientationName = state.getString(ORIENTATION_NAME_KEY, null)
            val savedOrientationDragFlags = state.getInt(ORIENTATION_DRAG_FLAGS_KEY, 0)
            val savedOrientationSwipeFlags = state.getInt(ORIENTATION_SWIPE_FLAGS_KEY, 0)
            if (savedOrientationName != null && savedOrientationName.isNotEmpty()) {
                val auxOrientation = ListOrientation.valueOf(savedOrientationName)
                auxOrientation.restoreFlags(savedOrientationDragFlags, savedOrientationSwipeFlags)
                orientation = auxOrientation
            }
        }

        super.onRestoreInstanceState(superState)
    }

    companion object {
        const val SUPER_STATE_KEY = "super_state"
        const val ITEM_LAYOUT_ID_KEY = "item_layout_id"
        const val DIVIDER_DRAWABLE_ID_KEY = "divider_drawable_id"
        const val BEHIND_SWIPED_ITEM_ICON_DRAWABLE_ID_KEY = "behind_swiped_item_icon_drawable_id"
        const val BEHIND_SWIPED_ITEM_ICON_SECONDARY_DRAWABLE_ID_KEY = "behind_swiped_item_icon_secondary_drawable_id"
        const val BEHIND_SWIPED_ITEM_ICON_MARGIN_KEY = "behind_swiped_item_icon_margin"
        const val BEHIND_SWIPED_ITEM_CENTER_ICON_KEY = "behind_swiped_item_center_icon"
        const val BEHIND_SWIPED_ITEM_BACKGROUND_COLOR_KEY = "behind_swiped_item_background_color"
        const val BEHIND_SWIPED_ITEM_BACKGROUND_SECONDARY_COLOR_KEY = "behind_swiped_item_background_secondary_color"
        const val BEHIND_SWIPED_ITEM_LAYOUT_ID_KEY = "behind_swiped_item_layout_id"
        const val BEHIND_SWIPED_ITEM_SECONDARY_LAYOUT_ID_KEY = "behind_swiped_item_secondary_layout_id"
        const val REDUCE_ITEM_ALPHA_ON_SWIPING_KEY = "reduce_item_alpha_on_swiping"
        const val NUM_OF_COLUMNS_PER_ROW_IN_GRID_LIST_KEY = "num_of_columns_per_row_in_grid_list"
        const val NUM_OF_ROWS_PER_COLUMN_IN_GRID_LIST_KEY = "num_of_rows_per_column_in_grid_list"
        const val ORIENTATION_NAME_KEY = "orientation_name"
        const val ORIENTATION_DRAG_FLAGS_KEY = "orientation_drag_flags"
        const val ORIENTATION_SWIPE_FLAGS_KEY = "orientation_swipe_flags"
    }
}