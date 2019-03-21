package com.jojajones.smackchat.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.AuthService
import com.jojajones.smackchat.Services.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onResume() {
        if(User.token != ""){
            finish()
        }
        super.onResume()
    }

    fun loginScreenLoginBtnClicked(view: View){
        AuthService.loginUser(this, loginScreenEmail.text.toString(), loginScreenPassword.text.toString()){complete ->
            if(complete){
                Log.d("LOGIN", "successful ${User.token}")
            }else{
                Log.d("LOGIN", "unsuccessful")
            }
        }
    }

    fun signUpBtnClicked(view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }
}
