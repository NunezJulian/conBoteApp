package com.conboteapp.Services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.view.View
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ScreenShotService {

    fun screenshot(view: View): Bitmap {

        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun save(bm: Bitmap, context: Context): String{

        val fos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG,100, fos)
        val base = toBase64(fos)

        val bitmapEncode = encodeImage(base)
        storeImage(bitmapEncode,"screenshot.png", context)

        return base
    }

    fun toBase64(bm: ByteArrayOutputStream): String{

        val b =  bm.toByteArray()

        val encodeImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encodeImage.toString()
    }

    fun encodeImage(encodedImage: String): Bitmap {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        return decodedByte!!
    }

    fun storeImage(bm: Bitmap, filename: String, context: Context){
        val dirPath = Environment.getExternalStorageDirectory().absolutePath

        val dir = File(filename)

        if(!dir.exists()){
            dir.mkdirs()
        }

        val file = File(dirPath, filename)
        try {
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG,100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(context,"Pantalla guardada", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(context,"Error en el guardado de captura", Toast.LENGTH_SHORT).show()
        }
    }
}