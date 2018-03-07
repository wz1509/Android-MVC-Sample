package com.wazing.mvcsample.model

import android.content.Context

interface GankModel {

    fun showLoading(context: Context)

    fun hideLoading()

    fun getGankList()

}