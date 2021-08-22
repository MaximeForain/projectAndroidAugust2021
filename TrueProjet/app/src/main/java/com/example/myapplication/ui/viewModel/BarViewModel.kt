package com.example.myapplication.ui.viewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.Error
import com.example.myapplication.model.ReviewToSend
import com.example.myapplication.model.Token
import com.example.myapplication.repositories.web.configuration.RetrofitConfigurationService
import com.example.myapplication.services.mappers.ReviewMapper
import com.example.myapplication.ui.fragment.BarFragment
import com.example.myapplication.utils.NoConnectivityException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// .---------------------------------------------------------------------.
// |                           BarViewModel                              |
// '---------------------------------------------------------------------'
class BarViewModel(application: Application) : AndroidViewModel(application) {

    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    private var webService = RetrofitConfigurationService.getInstance(application).webService()
    private var reviewMapper = ReviewMapper

    // .---------------------------------------------------------------------.
    // |                             addReview                               |
    // '---------------------------------------------------------------------'
    fun addReview(rivewToSend: ReviewToSend, token: Token){
        webService.postReview(reviewMapper.mapToReviewDto(rivewToSend),"Bearer " + token.token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                println(response.isSuccessful)
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
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }
        })
    }

    // .---------------------------------------------------------------------.
    // |                            addToFavorite                            |
    // '---------------------------------------------------------------------'
    fun addToFavorite(token: Token, barId: Int){
        webService.postFavorite(barId,"Bearer " + token.token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                println(response.isSuccessful)
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
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }
        })
    }

    // .---------------------------------------------------------------------.
    // |                          deleteToFavorite                           |
    // '---------------------------------------------------------------------'
    fun deleteToFavorite(token: Token, barId: Int){
        webService.deleteToFavorite(barId,"Bearer " + token.token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                println(response.isSuccessful)
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
                    println(t)
                    _error.value = Error.TECHNICAL_ERROR
                }
            }
        })
    }
}