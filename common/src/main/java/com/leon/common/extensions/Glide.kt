//package com.leon.common.extensions
//
//import androidx.annotation.NonNull
//import com.bumptech.glide.annotation.GlideExtension
//import com.bumptech.glide.annotation.GlideModule
//import com.bumptech.glide.annotation.GlideOption
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.bumptech.glide.request.BaseRequestOptions
//
//
//@GlideModule
//
//@GlideExtension
//class RoundCorner private constructor() {
//    companion object {
//
//        @NonNull
//        @GlideOption
//        @JvmStatic
//        fun corners(options: BaseRequestOptions<*>, cornerRadius: Int):BaseRequestOptions<*> {
//            return options.transform(RoundedCorners(cornerRadius))
//        }
//    }
//}