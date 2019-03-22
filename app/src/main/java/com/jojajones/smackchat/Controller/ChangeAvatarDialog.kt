package com.jojajones.smackchat.Controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.AuthService
import com.jojajones.smackchat.Services.User
import com.jojajones.smackchat.Utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.change_avatar_dialog.*
import kotlin.random.Random

class ChangeAvatarDialog: AppCompatActivity(){
    var userAvatar = "profileDefault"
    var bgColor = "[0.5, 0.5, 0.5, 1]"
    var r = 0
    var g = 0
    var b = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_avatar_dialog)
    }

    fun customizeAvatarClicked(view: View){
        val avatar = Random.nextInt(0,28)
        val color = Random.nextInt(2)

        if(color == 0){
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        customizeAvatarDialogImage.setImageResource(resources.getIdentifier(userAvatar, "drawable", packageName))

    }

    fun customizeAvatarBgClicked(view: View){
        r = Random.nextInt(256)
        g = Random.nextInt(256)
        b = Random.nextInt(256)
        customizeAvatarDialogImage.setBackgroundColor(Color.rgb(r,g,b))
        bgColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255},1]"

    }

    fun customizeCancelClicked(view: View){
        finish()
    }

    fun customizeAcceptClicked(view: View){
        User.avatarColor = bgColor
        User.avatarIcon = userAvatar
        AuthService.updateUser{
            val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)

            LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

        }
        finish()
    }

    fun nextAnimalClicked(view: View){
        var animalInt: Int?
        var animalColor: String?
        if(userAvatar == "profileDefault"){
            animalInt = 0
            animalColor = "light"
        }else{
            if(userAvatar[0] == 'l'){
                animalColor = "light"
                animalInt = (userAvatar.substring(animalColor.length,userAvatar.length).toInt()+1)%28
            }else{
                animalColor = "dark"
                animalInt = (userAvatar.substring(animalColor.length,userAvatar.length).toInt()+1)%28
            }
        }
        userAvatar = "$animalColor$animalInt"

        customizeAvatarDialogImage.setImageResource(resources.getIdentifier(userAvatar, "drawable", packageName))
    }

    fun previousAnimalClicked(view: View){
        var animalInt: Int?
        var animalColor: String?
        if(userAvatar == "profileDefault"){
            animalInt = 0
            animalColor = "light"
        }else{
            if(userAvatar[0] == 'l'){
                animalColor = "light"
                animalInt = (userAvatar.substring(animalColor.length,userAvatar.length).toInt()+27)%28
            }else{
                animalColor = "dark"
                animalInt = (userAvatar.substring(animalColor.length,userAvatar.length).toInt()+27)%28
            }
        }
        userAvatar = "$animalColor$animalInt"

        customizeAvatarDialogImage.setImageResource(resources.getIdentifier(userAvatar, "drawable", packageName))
    }

    fun lightBtnClicked(view: View){
        var animalInt: Int?
        var animalColor: String?
        if(userAvatar[0] != 'l') {
            if (userAvatar == "profileDefault") {
                animalInt = 0
                animalColor = "light"
            } else {
                animalColor = "light"
                animalInt = userAvatar.substring(animalColor.length-1, userAvatar.length).toInt()
            }
            userAvatar = "$animalColor$animalInt"
            customizeAvatarDialogImage.setImageResource(resources.getIdentifier(userAvatar, "drawable", packageName))
        }


    }

    fun darkBtnClicked(view: View){
        var animalInt: Int?
        var animalColor: String?
        if(userAvatar[0] != 'd') {
            if (userAvatar == "profileDefault") {
                animalInt = 0
                animalColor = "dark"
            } else {
                animalColor = "dark"
                animalInt = userAvatar.substring(animalColor.length+1, userAvatar.length).toInt()
            }
            userAvatar = "$animalColor$animalInt"
            customizeAvatarDialogImage.setImageResource(resources.getIdentifier(userAvatar, "drawable", packageName))
        }


    }

    fun redShuffleClicked(view: View){
        r = Random.nextInt(256)
        customizeAvatarDialogImage.setBackgroundColor(Color.rgb(r,g,b))
        bgColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255},1]"
    }

    fun greenShuffleClicked(view: View){
        g = Random.nextInt(256)
        customizeAvatarDialogImage.setBackgroundColor(Color.rgb(r,g,b))
        bgColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255},1]"
    }

    fun blueShuffleClicked(view: View){
        b = Random.nextInt(256)
        customizeAvatarDialogImage.setBackgroundColor(Color.rgb(r,g,b))
        bgColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255},1]"
    }
}
