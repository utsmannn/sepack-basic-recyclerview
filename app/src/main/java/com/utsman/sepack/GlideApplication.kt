package com.utsman.sepack

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions


@GlideModule
class GlideApplication : AppGlideModule() {

    @SuppressLint("NewApi")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSizeBytes: Long = 1024 * 1024 * 500
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (activityManager?.isLowRamDevice == true) {
            builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}