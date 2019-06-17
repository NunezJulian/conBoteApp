package com.conboteapp.Services

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

object Utils {

    private var sScreenSize: Point? = null

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getScreenWidth(context: Context): Int {
        fetchScreenSize(context)
        return sScreenSize!!.x
    }

    fun getScreenHeight(context: Context): Int {
        fetchScreenSize(context)
        return sScreenSize!!.y
    }

    private fun fetchScreenSize(context: Context) {
        if (sScreenSize != null) return
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        sScreenSize = Point()
        display.getSize(sScreenSize)
    }

}