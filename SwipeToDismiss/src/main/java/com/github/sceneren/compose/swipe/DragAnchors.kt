package com.github.sceneren.compose.swipe

sealed class DragAnchors {
    data object Start : DragAnchors()

    data object Center : DragAnchors()

    data object End : DragAnchors()
}