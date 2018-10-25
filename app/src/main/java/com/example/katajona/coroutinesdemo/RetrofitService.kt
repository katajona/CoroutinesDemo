package com.example.katajona.coroutinesdemo

import io.reactivex.Single
import retrofit2.http.GET

interface RetrofitService {
    @GET("/api/books/1")
    fun getBookData(): Single<Book>
}