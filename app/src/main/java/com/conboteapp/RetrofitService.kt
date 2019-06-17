package com.conboteapp

import com.conboteapp.floatButton.model.ImageBase
import com.conboteapp.floatButton.model.ResponseImage
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface RetrofitService {

    @GET("/api/jsonBlob/{id}")
    fun getAnswers(@Path("id") id: String): Call<ResponseImage>

    companion object{
        fun create(): RetrofitService {


            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://www.jsonblob.com") //TODO agregar url correcta
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }
}