package com.jojajones.smackchat.Services

import android.graphics.Color
import com.jojajones.smackchat.Controller.App
import java.util.*

object User{
    var name = ""
    var avatarIcon =""
    var avatarColor = ""
    var id = ""

    fun getBgColor(avatarColor: String): Int{
        val processedColors = avatarColor
            .replace("[","")
            .replace("]", "")
            .replace(",","")

        if(processedColors != "") {
            val scanner = Scanner(processedColors)
            var colors = Array(3) { i ->
                (scanner.nextDouble() * 255).toInt()
            }

            return Color.rgb(colors[0], colors[1], colors[2])
        }else{
            return Color.rgb((.5*255).toInt(),(.5*255).toInt(),(.5*255).toInt())
        }
    }

    fun logout(){
        name = ""
        avatarIcon = ""
        avatarColor = ""
        id = ""
        App.sharedPreferences.isLoggedIn = false
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.authToken = ""


    }
}