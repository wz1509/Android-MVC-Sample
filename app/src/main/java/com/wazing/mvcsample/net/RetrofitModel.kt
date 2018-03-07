package com.wazing.mvcsample.net

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.SSLSocketFactory
import java.util.concurrent.TimeUnit

class RetrofitModel {

    fun <T> createApi(clazz: Class<T>, url: String): T {
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(OkHttpClient.Builder()
                        .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(clazz)
    }

}

