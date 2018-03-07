package com.wazing.mvcsample.model.impl

import android.content.Context
import android.support.v7.app.AlertDialog
import com.wazing.mvcsample.model.GankModel
import com.wazing.mvcsample.net.GankService
import com.wazing.mvcsample.net.RetrofitModel
import com.wazing.mvcsample.net.RxSchedulers
import com.wazing.mvcsample.view.OnGankListener

class GankModelImpl constructor(private val view: OnGankListener) : GankModel {

    private var loadingDialog: AlertDialog? = null

    override fun showLoading(context: Context) {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("请求中...")
            loadingDialog = builder.create()
        }
        loadingDialog!!.show()
    }

    override fun hideLoading() {
        if (loadingDialog != null)
            loadingDialog!!.dismiss()
    }

    override fun getGankList() {
        val api: GankService = RetrofitModel().createApi(GankService::class.java, GankService.BASE_URL)

        api.getGankList("Android", 5, 1)
                .compose(RxSchedulers.ioMain())
                .doOnTerminate {
                    view.onTerminate()
                }
                .subscribe({
                    view.onSuccess(it.string())
                }, {
                    view.onFailed(it.message!!)
                })
    }

}