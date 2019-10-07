package com.ernestoyaquello.dragdropswiperecyclerviewsample.config.local

enum class ListFragmentType(val index: Int, val tag: String) {
    VERTICAL(0, "VerticalFragment"),
    HORIZONTAL(1, "HorizontalFragment"),
    GRID(2, "GridFragment")
}

data class ListFragmentConfig(
        var isUsingStandardItemLayout: Boolean,
        var isRestrictingDraggingDirections: Boolean,
        var isDrawingBehindSwipedItems: Boolean,
        var isUsingFadeOnSwipedItems: Boolean)

private val listFragmentConfigurations = listOf(

        // Initial state of the vertical-list fragment
        ListFragmentConfig(
                isUsingStandardItemLayout = true,
                isRestrictingDraggingDirections = true,
                isDrawingBehindSwipedItems = true,
                isUsingFadeOnSwipedItems = false),

        // Initial state of the horizontal-list fragment
        ListFragmentConfig(
                isUsingStandardItemLayout = false,
                isRestrictingDraggingDirections = false,
                isDrawingBehindSwipedItems = true,
                isUsingFadeOnSwipedItems = true),

        // Initial state of the grid-list fragment
        ListFragmentConfig(
                isUsingStandardItemLayout = false,
                isRestrictingDraggingDirections = false,
                isDrawingBehindSwipedItems = true,
                isUsingFadeOnSwipedItems = true)
)

var currentListFragmentType = ListFragmentType.VERTICAL
val currentListFragmentConfig
    get() = listFragmentConfigurations[currentListFragmentType.index]