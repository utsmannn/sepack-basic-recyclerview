package com.utsman.sepack.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.ObjectKey
import com.utsman.sepack.GlideApp

fun ImageView.loadUrl(
    url: String?,
    id: String,
    requestListener: RequestListener<Drawable>? = null,
    placeholder: Drawable? = null
) = run {
    val sign = ObjectKey(id)
    GlideApp.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(200))
        .addListener(requestListener)
        .placeholder(placeholder)
        .signature(sign)
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadRes(
    @DrawableRes res: Int,
    id: String,
    requestListener: RequestListener<Drawable>? = null,
    placeholder: Drawable? = null
) = run {
    val sign = ObjectKey(id)
    GlideApp.with(context)
        .load(res)
        .transition(DrawableTransitionOptions.withCrossFade(200))
        .addListener(requestListener)
        .placeholder(placeholder)
        .signature(sign)
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadUrlSvg(
    url: String?,
    id: String,
    placeholder: Drawable? = null
) = run {
    val uri = Uri.parse(url)
    val sign = ObjectKey(id)
    GlideApp.with(context)
        .load(uri)
        .transition(DrawableTransitionOptions.withCrossFade(200))
        .placeholder(placeholder)
        .signature(sign)
        .into(this)
        .clearOnDetach()
}