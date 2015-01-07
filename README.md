DragLinearLayout
================

![Dragging and swapping children views.](/sample/sample_in_action.gif)

An Android `LinearLayout` that supports draggable and swappable child `View`s.

Why?
----
Why bother doing drag & swap in a `LinearLayout` [when][drag_list_1] [there are][drag_list_2]
[so many][drag_list_3] [solutions][drag_list_4] for `ListView`?

1. *Simplicity* - no need for `ListAdapter`s. By default, works like a `LinearLayout`.
2. *Flexibility* - supports heterogeneous, selectively draggable (or not draggable), children.
3. *Portability* - can be used in any layout, e.g. as a child in a `ScrollView` container.

Usage
-----
Add it to your project using Gradle:

    compile 'com.jmedeisis:draglinearlayout:1.0.0'

The `DragLinearLayout` can be used in place of any `LinearLayout`. However, by default, children
will not be draggable. To set an existing `View` as draggable, use
`DragLinearLayout#setViewDraggable(View, View)`, passing in the child `View` and a (non-null!)
`View` that will act as the handle for dragging it (this can be the `View` itself).

XML layout file:

    <com.jmedeisis.draglinearlayout.DragLinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/text" />
    
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:scaleType="centerCrop"
            android:src="@drawable/image"/>
    
        <Button
            android:id="@+id/button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/button_text"/>
            
    </com.jmedeisis.draglinearlayout.DragLinearLayout>
    
Enabling drag & swap for all child views:

    DragLinearLayout dragLinearLayout = (DragLinearLayout) findViewById(R.id.container);
    for(int i = 0; i < dragLinearLayout.getChildCount(); i++){
        View child = dragLinearLayout.getChildAt(i);
        // the child will act as its own drag handle
        dragLinearLayout.setViewDraggable(child, child);
    }

Use `#addDragView(View, View)`,`#addDragView(View, View, int)` and `#removeDragView(View)` to
manage draggable children dynamically:

    final View view = View.inflate(context, R.layout.view_layout, null);
    dragLinearLayout.addDragView(view, view.findViewById(R.id.view_drag_handle));
    
    // ..
    
    dragLinearLayout.removeDragView(view);

Attach an `OnViewSwapListener` with `#setOnViewSwapListener(OnViewSwapListener)` to detect changes
to the ordering of child `View`s:

    dragLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
        @Override
        public void onSwap(View firstView, int firstPosition,
                View secondView, int secondPosition) {
            // update data, etc..
        }
    });

When placing the `DragLinearLayout` inside a `ScrollView`, call `#setContainerScrollView(ScrollView)`
to enable the user to scroll while dragging a child view.

For best visual results, use children that have opaque backgrounds. Furthermore, do not use
horizontal padding for the `DragLinearLayout`; instead, let children apply their own horizontal
padding.

Refer to the included sample activity project for a demonstration of the above usage techniques
and more.

Limitations
-----------
- Supports only the `LinearLayout#VERTICAL` orientation.
- Works for API levels 7+ with a dependency on [NineOldAndroids](http://nineoldandroids.com/).
For API levels 11+, the dependency could be removed by replacing all
`com.nineoldandroids.animation.*` imports with their identically named `android.animation.*`
counterparts.

License
-------
This project is licensed under the terms of the MIT license.
You may find a copy of the license in the included `LICENSE` file.

[drag_list_1]: https://github.com/bauerca/drag-sort-listview
[drag_list_2]: https://plus.google.com/u/0/+AndroidDevelopers/posts/7Qo9vmeqKwC
[drag_list_3]: http://ericharlow.blogspot.com/2010/10/experience-android-drag-and-drop-list.html
[drag_list_4]: https://github.com/terlici/DragNDropList
