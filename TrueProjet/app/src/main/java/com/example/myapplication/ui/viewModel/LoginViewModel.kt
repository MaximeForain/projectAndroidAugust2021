package com.example.myapplication.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.Error
import com.example.myapplication.model.Login
import com.example.myapplication.model.Token
import com.example.myapplication.repositories.web.configuration.RetrofitConfigurationService
import com.example.myapplication.repositories.web.dto.TokenDto
import com.example.myapplication.services.mappers.LoginMapper
import com.example.myapplication.services.mappers.TokenMapper
import com.example.myapplication.utils.NoConnectivityException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// .---------------------------------------------------------------------.
// |                          LoginViewModel                             |
// '---------------------------------------------------------------------'
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    private val _jwt : MutableLiveData<Token> = MutableLiveData()
    val jwt : LiveData<Token> = _jwt

    private var webService = RetrofitConfigurationService.getInstance(application).webService()
    private var loginMapper  = LoginMapper
    private  var tokenMapper = TokenMapper

    // .---------------------------------------------------------------------.
    // |                              loginUser                              |
    // '---------------------------------------------------------------------'
    fun loginUser(login : Login){
        webService.loginUser(loginMapper.mapToLoginDto(login)).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    _error.value = Error.NO_ERROR
                    _jwt.value = tokenMapper.mapToToken(response.body()!!)

                } else {
                    println("**********************************************")
                    println(response)
                    _error.value = Error.BAD_CREDENTIALS
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                if (t is NoConnectivityException) {
                    _error.value = Error.NO_CONNECTION
                } else {
                    println("**********************************************")
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }

        })
    }
}