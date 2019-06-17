package com.conboteapp.Services

import android.annotation.SuppressLint
import com.conboteapp.R
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringListener
import com.facebook.rebound.SpringSystem
import com.facebook.rebound.SpringUtil



class BubbleNoteService : Service() {

    private var mWindowManager: WindowManager? = null
    private var mBubble: ViewGroup? = null
    private var mContent: View? = null

    private var mbExpanded = false
    private var mbMoved = false
    private val mPos = intArrayOf(0, -20)

    override fun onBind(intent: Intent): IBinder? {
        // Not used
        return null
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        //super.onStartCommand(intent, flags, startId)
        super.onCreate()

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBubble = inflater.inflate(R.layout.bubble, null, false) as ViewGroup

        mContent = mBubble!!.findViewById(R.id.content)
        mContent!!.scaleX = 0.0f
        mContent!!.scaleY = 0.0f
        val contentParams = mContent!!.layoutParams
        contentParams.width = Utils.getScreenWidth(this)
        contentParams.height = Utils.getScreenHeight(this) - resources.getDimensionPixelOffset(R.dimen.bubble_height)
        mContent!!.layoutParams = contentParams

        mBubble!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mContent!!.pivotX = (mBubble!!.findViewById<View>(R.id.bubble_container).width / 2).toFloat() // todo no sé
                if (Build.VERSION.SDK_INT >= 16) {
                    mBubble!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    mBubble!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })

        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = mPos[0]
        params.y = mPos[1]
        params.dimAmount = 0.6f


        val system = SpringSystem.create()
        val springConfig = SpringConfig(sSpringTension.toDouble(), sSpringFriction.toDouble())

        sContentSpring = system.createSpring()
        sContentSpring!!.springConfig = springConfig
        sContentSpring!!.currentValue = 0.0
        sContentSpring!!.addListener(object : SpringListener {
            override fun onSpringUpdate(spring: Spring) {
                val value = spring.currentValue.toFloat()
                val clampedValue = SpringUtil.clamp(value.toDouble(), 0.0, 1.0).toFloat()
                mContent!!.scaleX = value
                mContent!!.scaleY = value
                mContent!!.alpha = clampedValue
            }

            override fun onSpringAtRest(spring: Spring) {
                mContent!!.setLayerType(View.LAYER_TYPE_NONE, null)
                if (spring.currentValueIsApproximately(0.0)) {
                    hideContent()
                }
            }

            override fun onSpringActivate(spring: Spring) {
                mContent!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }

            override fun onSpringEndStateChange(spring: Spring) {

            }
        })

        sBubbleSpring = system.createSpring()
        sBubbleSpring!!.springConfig = springConfig
        sBubbleSpring!!.currentValue = 1.0
        sBubbleSpring!!.addListener(object : SpringListener {
            override fun onSpringUpdate(spring: Spring) {
                val value = spring.currentValue
                params.x = (SpringUtil.mapValueFromRangeToRange(value, 0.0, 1.0, 0.0, mPos[0].toDouble())).toInt()
                params.y = (SpringUtil.mapValueFromRangeToRange(value, 0.0, 1.0, 0.0, mPos[1].toDouble())).toInt()
                mWindowManager!!.updateViewLayout(mBubble, params)
                if (spring.isOvershooting && sContentSpring!!.isAtRest) {
                    sContentSpring!!.endValue = 1.0
                }
            }

            override fun onSpringAtRest(spring: Spring) {

            }

            override fun onSpringActivate(spring: Spring) {

            }

            override fun onSpringEndStateChange(spring: Spring) {

            }
        })


        mBubble!!.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mbMoved = false
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        showContent()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mbMoved) return true
                        if (!mbExpanded) {
                            mBubble!!.getLocationOnScreen(mPos)
                            mPos[1] -= Utils.getStatusBarHeight(this@BubbleNoteService)
                            params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                            params.flags = params.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            sBubbleSpring!!.endValue = 0.0
                        } else {
                            params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            params.flags = params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
                            sBubbleSpring!!.endValue = 1.0
                            sContentSpring!!.endValue = 0.0
                        }
                        mbExpanded = !mbExpanded
                        mWindowManager!!.updateViewLayout(mBubble, params)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = (event.rawX - initialTouchX).toInt()
                        val deltaY = (event.rawY - initialTouchY).toInt()
                        params.x = initialX + deltaX
                        params.y = initialY + deltaY
                        if (deltaX * deltaX + deltaY * deltaY >= MOVE_THRESHOLD) {
                            mbMoved = true
                            hideContent()
                            mWindowManager!!.updateViewLayout(mBubble, params)
                        }
                        return true
                    }
                }
                return false
            }
        })

        mWindowManager!!.addView(mBubble, params)
        //return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBubble != null) {
            mWindowManager!!.removeView(mBubble)
        }
    }

    private fun showContent() {
        mContent!!.visibility = View.VISIBLE
    }

    private fun hideContent() {
        mContent!!.visibility = View.GONE
    }

    companion object {

        val TAG = "BubbleNoteService"
        private val MOVE_THRESHOLD = 100  // square of the threshold distance in pixels

        private var sBubbleSpring: Spring? = null
        private var sContentSpring: Spring? = null
        var sSpringTension = 200
        var sSpringFriction = 20

        fun setSpringConfig() {
            val config = sBubbleSpring!!.springConfig
            config.tension = sSpringTension.toDouble()
            config.friction = sSpringFriction.toDouble()
        }
    }

}