package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.messenger.messages.ChatLogActivity
import com.example.messenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_messege.*
import kotlinx.android.synthetic.main.user_row_newmessege.view.*

class NewMessegeActivity : AppCompatActivity() {
    //val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messege)

        supportActionBar?.title = "Select User"


//        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())

//        recyclerview_newmessege.adapter = adapter
        fetchUser()
    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUser() {

       val ref =  FirebaseDatabase.getInstance().reference.child("users")
        ref.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(ds: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                ds.children.forEach{

                    val user = it.getValue(User::class.java)

                    Log.e("TAG", user?.uid)

                    if (user!= null) {
                        adapter.add(UserItem(user))
                        adapter.notifyDataSetChanged()
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context , ChatLogActivity::class.java)
                    //intent.putExtra(USER_KEY , userItem.user.username)
                    intent.putExtra(USER_KEY , userItem.user)
                    startActivity(intent)
                }
                recyclerview_newmessege.adapter = adapter
            }


            override fun onCancelled(ds: DatabaseError) {
            }
        })

    }
}
class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_newmeesege.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newmessege)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessege
    }



}