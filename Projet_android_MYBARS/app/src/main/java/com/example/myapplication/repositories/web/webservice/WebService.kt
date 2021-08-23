package com.example.myapplication.repositories.web.webservice

import com.example.myapplication.repositories.web.dto.*
import retrofit2.Call
import retrofit2.http.*

// .---------------------------------------------------------------------.
// |                            WebService                               |
// '---------------------------------------------------------------------'
interface WebService {

    // .---------------------------------------------------------------------.
    // |                                POST                                 |
    // '---------------------------------------------------------------------'
    @POST("/user")
    fun loginUser(@Body userDto: LoginDto) : Call<TokenDto>

    @POST("/customer")
    fun postUser(@Body userDto: UserDto) : Call<String>

    @POST("/review")
    fun postReview(@Body reviewDto: ReviewToSendDto, @Header("Authorization") authHeader : String) : Call<String>

    @POST("/favorite/{id}")
    fun postFavorite(@Path("id") barId: Int, @Header("Authorization") authHeader : String) : Call<String>

    // .---------------------------------------------------------------------.
    // |                                 GET                                 |
    // '---------------------------------------------------------------------'
    @GET("/customer")
    fun getUser(@Header("Authorization") authHeader : String): Call<UserDto>

    @GET("/bar")
    fun getBars(@Header("Authorization") authHeader : String): Call<List<BarDto>>

    @GET("/review")
    fun getAllReview(@Header("Authorization") authHeader : String): Call<List<ReviewDto>>

    @GET("/favorite")
    fun getFavorite(@Header("Authorization") authHeader : String): Call<List<FavoriteDto>>

    // .---------------------------------------------------------------------.
    // |                               PATCH                                 |
    // '---------------------------------------------------------------------'
    @PATCH("/customer")
    fun putUser(@Body userDto: UserDto, @Header("Authorization") authHeader : String): Call<String>

    // .---------------------------------------------------------------------.
    // |                              DELETE                                 |
    // '---------------------------------------------------------------------'
    @DELETE("/customer")
    fun deleteUser(@Header("Authorization") authHeader: String): Call<String>

    @DELETE("/favorite/{id}")
    fun deleteToFavorite(@Path("id") barId: Int, @Header("Authorization") authHeader: String): Call<String>
}