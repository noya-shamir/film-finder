package com.ponap.filmfinder.ui

import android.os.SystemClock
import android.view.View

/**
 * extension method for ignoring multiple taps, using throttle-first mechanism.
 * To use it, define the throttle interval (or rely on the default, set to 400ms).
 * Any click between the last click time + throttleInterval will be ignored
 */
inline fun View.onThrottleFirstClickListener(
    throttleInterval: Int = 400,
    crossinline listener: (View) -> Unit
) {
    var lastClickedTime = 0L

    setOnClickListener {
        if (SystemClock.elapsedRealtime() <= (lastClickedTime + throttleInterval)) {
            // not enough time passed, ignore the click
            return@setOnClickListener
        }
        lastClickedTime = SystemClock.elapsedRealtime()
        listener(it)
    }
}