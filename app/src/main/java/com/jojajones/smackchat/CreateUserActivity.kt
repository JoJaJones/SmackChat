package com.jojajones.smackchat

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var bgColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun createUserAvatarClicked(view: View){

        val avatar = Random.nextInt(0,28)
        val color = Random.nextInt(2)

        if(color == 0){
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        createUserAvatar.setImageResource(resources.getIdentifier(userAvatar, "drawable", this.packageName))
    }

    fun createUserAvatarBgGenBtnClicked(view: View){
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        createUserAvatar.setBackgroundColor(Color.rgb(r,g,b))
        bgColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255},1]"
    }

    fun createUserBtnClicked(view: View){

    }
}
