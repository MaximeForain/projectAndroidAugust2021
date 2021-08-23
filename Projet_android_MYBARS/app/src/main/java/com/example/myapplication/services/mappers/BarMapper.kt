package com.example.myapplication.services.mappers

import com.example.myapplication.model.Bar
import com.example.myapplication.repositories.web.dto.BarDto


// .---------------------------------------------------------------------.
// |                              BarMapper                              |
// '---------------------------------------------------------------------'
object BarMapper {

    fun mapToBar(barsdto: List<BarDto>): List<Bar> {
        val bars: ArrayList<Bar> = ArrayList()

        for (bardto in barsdto)  {
            bars.add(Bar(
                    bardto.bar_id,
                    bardto.barname,
                    bardto.description,
                    bardto.phonenumber,
                    bardto.hashtags,
                    bardto.webaddress,
                    bardto.address,
                    bardto.admin_id,
                    0,
                    0,
                    0f,
                    isFavorite = false,
                    isAlreadyEvaluatedByUseur = false))
        }

        return bars
    }
}