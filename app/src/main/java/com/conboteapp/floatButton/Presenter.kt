package com.conboteapp.floatButton

import android.content.Context
import com.conboteapp.RetrofitService
import com.conboteapp.floatButton.model.ImageBase
import com.conboteapp.floatButton.model.ResponseImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConBotePresenter(context: Context){

    val login = context as IFloatView

    fun getDataFromApi(base: ImageBase){
        RetrofitService.create()
            .getAnswers(base)
            .enqueue(object : Callback<ResponseImage>{
                override fun onResponse(call: Call<ResponseImage>, response: Response<ResponseImage>) {
                    login.onDataSuccessFromApi(response.body()?.answer.toString())
                }
                override fun onFailure(call: Call<ResponseImage>, t: Throwable) {
                    login.onDataErrorFromApi(t)
                }
            })
    }

}