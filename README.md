DragLinearLayout
================

An Android `LinearLayout` that supports draggable and swappable child `View`s.

Usage
-----
The `DragLinearLayout` can be used in place of any `LinearLayout`. However, by default, children
will not be draggable. To set an existing `View` as draggable, use
`DragLinearLayout#setViewDraggable(View, View)`, passing in the child `View` and a (non-null!)
`View` that will act as the handle for dragging it (this can be the `View` itself).

Use `#addDragView(View, View)`,`#addDragView(View, View, int)` and `#removeDragView(View)` to
manage draggable children dynamically.

Attach an `OnViewSwapListener` with `#setOnViewSwapListener(OnViewSwapListener)` to detect changes
to the ordering of child `View`s.

A sample activity project will be included soon.

Limitations
-----------
- Only supports vertical `LinearLayout`s.
- Works for API levels 7+ with a dependency on [NineOldAndroids](http://nineoldandroids.com/).
For API levels 11+, the dependency could be removed by replacing all
`com.nineoldandroids.animation.*` imports with their identically named `android.animation.*`
counterparts.

License
-------
This project is licensed under the terms of the MIT license.
You may find a copy of the license in the included LICENSE file.