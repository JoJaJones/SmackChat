package com.jojajones.smackchat.Controller

import android.annotation.SuppressLint
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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.jojajones.smackchat.Model.Channel
import com.jojajones.smackchat.Model.Message
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.AuthService
import com.jojajones.smackchat.Services.MessageService
import com.jojajones.smackchat.Services.User
import com.jojajones.smackchat.Utils.BROADCAST_USER_DATA_CHANGE
import com.jojajones.smackchat.Utils.SOCKET_URL
import com.jojajones.smackchat.adapters.MessageAdapter
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val socket: Socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null
    lateinit var messageAdapter: MessageAdapter

    private fun setUpAdapters(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channelList.adapter = channelAdapter



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        channelList.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel(this)
        }

        setUpAdapters()
        if(App.sharedPreferences.isLoggedIn){
            AuthService.getUserProfile(this){ }
        }else{
            Toast.makeText(this, "Prefs not saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.sharedPreferences.isLoggedIn){
                userNameNavHeader.text = User.name
                userEmailNavHeader.text = App.sharedPreferences.userEmail

                val resourceID = resources.getIdentifier(User.avatarIcon, "drawable", packageName)
                userProfileImageNavHeader.setImageResource(resourceID)
                userProfileImageNavHeader.setBackgroundColor(User.getBgColor(User.avatarColor))
                loginBtnNavHeader.text = "LOGOUT"

                MessageService.getChannels(context){acquiredChannels ->
                    if(acquiredChannels){
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                        }
                        channelAdapter.notifyDataSetChanged()
                        updateWithChannel(context)
                    }else{
                        Log.d("ERROR","Unable to retrieve channels")
                    }
                }





                }
        }
    }

    fun updateWithChannel(context: Context){
        if(selectedChannel != null){
            mainText.text = selectedChannel.toString()
            MessageService.messages = ArrayList()
            MessageService.getMessages(context, selectedChannel?.id!!) {
                if (it) {
                    messageAdapter.notifyDataSetChanged()
                }
            }
            messageAdapter = MessageAdapter(this, MessageService.messages){

            }

            messageView.adapter = messageAdapter
            messageView.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    fun loginBtnNavClicked(view: View){
        if(!App.sharedPreferences.isLoggedIn) {
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
        if(App.sharedPreferences.isLoggedIn) {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){ _, _ ->
                    //actions when clicked
                    val channelNameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val channelDescTextField = dialogView.findViewById<EditText>(R.id.addChannelDescText)


                    socket.emit("newChannel",channelNameTextField.text.toString(), channelDescTextField.text.toString())



                }
                .setNegativeButton("Cancel"){ _, _ ->
                    //cancel and close


                }
                .show()
        }else{
            Toast.makeText(this, "Please log in before trying to add channels", Toast.LENGTH_LONG).show()
        }
    }

    fun profileImageClicked(view: View){
        if(App.sharedPreferences.isLoggedIn){
            val customizeIntent = Intent(this, ChangeAvatarDialog::class.java)
            startActivity(customizeIntent)




        }

    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            MessageService.addChannel(args[0] as String, args[1] as String, args[2] as String)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            MessageService.addMessage(args[0] as String, args[1] as String, args[2] as String, args[3] as String,
                args[4] as String, args[5] as String, args[6] as String, args[7] as String )
            messageAdapter.notifyDataSetChanged()
        }
    }

    fun sendMessageBtnClicked(view: View){
        hideKeyboard()
        if(App.sharedPreferences.isLoggedIn && chatBar.text.isNotEmpty() && selectedChannel != null){
            socket.emit("newMessage", chatBar.text.toString(), User.id, selectedChannel!!.id,
                User.name, User.avatarIcon, User.avatarColor)
            chatBar.text.clear()
            chatBar.hint = "message"
        }
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

    }

}
