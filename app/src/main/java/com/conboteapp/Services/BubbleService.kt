package com.conboteapp.Services

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.conboteapp.R
import com.conboteapp.floatButton.model.ImageBase
import com.txusballesteros.bubbles.BubbleLayout
import com.txusballesteros.bubbles.BubblesManager

class BubbleService{

    var bubblesManager: BubblesManager? = null

    fun initBubble(context: Context, button: Button, window: Window) {
        bubblesManager = BubblesManager.Builder(context)
            .setTrashLayout(R.layout.bubble_remove)
            .setInitializationCallback { addNewBubble(context, button, window) }.build()
        bubblesManager!!.initialize()
    }

    fun addNewBubble(context: Context, button: Button,  window: Window) {
        val bubbleView = LayoutInflater.from(context)
            .inflate(R.layout.bubble_layout, null) as BubbleLayout

        bubbleView.setOnBubbleRemoveListener {
            button.isEnabled = true
            Toast.makeText(context, "Bye bye!", Toast.LENGTH_SHORT).show()
        }
        val screenshotservice = ScreenShotService()

        bubbleView.setOnBubbleClickListener {
            val rootView = window.decorView.findViewById<View>(android.R.id.content)

            val bitmap = screenshotservice.screenshot(rootView)
            val base64 = screenshotservice.save(bitmap, context)
            val imageBase = ImageBase(base64)
        }

        bubbleView.setShouldStickToWall(true)
        bubblesManager!!.addBubble(bubbleView, 60, 20)
    }
}