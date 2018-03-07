package com.wazing.mvcsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.wazing.mvcsample.model.GankModel
import com.wazing.mvcsample.model.impl.GankModelImpl
import com.wazing.mvcsample.view.OnGankListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnGankListener {

    private lateinit var gankModel: GankModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gankModel = GankModelImpl(this)

        btn_request_data.setOnClickListener({
            gankModel.showLoading(this)
            gankModel.getGankList()
        })
    }

    override fun onSuccess(json: String) {
        tv_content.run {
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.default_text_color))
            text = json
        }
    }

    override fun onFailed(msg: String) {
        tv_content.text = msg
        tv_content.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark))
    }

    override fun onTerminate() {
        gankModel.hideLoading()
    }

}
