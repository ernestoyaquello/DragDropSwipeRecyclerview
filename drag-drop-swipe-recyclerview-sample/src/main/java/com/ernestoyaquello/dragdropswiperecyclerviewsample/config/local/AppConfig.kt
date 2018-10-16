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
        ListFragmentConfig(true, true, true, false),
        ListFragmentConfig(false, false, true, true),
        ListFragmentConfig(false, false, true, true)
)

var currentListFragmentType = ListFragmentType.VERTICAL
val currentListFragmentConfig
    get() = listFragmentConfigurations[currentListFragmentType.index]