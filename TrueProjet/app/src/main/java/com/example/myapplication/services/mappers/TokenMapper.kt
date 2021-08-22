package com.example.myapplication.services.mappers

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.example.myapplication.model.Token
import com.example.myapplication.model.Value
import com.example.myapplication.repositories.web.dto.TokenDto
import java.util.*


// .---------------------------------------------------------------------.
// |                           TokenMapper                               |
// '---------------------------------------------------------------------'
object TokenMapper {
    fun mapToToken(tokenDto: TokenDto) : Token {
        val parsedJWT = JWT(tokenDto.token);
        val allClaims: Map<String, Claim> = parsedJWT.claims
        val value: Value? = allClaims.getValue("value").asObject(Value::class.java)
        val exp : Date? = parsedJWT.expiresAt
        val token = tokenDto.token
        val userId: String = value?.id.toString()

        return Token(exp, token, userId)
    }
}