package com.example.messenger.Views

import android.view.View
import com.example.messenger.R
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latestmessage_row.view.*

@Suppress("PLUGIN_WARNING")

class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>(){

    var chatPartnerUser : User? = null
    override fun getLayout(): Int {
        return R.layout.latestmessage_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.latestmessage_textview.text = chatMessage.text

        val chatPartner : String
        if (chatMessage.fromid == FirebaseAuth.getInstance().uid){
            chatPartner = chatMessage.toId
        }else{
            chatPartner = chatMessage.fromid
        }

        val ref = FirebaseDatabase.getInstance().getReference("users").child(chatPartner)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(ds: DatabaseError) {

            }

            override fun onDataChange(ds: DataSnapshot) {
                chatPartnerUser = ds.getValue(User::class.java)
                viewHolder.itemView.username_latest_message.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageView
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)

            }

        })


    }

}

private fun Any.into(targetImageView: View?) {

}
