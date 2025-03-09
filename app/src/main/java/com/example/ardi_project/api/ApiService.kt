package com.example.ardi_project.api

import com.example.ardi_project.models.Friend
import com.example.ardi_project.models.Note
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // 📝 NOTE CRUD
    @GET("notes")
    fun getNotes(): Call<List<Note>>

    @POST("notes")
    @FormUrlEncoded
    fun addNote(
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Note>

    @PUT("notes/{id}")
    @FormUrlEncoded
    fun updateNote(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Note>

    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") id: Int): Call<Void>

    // 👥 FRIEND CRUD
    @GET("friends")
    fun getFriends(): Call<List<Friend>>

    @POST("friends")
    @FormUrlEncoded
    fun addFriend(
        @Field("name") name: String,
        @Field("birth_date") birthDate: String,
        @Field("quote_title") quoteTitle: String?,
        @Field("quote_content") quoteContent: String?,
        @Field("image") image: String? // Hanya path gambar
    ): Call<Friend>

    @PUT("friends/{id}")
    @FormUrlEncoded
    fun updateFriend(
        @Path("id") id: Int,
        @Field("name") name: String,
        @Field("birth_date") birthDate: String,
        @Field("quote_title") quoteTitle: String?,
        @Field("quote_content") quoteContent: String?,
        @Field("image") image: String? // Hanya path gambar
    ): Call<Friend>

    @DELETE("friends/{id}")
    fun deleteFriend(@Path("id") id: Int): Call<Void>
}
