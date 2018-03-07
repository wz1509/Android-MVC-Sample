package com.wazing.mvcsample.net

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface GankService {

    companion object {
        var BASE_URL = "http://gank.io/"
    }

    @GET("api/data/{category}/{count}/{page}")
    fun getGankList(@Path("category") category: String,
                      @Path("count") count: Int,
                      @Path("page") page: Int): Observable<ResponseBody>
}