package com.leon.common.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

fun ImageView.setImageFromUrl(url: String, @DrawableRes placeHolder: Int = 0) {
    Glide.with(this).load(url).placeholder(placeHolder).into(this)
}

fun ImageView.setRoundedImageFromUrl(url: String, @Px corners: Int, @DrawableRes placeHolder: Int = 0) {
    Glide.with(this).load(url).placeholder(placeHolder).transform(RoundedCorners(corners)).into(this)
}

fun ImageView.setRoundedImage(@RawRes @DrawableRes imgId: Int, @Px corners: Int, @DrawableRes placeHolder: Int = 0) {
    Glide.with(this).load(imgId).placeholder(placeHolder).transform(RoundedCorners(corners)).into(this)
}
