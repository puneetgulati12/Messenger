package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.messenger.NewMessegeActivity.Companion.USER_KEY
import com.example.messenger.Views.LatestMessageRow
import com.example.messenger.messages.ChatLogActivity
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messege.*

class LatestMessegeActivity : AppCompatActivity() {
    companion object {
        var currentUser : User? = null
    }
val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messege)

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this , DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.e("TAG" , "qwerty")
            val intent = Intent(this , ChatLogActivity::class.java)

            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY , row.chatPartnerUser)

            startActivity(intent)
        }

        listenForLatestMessage()

        fetchCurrentUser()

        verifyUserIsLoggedIn()

        }
val latestMessageMap = HashMap<String , ChatMessage>()

    private fun refershRecyclerView(){
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenForLatestMessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-message").child(fromid!!)
        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(ds: DatabaseError) {

            }

            override fun onChildMoved(ds: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(ds: DataSnapshot, p1: String?) {
                val chatMessage = ds.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[ds.key!!] = chatMessage
                refershRecyclerView()
            }

            override fun onChildAdded(ds: DataSnapshot, p1: String?) {
                val chatMessage = ds.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[ds.key!!] = chatMessage
                refershRecyclerView()
            }

            override fun onChildRemoved(ds: DataSnapshot) {
            }

        })
    }



    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onCancelled(ds: DatabaseError) {

            }

            override fun onDataChange(ds: DataSnapshot) {
                currentUser = ds.getValue(User::class.java)
                Log.e("LatestMessages ", "current user ${currentUser?.username}")
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this , MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }

}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_messege -> {
                val intent = Intent(this , NewMessegeActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this , MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu , menu)
        return super.onCreateOptionsMenu(menu)
    }
}

private fun Any.into(targetImageView: View?) {

}


