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
import com.conboteapp.floatButton.FloatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1000)

        }

        button.setOnClickListener {
            changeActivity(this, FloatActivity())
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

    fun AppCompatActivity.changeActivity(context: Context, activity: Activity){

        val intent = Intent(context, activity::class.java)
        startActivity(intent)

    }
}
