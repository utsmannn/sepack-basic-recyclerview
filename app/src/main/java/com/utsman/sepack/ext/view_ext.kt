package com.utsman.sepack.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View = run {
    LayoutInflater.from(context).inflate(layoutRes, this, false)
}