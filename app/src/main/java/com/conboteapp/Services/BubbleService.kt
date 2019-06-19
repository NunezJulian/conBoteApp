package com.conboteapp.Services

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.conboteapp.R
import com.conboteapp.Services.bubbles.BubbleLayout
import com.conboteapp.Services.bubbles.BubblesManager

class BubbleService{

    var bubblesManager: BubblesManager? = null


    @RequiresApi(Build.VERSION_CODES.O)
    fun initBubble(context: Context, button: Button, window: Window, screenshotManager: ScreenshotManager) {
        bubblesManager = BubblesManager.Builder(context)
            .setTrashLayout(R.layout.bubble_remove)
            .setInitializationCallback { addNewBubble(context, button, window, screenshotManager) }.build()
        bubblesManager!!.initialize()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewBubble(context: Context, button: Button, window: Window, screenshotManager: ScreenshotManager) {
        val bubbleView = LayoutInflater.from(context)
            .inflate(R.layout.bubble_layout, null) as BubbleLayout

        bubbleView.setOnBubbleRemoveListener {
            button.isEnabled = true
            Toast.makeText(context, "Bye bye!", Toast.LENGTH_SHORT).show()
            bubblesManager!!.recycle()
        }

        bubbleView.setOnBubbleClickListener {

            screenshotManager.takeScreenshot(context)
        }

        bubbleView.setShouldStickToWall(true)
        bubblesManager!!.addBubble(bubbleView, 60, 20)
    }
}