package com.ernestoyaquello.dragdropswiperecyclerview.util

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View

internal fun drawHorizontalDividers(itemLayout: View, itemParent: View, canvas: Canvas, divider: Drawable, left: Int? = null, right: Int? = null, alpha: Float? = null) {
    val itemParams = itemLayout.layoutParams as RecyclerView.LayoutParams
    val dividerLeft = (left ?: itemLayout.left + itemLayout.translationX.toInt()) - itemParams.leftMargin
    val dividerRight = (right ?: itemLayout.right + itemLayout.translationX.toInt()) + itemParams.rightMargin

    // Restore alpha to normal and then set it to a different value if required
    divider.alpha = 255
    if (alpha != null)
        divider.alpha = (alpha * 255).toInt()

    // Draw the bottom divider
    val bottomDividerTop = itemLayout.bottom + itemParams.bottomMargin + itemLayout.translationY.toInt()
    val bottomDividerBottom = bottomDividerTop + divider.intrinsicHeight
    divider.setBounds(dividerLeft, bottomDividerTop, dividerRight, bottomDividerBottom)
    divider.draw(canvas)

    // Draw the top divider
    val topDividerBottom = itemLayout.top - itemParams.topMargin + itemLayout.translationY.toInt()
    val topDividerTop = topDividerBottom - divider.intrinsicHeight
    divider.setBounds(dividerLeft, topDividerTop, dividerRight, topDividerBottom)
    divider.draw(canvas)
}

internal fun drawVerticalDividers(itemLayout: View, itemParent: View, canvas: Canvas, divider: Drawable, top: Int? = null, bottom: Int? = null, alpha: Float? = null) {
    val itemParams = itemLayout.layoutParams as RecyclerView.LayoutParams
    val dividerTop = (top ?: itemLayout.top + itemLayout.translationY.toInt()) - itemParams.topMargin
    val dividerBottom = (bottom ?: itemLayout.bottom + itemLayout.translationY.toInt()) + itemParams.bottomMargin

    // Restore alpha to normal and then set it to a different value if required
    divider.alpha = 255
    if (alpha != null)
        divider.alpha = (alpha * 255).toInt()

    // Draw the right divider
    val rightDividerLeft = itemLayout.right + itemParams.rightMargin + itemLayout.translationX.toInt()
    val rightDividerRight = rightDividerLeft + divider.intrinsicWidth
    divider.setBounds(rightDividerLeft, dividerTop, rightDividerRight, dividerBottom)
    divider.draw(canvas)

    // Draw the left divider
    val leftDividerRight = itemLayout.left - itemParams.leftMargin + itemLayout.translationX.toInt()
    val leftDividerLeft = leftDividerRight - divider.intrinsicWidth
    divider.setBounds(leftDividerLeft, dividerTop, leftDividerRight, dividerBottom)
    divider.draw(canvas)
}