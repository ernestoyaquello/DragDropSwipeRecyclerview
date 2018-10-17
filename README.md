# Drag-Drop-Swipe-Recyclerview
Android library written in Kotlin that extends `RecyclerView` to include extra features, such as support for gestures like *drag & drop* and *swipe*.

## Demo
![Drag & drop and swipe recycler view; demo with vertical list](https://raw.githubusercontent.com/ernestoyaquello/drag-drop-swipe-recyclerview/master/drag-drop-swipe-list-demo.gif)

![Drag & drop and swipe recycler view; demo with grid list](https://raw.githubusercontent.com/ernestoyaquello/drag-drop-swipe-recyclerview/master/drag-drop-swipe-list-demo2.gif)

## How to use it
### 1. Reference the library
Add the library to your project via Gradle:

```
dependencies {
    implementation 'com.ernestoyaquello.dragdropswiperecyclerview:drag-drop-swipe-recyclerview:0.0.1'
}
```

**NOTE**: Make sure you are using **AndroidX** instead of the old support libraries; otherwise this library might not work.

### 2. Add the list to your layout
Place the `DragDropSwipeRecyclerview` inside your layout using XML:

```xml
<!-- layout/view_layout.xml -->
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:item_layout="@layout/list_item_layout"
        app:divider="@drawable/list_divider"/>

</FrameLayout>
```

As you can see in the code above, **we specify the list item layout through the attribute `item_layout`**. This way, the library will take care of inflating the item layout automatically, so you won't have to do it manually in the adapter.

In addition, in this example you can also see that the optional attribute `divider` is being used to specify the drawable that will be displayed between list items (for more information about available attributes, see [**Customization**](#customization)).

> #### Referenced resource files
> Just in case they are of any help, these are the example resource files referenced in the code above:
> 
> ```xml
> <!-- layout/list_item_layout.xml -->
> <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
>     android:layout_width="match_parent"
>     android:layout_height="wrap_content"
>     android:orientation="horizontal"
>     android:gravity="center"
>     android:padding="16dp"
>     android:background="#eeeeee">
>     <TextView
>         android:id="@+id/item_text"
>         android:layout_width="0dp"
>         android:layout_weight="1"
>         android:layout_height="wrap_content"/>
>     <ImageView
>         android:id="@+id/drag_icon"
>         android:layout_width="wrap_content"
>         android:layout_height="match_parent"
>         android:src="@drawable/ic_drag"/>
> </LinearLayout>
> ```
> 
> ```xml
> <!-- drawable/list_divider.xml -->
> <shape xmlns:android="http://schemas.android.com/apk/res/android"
>     android:shape="rectangle">
>     <size android:height="1dp" />
>     <solid android:color="#e1e1e1" />
> </shape>
> ```
> 
> ```xml
> <!-- drawable/ic_drag.xml -->
> <vector xmlns:android="http://schemas.android.com/apk/res/android"
>     android:width="24dp"
>     android:height="24dp"
>     android:viewportHeight="24.0"
>     android:viewportWidth="24.0">
>     <path
>         android:fillColor="#333333"
>         android:pathData="M7,19V17H9V19H7M11,19V17H13V19H11M15,19V17H17V19H15M7,15V13H9V15H7M11,15V13H13V15H11M15,15V13H17V15H15M7,11V9H> 9V11H7M11,11V9H13V11H11M15,11V9H17V11H15M7,7V5H9V7H7M11,7V5H13V7H11M15,7V5H17V7H15Z" />
> </vector>
> ```

### 3. Create the adapter

The next step is to implement your adapter (and viewholder) by **extending the class `DragDropSwipeAdapter<T, U>`**, where `T` will be your item type and `U` will be your viewholder type:

```kotlin
class MyAdapter(dataSet: List<String> = emptyList())
    : DragDropSwipeAdapter<String, MyAdapter.ViewHolder>(dataSet) {
    
    class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)
    }

    override fun getViewHolder(itemLayout: View) = MyAdapter.ViewHolder(itemLayout)

    override fun onBindViewHolder(item: String, viewHolder: MyAdapter.ViewHolder, position: Int) {
        // Here we update the contents of the view holder's views to reflect the item's data
        viewHolder.itemText.text = item
    }

    override fun getViewToTouchToStartDraggingItem(item: String, viewHolder: MyAdapter.ViewHolder, position: Int): View? {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon 
    }
}
```

This is just a basic implementation, but there are more methods in the adapter that you can override to customise the list and its behaviour (see [**Customization**](#customization)).

### 4. Setup the list
Finally, you should setup the list to make it work and take advantage of its features.

#### Set up the adapter
Inside `onCreate` or `onCreateView`, find the list and set it with a layout manager and your adapter:

```kotlin
val dataSet = listOf("Item 1", "Item 2", "Item 3")
mAdapter = MyAdapter(dataSet)
mList = findViewById(R.id.list)
mList.layoutManager = LinearLayoutManager(this)
mList.adapter = mAdapter

```

#### Set up the orientation
Then, specify the list orientation. For example:

```kotlin
mList.orientation = DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
```

> ##### Using grid orientation with dividers
> Take into account that if *and only if* you want to show dividers in a list with a grid orientation, you also need to set one of these two properties:
> 
> ```kotlin
> // Set this property if your grid can be scrolled vertically
> mList.numOfColumnsPerRowInGridList = <numberOfColumns>
> ```
> 
> Or:
> 
> ```kotlin
> // Set this property if your grid can be scrolled horizontally
> mList.numOfRowsPerColumnInGridList = <numberOfRows>
> ```

#### Set event listeners
Finally, create event listeners for the events you want to handle. For example, these are the listeners for actions of *swiping*, *dragging & dropping* and *scrolling*:

```kotlin
private val onItemSwipeListener = object : OnItemSwipeListener<String> {
    override fun onItemSwiped(position: Int, direction: OnItemSwipeListener.SwipeDirection, item: String) {
        // Handle action of item swiped
    }
}

private val onItemDragListener = object : OnItemDragListener<String> {
    override fun onItemDragged(previousPosition: Int, newPosition: Int, item: String) {
        // Handle action of item being dragged from one position to another
    }

    override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: String) {
        // Handle action of item dropped
    }
}

private val onListScrollListener = object : OnListScrollListener {
    override fun onListScrolled(scrollDirection: OnListScrollListener.ScrollDirection, distance: Int) {
        // Handle scrolling
    }
}
```

Then, set the listeners...

```kotlin
mList.swipeListener = onItemSwipeListener
mList.dragListener = onItemDragListener
mList.scrollListener = onListScrollListener
```

And that's it! Your list with support for *swipe* and *drag & drop* should be fully working now.

## Customization

TBC

## Complete Example
Check out the **Sample App** that is included in this repository: it has vertical lists, horizontal lists and grid lists, and it makes use of most of the library's features.

## Contribution
Feel free to contribute to this library, any help will be welcomed!

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
