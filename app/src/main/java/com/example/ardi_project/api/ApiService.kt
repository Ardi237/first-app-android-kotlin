package com.example.ardi_project.api

import com.example.ardi_project.models.Note
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("notes")
    fun getNotes(): Call<List<Note>>

    @POST("notes")
    @FormUrlEncoded
    fun addNote(
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Note>

    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") id: Int): Call<Void>

    @PUT("notes/{id}")
    @FormUrlEncoded
    fun updateNote(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Note>
}