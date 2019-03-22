package com.jojajones.smackchat.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.jojajones.smackchat.Controller.App
import com.jojajones.smackchat.Model.Channel
import com.jojajones.smackchat.Model.Message
import com.jojajones.smackchat.Utils.*
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()
    var messages = ArrayList<Message>()

    fun addChannel(name: String, desc: String, id: String){
        channels.add(Channel(name, desc, id))
    }

    fun getChannels(context: Context, complete: (Boolean) -> Unit){
        val getChannelData = object: JsonArrayRequest(Method.GET, URL_GETCHANNELS, null, Response.Listener { channelList ->
            try {
                for(x in 0 until channelList.length()){
                    val channel = channelList.getJSONObject(x)
                    if(x >= MessageService.channels.count()) {
                        addChannel(channel.getString(NAME), channel.getString(DESC), channel.getString(ID))
                    }
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXC: "+e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not get channels")
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
        App.sharedPreferences.requestQueue.add(getChannelData)
    }

    fun addMessage(message: String, userId: String, channelId: String, userName: String, userAvatar: String, userAvatarBg: String, id: String, timestamp: String){
        messages.add(Message(message, userId, channelId, userName, userAvatar, userAvatarBg, id, timestamp))
    }

    fun getMessages(context: Context, channelId: String, complete: (Boolean) -> Unit){
        messages = ArrayList()
        val getMessageData = object: JsonArrayRequest(Method.GET, "$URL_GETMESSAGES$channelId", null, Response.Listener { messageList ->
            try {
                for(x in 0 until messageList.length()){
                    val message = messageList.getJSONObject(x)
                    //if(x >= MessageService.messages.count()) {
                        addMessage(
                            message = message.getString("messageBody"),
                            userId = message.getString("userId"),
                            channelId = message.getString("channelId"),
                            userName = message.getString("userName"),
                            userAvatar = message.getString("userAvatar"),
                            userAvatarBg = message.getString("userAvatarColor"),
                            id = message.getString(ID),
                            timestamp = message.getString("timeStamp")
                        )
                    //}
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXC: "+e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not get channels")
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
        App.sharedPreferences.requestQueue.add(getMessageData)
    }
}