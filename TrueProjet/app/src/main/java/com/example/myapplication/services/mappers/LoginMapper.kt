package com.example.myapplication.services.mappers

import com.example.myapplication.model.Login
import com.example.myapplication.repositories.web.dto.LoginDto

object LoginMapper {

    fun mapToLoginDto(login : Login) : LoginDto {
        return LoginDto(login.email, login.password)
    }
}