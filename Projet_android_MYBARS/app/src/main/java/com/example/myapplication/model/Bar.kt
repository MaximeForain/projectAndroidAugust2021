package com.example.myapplication.model


// .---------------------------------------------------------------------.
// |                            Object Bar                               |
// '---------------------------------------------------------------------'
data class Bar (val bar_id: String,
                val barname: String,
                val description: String,
                val phonenumber: String,
                val hashtags: String,
                val webaddress: String,
                val address: String,
                val admin_id: String,
                var numberOfReview: Int,
                var sumDegrees: Int,
                var average: Float,
                var isFavorite: Boolean,
                var isAlreadyEvaluatedByUseur: Boolean) {
}