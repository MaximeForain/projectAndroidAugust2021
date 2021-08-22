package com.example.myapplication.ui.viewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.*
import com.example.myapplication.repositories.web.configuration.RetrofitConfigurationService
import com.example.myapplication.repositories.web.dto.BarDto
import com.example.myapplication.repositories.web.dto.FavoriteDto
import com.example.myapplication.repositories.web.dto.ReviewDto
import com.example.myapplication.services.mappers.BarMapper
import com.example.myapplication.services.mappers.FavoriteMapper
import com.example.myapplication.services.mappers.ReviewMapper
import com.example.myapplication.utils.NoConnectivityException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// .---------------------------------------------------------------------.
// |                          HomeViewModel                              |
// '---------------------------------------------------------------------'
class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    private var _filledBars: MutableLiveData<List<Bar>> = MutableLiveData()
    val filledBars: LiveData<List<Bar>> = _filledBars

    private var _isShowingFavoriteBars: MutableLiveData<Boolean> = MutableLiveData(false)
    val isShowingFavoriteBars: LiveData<Boolean> = _isShowingFavoriteBars

    private var _isShowingBetterBars: MutableLiveData<Boolean> = MutableLiveData(false)
    val isShowingBetterBars: LiveData<Boolean> = _isShowingBetterBars

    private lateinit var bars: List<Bar>
    private lateinit var reviews: List<Review>
    private lateinit var favorites: List<Favorite>
    private lateinit var userId: String

    private var webService = RetrofitConfigurationService.getInstance(application).webService()
    private var barMapper = BarMapper
    private var reviewMapper = ReviewMapper
    private var favoriteMapper = FavoriteMapper


    // .---------------------------------------------------------------------.
    // |                               getBars                               |
    // '---------------------------------------------------------------------'
    fun getBars(token: Token){
        webService.getBars("Bearer " + token.token).enqueue(object : Callback<List<BarDto>> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<List<BarDto>>, response: Response<List<BarDto>>) {
                if (response.isSuccessful) {
                    bars = barMapper.mapToBar(response.body()!!)
                    getAllReviews(token, bars)
                    _error.value = Error.NO_ERROR
                } else {
                    _error.value = Error.REQUEST_ERROR
                }
            }

            override fun onFailure(call: Call<List<BarDto>>, t: Throwable) {
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
    // |                           getAllReview                              |
    // '---------------------------------------------------------------------'
    fun getAllReviews(token: Token, bars: List<Bar>) {
        webService.getAllReview("Bearer " + token.token).enqueue(object :
            Callback<List<ReviewDto>> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<List<ReviewDto>>, response: Response<List<ReviewDto>>) {
                if (response.isSuccessful) {
                    reviews = reviewMapper.mapToReview(response.body()!!)
                    getFavorites(token, bars, reviews)
                    _error.value = Error.NO_ERROR
                } else {
                    _error.value = Error.REQUEST_ERROR
                }
            }

            override fun onFailure(call: Call<List<ReviewDto>>, t: Throwable) {
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
    // |                            getFavorite                              |
    // '---------------------------------------------------------------------'
    fun getFavorites(token: Token, bars: List<Bar>, reviews: List<Review>) {
        webService.getFavorite("Bearer " + token.token).enqueue(object : Callback<List<FavoriteDto>> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<List<FavoriteDto>>, response: Response<List<FavoriteDto>>) {
                if (response.isSuccessful) {
                    favorites = favoriteMapper.mapToFavorite(response.body()!!)
                    _filledBars.value = fillBar(bars, reviews, favorites)
                    _error.value = Error.NO_ERROR
                } else {
                    if (response.code() == 404) {
                        _filledBars.value = fillBar(bars, reviews, null)
                        _error.value = Error.NO_ERROR
                    } else {
                        _error.value = Error.REQUEST_ERROR
                    }

                }
            }

            override fun onFailure(call: Call<List<FavoriteDto>>, t: Throwable) {
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
    // |                               Utility                               |
    // '---------------------------------------------------------------------'
    private fun fillBar(bars: List<Bar>, reviews: List<Review>, favorites: List<Favorite>?): List<Bar> {
        for (bar in bars) {
            var sumDegree = 0
            var numberOfReview = 0

            for (review in reviews) {
                if (review.bar_id.toString() == bar.bar_id) {
                    sumDegree += review.reviewdegree
                    numberOfReview++

                    if (review.customer_id.toString() == this.userId) bar.isAlreadyEvaluatedByUseur = true
                }
            }

            bar.average         = if (sumDegree != 0 && numberOfReview != 0) sumDegree.toFloat() / numberOfReview else 0f
            bar.sumDegrees      = sumDegree
            bar.numberOfReview  = numberOfReview

            if (favorites != null) {
                for (favorite in favorites) {
                    if (favorite.favoritebar_id.toString() == bar.bar_id) bar.isFavorite = true
                }
            }
        }

        return bars
    }

    // .---------------------------------------------------------------------.
    // |                               Setters                               |
    // '---------------------------------------------------------------------'
    fun setUserId(id: String) { userId = id }

    fun setIsShowingFavoriteBars(value: Boolean) { _isShowingFavoriteBars.value = value }

    fun setIsShowingBetterBars(value: Boolean) { _isShowingBetterBars.value = value }
}

