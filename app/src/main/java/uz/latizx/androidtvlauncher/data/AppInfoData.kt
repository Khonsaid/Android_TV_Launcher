package uz.latizx.androidtvlauncher.data

import android.graphics.drawable.Drawable

data class AppInfoData(
    val label:String,
    val packageName:String,
    val activityName: String,
    val icon: Drawable
)