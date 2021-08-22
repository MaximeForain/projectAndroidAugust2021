package com.example.myapplication.model

import java.util.*


// .---------------------------------------------------------------------.
// |                           Object Token                              |
// '---------------------------------------------------------------------'
data class Token(val expDate: Date?, val token: String, val userId: String) {
}