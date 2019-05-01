package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class loginActvity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginActivity.setOnClickListener {
            val email = emailLogin.text.toString()
            val password = PasswordLogin.text.toString()

         FirebaseAuth.getInstance().signInWithEmailAndPassword(email , password)
             .addOnSuccessListener {
//             if (FirebaseAuth.getInstance().currentUser != null){
                 val intent = Intent(this , LatestMessegeActivity::class.java)
                 startActivity(intent)
//             }
             Log.e("TAG", "${it.user}")
            }.addOnCompleteListener {
//             if (it.isSuccessful){
//                 val intent = Intent(this , LatestMessegeActivity::class.java)
//                 startActivity(intent)
//             }
         }

             .addOnFailureListener {
                 it.printStackTrace()
            }

        }


        back_to_register.setOnClickListener {
            finish()
        }
    }


}