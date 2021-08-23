package com.example.myapplication.model


// .---------------------------------------------------------------------.
// |                           Object User                               |
// '---------------------------------------------------------------------'
data class User (val email : String,
                 val password : String,
                 val username : String,
                 val phonenumber : String,
                 val gender : String?){
}