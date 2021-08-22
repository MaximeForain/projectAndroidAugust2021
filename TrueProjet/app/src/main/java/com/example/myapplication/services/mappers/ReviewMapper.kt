package com.example.myapplication.services.mappers

import com.example.myapplication.model.Review
import com.example.myapplication.model.ReviewToSend
import com.example.myapplication.repositories.web.dto.ReviewDto
import com.example.myapplication.repositories.web.dto.ReviewToSendDto


// .---------------------------------------------------------------------.
// |                            ReviewMapper                             |
// '---------------------------------------------------------------------'
object ReviewMapper {

    fun mapToReviewDto(rivewToSend: ReviewToSend): ReviewToSendDto {
        return ReviewToSendDto(rivewToSend.reviewdegree!!, rivewToSend.bar_id!!)
    }

    fun mapToReview(reviewsDto: List<ReviewDto>): List<Review> {
        val reviews: ArrayList<Review> = ArrayList()

        for (reviewDto in reviewsDto)  {
            reviews.add(Review(
                    reviewDto.reviewid,
                    reviewDto.reviewdegree,
                    reviewDto.customer_id,
                    reviewDto.bar_id))
        }

        return reviews
    }
}