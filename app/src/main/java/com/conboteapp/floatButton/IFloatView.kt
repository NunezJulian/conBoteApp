package com.conboteapp.floatButton

interface IFloatView {

    fun onDataSuccessFromApi(message: String)

    fun onDataErrorFromApi(throwable: Throwable)

}