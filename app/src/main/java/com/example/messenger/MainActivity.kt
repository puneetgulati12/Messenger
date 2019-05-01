package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.messenger.models.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Register.setOnClickListener {
            performRegister()
        }
        Alreadyaccount.setOnClickListener {
            val intent = Intent(this, loginActvity::class.java)
            startActivity(intent)

        }

        selectphoto_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectPhotoUri : Uri? =  null

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null){
            selectPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver , selectPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f

        }

    }

    private fun performRegister() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this  , "please enter text in email/password" , Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseApp.initializeApp(this)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d("Main" , "Successfully create user with uid : ${it.result!!.user.uid}")
                uploadImagetoFirebaseStorage()
                Toast.makeText(this , "Successfully account is create :${it.result!!.user.displayName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LatestMessegeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.d("Main" , "Failed to create user :  ${it.message}")
                Toast.makeText(this  , "Failed to create user :  ${it.message}" , Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImagetoFirebaseStorage() {
        if (selectPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
       val ref =  FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectPhotoUri!!).addOnSuccessListener {
            Log.d("RegisterActivity" , "Successfully uploaded images : ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("RegisterActivity" , "File Location : $it")

                saveUserToFirebaseDatabase(it.toString())
            }
        }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid  , etUsername.text.toString() , profileImageUrl )
        ref.setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity" , "saved to Firebase Database")

            val intent = Intent(this , LatestMessegeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
            .addOnFailureListener {

            }

    }

}


