package com.conboteapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.widget.Toast
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import android.support.annotation.RequiresApi
import com.conboteapp.Services.BubbleService
import com.conboteapp.floatButton.IFloatView


class MainActivity : AppCompatActivity(), IFloatView {

    private var bubbleService: BubbleService? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        checkPermissions()
        bubbleService = BubbleService()
        button.setOnClickListener {
            bubbleService!!.initBubble(this, button, window)
            button.isEnabled = false
        }
    }

    fun checkPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1000)

        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!canDrawOverlays(this@MainActivity)) {
                val intent = Intent(
                    ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1000)
            }
        } else {
            val intent = Intent(this@MainActivity, Service::class.java)
            startService(intent)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        bubbleService = BubbleService()
        bubbleService!!.bubblesManager!!.recycle()
    }

    override fun onDataSuccessFromApi(message: String) {
        Toast.makeText(this, "SERVICIO CONECTADO", Toast.LENGTH_LONG).show()
    }

    override fun onDataErrorFromApi(throwable: Throwable) {
        Toast.makeText(this, "SERVICIO FALLIDO, VERIFIQUE SU CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show()
    }

}
