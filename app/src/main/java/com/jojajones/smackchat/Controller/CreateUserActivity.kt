package com.jojajones.smackchat.Controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.AuthService
import com.jojajones.smackchat.Utils.BROADCAST_USER_DATA_CHANGE
import com.jojajones.smackchat.Utils.SWW
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var bgColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserSpinner.visibility = View.INVISIBLE
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

        enableSpinner(true)

        val email = createUserEmail.text.toString()
        val password = createUserPassword.text.toString()
        val name = createUserUserName.text.toString()

        if(!(email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty())){
            errorToast("All text fields must be completed")
        }else {

            if(email.contains('@') && email.contains('.')) {
                AuthService.registerUser(email, password) { registerSuccess ->
                    if (registerSuccess) {
                        AuthService.loginUser(email, password) { loginSuccess ->
                            if (loginSuccess) {
                                AuthService.createUser(name, email, bgColor, userAvatar) { created ->
                                    if (created) {
                                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)

                                        LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                        enableSpinner(false)
                                        finish()
                                    } else {
                                        errorToast(SWW)
                                    }
                                }
                            } else {
                                errorToast(SWW)
                            }
                        }
                    } else {
                        errorToast(SWW)
                    }

                }
            } else {
                errorToast("Invalid email")
            }
        }
    }

    fun errorToast(error: String){
        Toast.makeText(this, "$error, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            createUserSpinner.visibility = View.VISIBLE

        }else{
            createUserSpinner.visibility = View.INVISIBLE
        }

        createUserAvatar.isClickable = !enable
        createUserAvatarBgGenBtn.isEnabled = !enable
        createUserBtn.isEnabled = !enable
    }
}
