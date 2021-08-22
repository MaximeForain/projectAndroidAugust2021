package com.example.myapplication.repositories.web.dto


// .---------------------------------------------------------------------.
// |                               UserDto                               |
// '---------------------------------------------------------------------'
data class UserDto(val email: String,
                   val password: String,
                   val username: String,
                   val phonenumber: String,
                   val gender: String){
}