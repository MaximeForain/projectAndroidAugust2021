package com.example.myapplication.repositories.web.dto


// .---------------------------------------------------------------------.
// |                             ReviewDto                               |
// '---------------------------------------------------------------------'
data class ReviewDto(val reviewid: Int,
                     val reviewdegree: Int,
                     val customer_id: Int,
                     val bar_id: Int) {
}