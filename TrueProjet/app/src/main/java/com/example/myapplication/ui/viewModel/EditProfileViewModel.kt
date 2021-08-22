package com.example.myapplication.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.model.Error
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.Token
import com.example.myapplication.model.User
import com.example.myapplication.repositories.web.configuration.RetrofitConfigurationService
import com.example.myapplication.services.mappers.UserMapper
import com.example.myapplication.utils.NoConnectivityException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// .---------------------------------------------------------------------.
// |                     EditProfileViewModel                            |
// '---------------------------------------------------------------------'
class EditProfileViewModel (application: Application) : AndroidViewModel(application) {

    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    private var webService = RetrofitConfigurationService.getInstance(application).webService()
    private var userMapper  = UserMapper

    // .---------------------------------------------------------------------.
    // |                            updateUser                               |
    // '---------------------------------------------------------------------'
    fun updateUser(user : User, token: Token){
        webService.putUser(userMapper.mapToUserDto(user)!!, "Bearer " + token.token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    _error.value = Error.NO_ERROR
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
                    System.out.println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }

        })
    }
}