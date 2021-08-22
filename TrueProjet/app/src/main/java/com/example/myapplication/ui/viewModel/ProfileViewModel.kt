package com.example.myapplication.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.User
import com.example.myapplication.repositories.web.dto.UserDto
import com.example.myapplication.services.mappers.UserMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.model.Error
import com.example.myapplication.model.Token
import com.example.myapplication.repositories.web.configuration.RetrofitConfigurationService
import com.example.myapplication.utils.NoConnectivityException


// .---------------------------------------------------------------------.
// |                       ProfileViewModel                              |
// '---------------------------------------------------------------------'
class ProfileViewModel (application: Application) : AndroidViewModel(application){
    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    private var _user : MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private var _isDeleted : MutableLiveData<Boolean> = MutableLiveData()
    val isDeleted: LiveData<Boolean> = _isDeleted

    private var webService = RetrofitConfigurationService.getInstance(application).webService()
    private var userMapper  = UserMapper

    // .---------------------------------------------------------------------.
    // |                               getUser                               |
    // '---------------------------------------------------------------------'
    fun getUser(token: Token){
        webService.getUser("Bearer "+ token.token).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    _user.value = userMapper.mapToUser(response.body()!!)
                    _error.value = Error.NO_ERROR

                } else {
                    _error.value = Error.REQUEST_ERROR
                }
            }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                if (t is NoConnectivityException) {
                    _error.value = Error.NO_CONNECTION
                } else {
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }
        })
    }

    // .---------------------------------------------------------------------.
    // |                             deleteUser                              |
    // '---------------------------------------------------------------------'
    fun deleteUser(token: Token) {
        webService.deleteUser("Bearer "+ token.token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    _error.value = Error.NO_ERROR
                    _isDeleted.value = true
                } else {
                    if (response.code() == 409)
                        _error.value = Error.USER_ALREADY_EXIST
                    else
                        _error.value = Error.REQUEST_ERROR
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                if (t is NoConnectivityException) {
                    _error.value = Error.NO_CONNECTION
                } else {
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }
        })
    }
}