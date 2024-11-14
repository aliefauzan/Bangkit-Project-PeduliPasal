package com.example.pedulipasal.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.pedulipasal.R

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