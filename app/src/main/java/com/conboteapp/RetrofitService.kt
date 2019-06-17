package com.conboteapp

import com.conboteapp.floatButton.model.ImageBase
import com.conboteapp.floatButton.model.ResponseImage
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface RetrofitService {

    @POST("/get-image")
    fun getAnswers(@Body base64: ImageBase): Call<ResponseImage>

    companion object{
        fun create(): RetrofitService {


            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost:8000") //TODO agregar url correcta
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }
}