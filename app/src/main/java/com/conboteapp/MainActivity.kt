package com.conboteapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.widget.Toast
import com.conboteapp.Services.BubbleNoteService
import android.widget.SeekBar


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val mSpringTensionSlider: SeekBar? = null
    private val mSpringFrictionSlider: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1000)

        }

        button.setOnClickListener {
            val i = Intent(this, BubbleNoteService::class.java)
            startService(i)
            //changeActivity(this, FloatActivity())
        }
        mSpringTensionSlider?.progress = BubbleNoteService.sSpringTension
        mSpringFrictionSlider?.progress = BubbleNoteService.sSpringFriction

        mSpringTensionSlider?.setOnSeekBarChangeListener(this)
        mSpringFrictionSlider?.setOnSeekBarChangeListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1000){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Se ha otorgado los permisos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Permiso denegado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun AppCompatActivity.changeActivity(context: Context, activity: Activity){

        val intent = Intent(context, activity::class.java)
        startActivity(intent)

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar == mSpringTensionSlider) {
            BubbleNoteService.sSpringTension = progress
        } else if (seekBar == mSpringFrictionSlider) {
            BubbleNoteService.sSpringFriction = progress
        }
        BubbleNoteService.setSpringConfig()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }



}
