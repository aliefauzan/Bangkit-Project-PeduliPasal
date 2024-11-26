package com.example.pedulipasal.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.pedulipasal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getProfileIcon(context: Context, isLocalUser: Boolean): Drawable {
    val drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_person_24)
            ?: throw IllegalStateException("Could not get user profile image")

    if (isLocalUser) {
        DrawableCompat.setTint(drawable.mutate(), Color.BLUE)
    } else {
        DrawableCompat.setTint(drawable.mutate(), Color.RED)
    }

    return drawable
}

fun getTimeFormat(date: Date?): String {
    val dateFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
    return dateFormat.format(date ?: dateFormat.parse("00.00"))
}

fun showLocalTime(date: Date?): Date {
    return SimpleDateFormat("HH.mm", Locale.getDefault()).parse(getTimeFormat(date)) ?: Date()
}


fun getDateFormat(date: Date?): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date ?: dateFormat.parse("01/01/1970"))
}
