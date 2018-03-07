package com.wazing.mvcsample.view

interface OnGankListener {

    fun onSuccess(json: String)

    fun onFailed(msg: String)

    fun onTerminate()
}