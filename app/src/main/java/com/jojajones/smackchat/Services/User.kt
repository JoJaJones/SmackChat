package com.jojajones.smackchat.Services

import android.graphics.Color
import java.util.*

object User{
    var name = ""
    var email = ""
    var avatarIcon =""
    var avatarColor = ""
    var token = ""
    var id = ""
    var isLoggedIn = false

    fun getBgColor(): Int{
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
        email = ""
        avatarIcon = ""
        avatarColor = ""
        token = ""
        id = ""
        isLoggedIn = false


    }
}