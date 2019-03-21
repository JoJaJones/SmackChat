package com.jojajones.smackchat.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jojajones.smackchat.Utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {


    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){

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

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email.toLowerCase())
        jsonBody.put(PASSWORD, password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGINUSER, null, Response.Listener {response ->
            try {
                User.token = response.getString(TOKEN)
                User.email = response.getString(USER)
                User.isLoggedIn = true
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

        Volley.newRequestQueue(context).add(loginRequest)

    }

    fun getUserProfile(context: Context, complete: (Boolean) -> Unit){

        val updateUserData = object: JsonObjectRequest(Method.GET, "$URL_FINDBYEMAIL${User.email}", null, Response.Listener {response ->
            try{
                User.name = response.getString(NAME)
                User.avatarIcon = response.getString(AVATAR_ICON)
                User.avatarColor = response.getString(AVATAR_BG)
                User.id = response.getString(ID)
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
                header.put(AUTH, "Bearer ${User.token}")
                return header
            }
        }

        Volley.newRequestQueue(context).add(updateUserData)

    }

    fun createUser(context: Context, name: String, email: String, avatarBg: String, avatarIcon: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put(NAME, name)
        jsonBody.put(EMAIL, email.toLowerCase())
        jsonBody.put(AVATAR_ICON, avatarIcon)
        jsonBody.put(AVATAR_BG, avatarBg)
        val requestBody = jsonBody.toString()


        val createUserRequest = object: JsonObjectRequest(Method.POST, URL_ADDUSER, null, Response.Listener {response ->
            try {
                User.name = response.getString(NAME)
                User.email = response.getString(EMAIL)
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
                header.put(AUTH, "Bearer ${User.token}")
                return header
            }
        }

        Volley.newRequestQueue(context).add(createUserRequest)

    }
}