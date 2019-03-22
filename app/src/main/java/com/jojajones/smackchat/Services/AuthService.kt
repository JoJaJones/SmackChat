package com.jojajones.smackchat.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.jojajones.smackchat.Controller.App
import com.jojajones.smackchat.Utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {


    fun registerUser(email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email.toLowerCase())
        jsonBody.put(PASSWORD, password)
        val requestBody = jsonBody.toString()

        val registerRequest = object: StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener { _ ->
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not register user $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return BODY_CONTENT_TYPE
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email.toLowerCase())
        jsonBody.put(PASSWORD, password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGINUSER, null, Response.Listener {response ->
            try {
                App.sharedPreferences.authToken = response.getString(TOKEN)
                App.sharedPreferences.userEmail = response.getString(USER)
                App.sharedPreferences.isLoggedIn = true
                complete(true)
            }catch (e: JSONException){
                Log.d("ERROR", "Exc:"+e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user $error")
            complete(false)
        }){
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return BODY_CONTENT_TYPE
            }
        }

        App.sharedPreferences.requestQueue.add(loginRequest)

    }

    fun getUserProfile(context: Context, complete: (Boolean) -> Unit){

        val updateUserData = object: JsonObjectRequest(Method.GET, "$URL_FINDBYEMAIL${App.sharedPreferences.userEmail}", null, Response.Listener {response ->
            try{
                User.name = response.getString(NAME)
                User.avatarIcon = response.getString(AVATAR_ICON)
                User.avatarColor = response.getString(AVATAR_BG)
                User.id = response.getString(ID)
                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)

                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)


                complete(true)
            }catch (e: JSONException){
                Log.d("ERROR", "Exc:"+e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Could not find user ${error.localizedMessage}")
            complete(false)
        }){

            override fun getBodyContentType(): String {
                return BODY_CONTENT_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put(AUTH, "Bearer ${App.sharedPreferences.authToken}")
                return header
            }
        }

        App.sharedPreferences.requestQueue.add(updateUserData)

    }

    fun createUser(name: String, email: String, avatarBg: String, avatarIcon: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put(NAME, name)
        jsonBody.put(EMAIL, email.toLowerCase())
        jsonBody.put(AVATAR_ICON, avatarIcon)
        jsonBody.put(AVATAR_BG, avatarBg)
        val requestBody = jsonBody.toString()


        val createUserRequest = object: JsonObjectRequest(Method.POST, URL_ADDUSER, null, Response.Listener {response ->
            try {
                User.name = response.getString(NAME)
                App.sharedPreferences.userEmail = response.getString(EMAIL)
                User.avatarIcon = response.getString(AVATAR_ICON)
                User.avatarColor = response.getString(AVATAR_BG)
                User.id = response.getString(ID)
                complete(true)
            }catch (e: JSONException){
                Log.d("ERROR", "Exc:"+e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not create user ${error.localizedMessage}")
            complete(false)
        }){
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return BODY_CONTENT_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put(AUTH, "Bearer ${App.sharedPreferences.authToken}")
                return header
            }
        }

        App.sharedPreferences.requestQueue.add(createUserRequest)

    }

    fun updateUser(complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put(NAME, User.name)
        jsonBody.put(EMAIL, App.sharedPreferences.userEmail.toLowerCase())
        jsonBody.put(AVATAR_ICON, User.avatarIcon)
        jsonBody.put(AVATAR_BG, User.avatarColor)
        val requestBody = jsonBody.toString()


        val updateUserRequest = object: JsonObjectRequest(Method.PUT, "$URL_UPDATEUSER${User.id}", null, Response.Listener { response ->
            try {
                complete(true)
            }catch (e: JSONException){
                Log.d("ERROR", "Exc:"+e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not create user ${error.localizedMessage}")
            complete(false)
        }){
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return BODY_CONTENT_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put(AUTH, "Bearer ${App.sharedPreferences.authToken}")
                return header
            }
        }

        App.sharedPreferences.requestQueue.add(updateUserRequest)
    }
}