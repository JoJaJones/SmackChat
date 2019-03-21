package com.jojajones.smackchat.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.jojajones.smackchat.Model.Channel
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.MessageService
import com.jojajones.smackchat.Services.User
import com.jojajones.smackchat.Utils.BROADCAST_USER_DATA_CHANGE
import com.jojajones.smackchat.Utils.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>

    private fun setUpAdapters(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channelList.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setUpAdapters()

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if(User.isLoggedIn){
                userNameNavHeader.text = User.name
                userEmailNavHeader.text = User.email

                val resourceID = resources.getIdentifier(User.avatarIcon, "drawable", packageName)
                userProfileImageNavHeader.setImageResource(resourceID)
                userProfileImageNavHeader.setBackgroundColor(User.getBgColor())
                loginBtnNavHeader.text = "LOGOUT"

//                MessageService.getChannels(context){acquiredChannels ->
//                    if(acquiredChannels){
//                        channelAdapter.notifyDataSetChanged()
//                    }else{
//                        Log.d("ERROR","Unable to retrieve channels")
//                    }
//                }


            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View){
        if(!User.isLoggedIn) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }else{
            User.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userProfileImageNavHeader.setImageResource(R.drawable.profiledefault)
            userProfileImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "LOGIN"

        }

    }

    fun addChannelClicked(view: View){
        if(User.isLoggedIn) {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){ dialog, which ->
                    //actions when clicked
                    val channelNameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val channelDescTextField = dialogView.findViewById<EditText>(R.id.addChannelDescText)


                    socket.emit("newChannel",channelNameTextField.text.toString(), channelDescTextField.text.toString())



                }
                .setNegativeButton("Cancel"){dialog, which ->
                    //cancel and close


                }
                .show()
        }else{
            Toast.makeText(this, "Please log in before trying to add channels", Toast.LENGTH_LONG).show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            MessageService.addChannel(args[0] as String, args[1] as String, args[2] as String)
            channelAdapter.notifyDataSetChanged()
        }
    }

    fun sendMessageBtnClicked(view: View){
        TODO()
        hideKeyboard()
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }




}
