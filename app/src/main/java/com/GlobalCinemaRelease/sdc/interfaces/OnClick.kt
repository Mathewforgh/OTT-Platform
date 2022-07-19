package com.GlobalCinemaRelease.sdc.interfaces

import android.widget.ImageView
import com.GlobalCinemaRelease.sdc.response.Data
import com.GlobalCinemaRelease.sdc.adapters.LanguageSlideAdapter

interface OnClick {
    fun onItemClick(
        position: Int,
        holder: LanguageSlideAdapter.ViewHolder,
        data: List<Data>,
        checkLanguage: ImageView
    )
}