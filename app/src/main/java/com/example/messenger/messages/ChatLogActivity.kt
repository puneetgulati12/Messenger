package com.example.messenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.messenger.LatestMessegeActivity
import com.example.messenger.NewMessegeActivity
import com.example.messenger.R
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "Chatlog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra(NewMessegeActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        listenForMessage()
        sendbtn_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send msg.....")
            performSendMessage()
        }

    }

    var fromToMsgId : String = ""

    private fun listenForMessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        fromToMsgId = "$fromid$toId"


        val chars = fromToMsgId.toCharArray()
        chars.sort()
        fromToMsgId = String(chars)

        val ref = FirebaseDatabase.getInstance().reference.child("user-message").child(fromToMsgId)

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(ds: DatabaseError) {

            }

            override fun onChildMoved(ds: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(ds: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(ds: DataSnapshot, p1: String?) {
                Log.e(TAG , "onChildAdded")
                val chatMessage = ds.getValue(ChatMessage::class.java)
                if (chatMessage!= null){

                    if (chatMessage.fromid == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessegeActivity.currentUser ?: return

                        adapter.add(ChatFromItem(chatMessage.text, currentUser))

                    }else {

                        adapter.add(ChatToItem(chatMessage.text , toUser!!))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)

            }


            override fun onChildRemoved(ds: DataSnapshot) {
            }

        })
    }


    private fun performSendMessage(){

        val text =editText_chat_log.text.toString()

        val fromid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessegeActivity.USER_KEY)

        val toId = user.uid

        if (fromid== null) return
//        val refernce = FirebaseDatabase.getInstance().getReference("messages/").push()

        val refernce = FirebaseDatabase.getInstance().getReference("user-message/$fromToMsgId").push()
//        val torefernce = FirebaseDatabase.getInstance().getReference("user-message/$fromToMsgId").push()


        val chatmessage = ChatMessage(refernce.key!! , text  , fromid, toId , System
            .currentTimeMillis()/1000)
        refernce.setValue(chatmessage).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message: ${refernce.key}")
            editText_chat_log.text.clear()
            recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
        }

    }

}

class ChatFromItem(val text: String, private val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
//load images from url
        val uri = user.profileImageUrl
        val targetImageview = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(uri).into(targetImageview)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem(val text: String,  val user: User) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
//load images from url
val uri = user.profileImageUrl
        val targetImageview = viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(uri).into(targetImageview)
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}