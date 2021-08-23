package com.example.myapplication.repositories.web.dto


// .---------------------------------------------------------------------.
// |                             BarDto                                  |
// '---------------------------------------------------------------------'
data class BarDto(val bar_id: String,
                val barname: String,
                val description: String,
                val phonenumber: String,
                val hashtags: String,
                val webaddress: String,
                val address: String,
                val admin_id: String) {
}