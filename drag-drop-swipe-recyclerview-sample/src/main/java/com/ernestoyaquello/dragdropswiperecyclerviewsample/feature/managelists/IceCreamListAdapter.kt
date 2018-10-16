package com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelists

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.widget.ImageViewCompat
import androidx.appcompat.widget.AppCompatImageView
import android.view.View
import android.widget.TextView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerviewsample.R
import com.ernestoyaquello.dragdropswiperecyclerviewsample.data.model.IceCream
import com.ernestoyaquello.dragdropswiperecyclerviewsample.util.Logger

/**
 * Adapter for a list of ice creams.
 */
class IceCreamListAdapter(dataSet: List<IceCream> = emptyList())
    : DragDropSwipeAdapter<IceCream, IceCreamListAdapter.ViewHolder>(dataSet) {

    class ViewHolder(iceCreamLayout: View) : DragDropSwipeAdapter.ViewHolder(iceCreamLayout) {
        val iceCreamNameView: TextView = itemView.findViewById(R.id.ice_cream_name)
        val iceCreamPriceView: TextView = itemView.findViewById(R.id.ice_cream_price)
        val dragIcon: AppCompatImageView = itemView.findViewById(R.id.drag_icon)
        val iceCreamIcon: AppCompatImageView? = itemView.findViewById(R.id.ice_cream_icon)
        val iceCreamPhotoFilter: View? = itemView.findViewById(R.id.ice_cream_photo_filter)
    }

    override fun getViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(item: IceCream, viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.itemView.context

        // Set ice cream name and price
        viewHolder.iceCreamNameView.text = item.name
        viewHolder.iceCreamPriceView.text = context.getString(R.string.priceFormat, item.price)

        // Set ice cream icon color
        val red = (item.colorRed * 255).toInt()
        val green = (item.colorGreen * 255).toInt()
        val blue = (item.colorBlue * 255).toInt()

        // Set the icon/image color
        if (viewHolder.iceCreamIcon != null) {
            val iceCreamIconColor = Color.rgb(red, green, blue)
            ImageViewCompat.setImageTintList(viewHolder.iceCreamIcon, ColorStateList.valueOf(iceCreamIconColor))
        } else if (viewHolder.iceCreamPhotoFilter != null) {
            val iceCreamPhotoFilter = Color.argb(128, red, green, blue)
            viewHolder.iceCreamPhotoFilter.setBackgroundColor(iceCreamPhotoFilter)
        }
    }

    override fun getViewToTouchToStartDraggingItem(item: IceCream, viewHolder: ViewHolder, position: Int) = viewHolder.dragIcon

    override fun onDragStarted(item: IceCream, viewHolder: IceCreamListAdapter.ViewHolder) {
        Logger.log("Dragging started on ${item.name}")
    }

    override fun onSwipeStarted(item: IceCream, viewHolder: IceCreamListAdapter.ViewHolder) {
        Logger.log("Swiping started on ${item.name}")
    }

    override fun onIsDragging(
            item: IceCream,
            viewHolder: IceCreamListAdapter.ViewHolder,
            offsetX: Int,
            offsetY: Int,
            canvasUnder: Canvas?,
            canvasOver: Canvas?,
            isUserControlled: Boolean) {
        // Call commented out to avoid saturating the log
        //Logger.log("The ${if (isUserControlled) "User" else "System"} is dragging ${item.name} (offset X: $offsetX, offset Y: $offsetY)")
    }

    override fun onIsSwiping(
            item: IceCream?,
            viewHolder: IceCreamListAdapter.ViewHolder,
            offsetX: Int,
            offsetY: Int,
            canvasUnder: Canvas?,
            canvasOver: Canvas?,
            isUserControlled: Boolean) {
        // Call commented out to avoid saturating the log
        //Logger.log("The ${if (isUserControlled) "User" else "System"} is swiping ${item?.name} (offset X: $offsetX, offset Y: $offsetY)")
    }

    override fun onDragFinished(item: IceCream, viewHolder: IceCreamListAdapter.ViewHolder) {
        Logger.log("Dragging finished on ${item.name} (the item was dropped)")
    }
    override fun onSwipeAnimationFinished(viewHolder: IceCreamListAdapter.ViewHolder) {
        Logger.log("Swiping animation finished")
    }
}