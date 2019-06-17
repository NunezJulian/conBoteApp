package com.conboteapp.floatButton

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.conboteapp.R
import com.conboteapp.floatButton.model.ImageBase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class FloatActivity : AppCompatActivity(), IFloatView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.floating_widget_layout)
        supportActionBar?.hide()
        button.setOnClickListener {

            val rootView = window.decorView.findViewById<View>(android.R.id.content)

            val bitmap = screenshot(rootView)
            val base64 = save(bitmap)

            val imageBase = ImageBase(base64)
            //ConBotePresenter(this).getDataFromApi(imageBase) TODO llamar servicio
        }
    }

    fun screenshot(view: View): Bitmap {

        val screenView = view.getRootView()
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun save(bm: Bitmap): String{

        val fos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG,100, fos)
        val base = toBase64(fos)

        val bitmapEncode = encodeImage(base)
        storeImage(bitmapEncode,"screenshot.png")

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

    fun storeImage(bm: Bitmap, filename: String){
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
            Toast.makeText(this,"Pantalla guardada", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this,"Error en el guardado de captura", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDataErrorFromApi(throwable: Throwable) {
        Toast.makeText(this, "bad", Toast.LENGTH_LONG).show()
    }

    override fun onDataSuccessFromApi(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}