package com.minorproject.cloudgallery.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.minorproject.cloudgallery.interfaces.SwipeTouchListener


class OnSwipeTouchListener(ctx: Context?, swipeTouchListener: SwipeTouchListener) :
    OnTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener(swipeTouchListener))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private inner class GestureListener(val swipeTouchListener: SwipeTouchListener) :
        SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > Companion.SWIPE_THRESHOLD && Math.abs(
                            velocityX
                        ) > Companion.SWIPE_VELOCITY_THRESHOLD
                    ) {
                        if (diffX > 0) {
                            swipeTouchListener.onSwipeRight()
                        } else {
                            swipeTouchListener.onSwipeLeft()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > Companion.SWIPE_THRESHOLD && Math.abs(
                        velocityY
                    ) > Companion.SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (diffY > 0) {
                        swipeTouchListener.onSwipeBottom()
                    } else {
                        swipeTouchListener.onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }
}