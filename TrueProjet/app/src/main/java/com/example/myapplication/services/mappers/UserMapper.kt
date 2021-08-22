package com.example.myapplication.services.mappers

import com.example.myapplication.model.User
import com.example.myapplication.repositories.web.dto.UserDto


// .---------------------------------------------------------------------.
// |                            UserMapper                               |
// '---------------------------------------------------------------------'
object UserMapper {
    fun mapToUserDto(user : User?) : UserDto? {
        if (user == null) return null
        else {
            return UserDto(
                    user.email,
                    user.password,
                    user.username,
                    user.phonenumber,
                    user.gender)
        }
    }

    fun mapToUser(userDto: UserDto) : User {
        return User(
                userDto.email,
                userDto.password,
                userDto.username,
                userDto.phonenumber,
                userDto.gender)
    }
}