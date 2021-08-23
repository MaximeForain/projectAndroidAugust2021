package com.example.myapplication.services.mappers

import com.example.myapplication.model.Favorite
import com.example.myapplication.repositories.web.dto.FavoriteDto


// .---------------------------------------------------------------------.
// |                          FavoriteMapper                             |
// '---------------------------------------------------------------------'
object FavoriteMapper {

    fun mapToFavorite(favoritesDto: List<FavoriteDto>): List<Favorite> {
        val favorites: ArrayList<Favorite> = ArrayList()

        for (favoriteDto in favoritesDto)  {
            favorites.add(Favorite(
                    favoriteDto.customer_id,
                    favoriteDto.favoritebar_id))
        }

        return favorites
    }
}