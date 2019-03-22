package com.jojajones.smackchat.Controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.AuthService
import com.jojajones.smackchat.Services.MessageService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginUserSpinner.visibility = View.INVISIBLE
    }

    override fun onResume() {
        if(App.sharedPreferences.authToken != ""){
            finish()
        }
        super.onResume()
    }

    fun loginScreenLoginBtnClicked(view: View){
        hideKeyboard()
        if(loginScreenEmail.text.isNotEmpty() && loginScreenPassword.text.isNotEmpty()) {
            enableSpinner(true)
            AuthService.loginUser(
                loginScreenEmail.text.toString(),
                loginScreenPassword.text.toString()
            ) { complete ->
                if (complete) {
                    AuthService.getUserProfile(this) { userUpdated ->
                        if (userUpdated) {
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast("Unable to update user info")
                        }
                    }


                } else {
                    errorToast("Login unsuccessful")
                }
            }
        }else{
            errorToast("Enter both email and password")
        }
    }

    fun signUpBtnClicked(view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun errorToast(error: String){
        Toast.makeText(this, "$error, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            loginUserSpinner.visibility = View.VISIBLE

        }else{
            loginUserSpinner.visibility = View.INVISIBLE
        }

        loginScreenLoginBtn.isEnabled = !enable
        signUpBtn.isEnabled = !enable
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
