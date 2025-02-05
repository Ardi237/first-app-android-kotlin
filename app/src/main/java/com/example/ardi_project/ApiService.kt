package com.example.ardi_project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("logout")
    fun logout(): Call<Void>
}