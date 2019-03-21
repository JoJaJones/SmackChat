package com.jojajones.smackchat.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.jojajones.smackchat.Model.Channel
import com.jojajones.smackchat.Utils.*
import org.json.JSONException
import org.json.JSONObject

object MessageService {
    val channels = ArrayList<Channel>()

    fun addChannel(name: String, desc: String, id: String){
        channels.add(Channel(name, desc, id))
    }

    fun getChannels(context: Context, complete: (Boolean) -> Unit){
        val getChannelData = object: JsonArrayRequest(Method.GET, URL_GETCHANNELS, null, Response.Listener { channelList ->
            try {
                for(x in 0 until channelList.length()){
                    val channel = channelList.getJSONObject(x)
                    addChannel(channel.getString(NAME), channel.getString(DESC), channel.getString(ID))
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
                header.put(AUTH, "Bearer ${User.token}")
                return header
            }
        }
        Volley.newRequestQueue(context).add(getChannelData)
    }
}