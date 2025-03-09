package com.example.ardi_project.models

data class Friend(
    val id: Int,
    val name: String,
    val birth_date: String,
    val quote_title: String?,
    val quote_content: String?,
    val image: String?
)
