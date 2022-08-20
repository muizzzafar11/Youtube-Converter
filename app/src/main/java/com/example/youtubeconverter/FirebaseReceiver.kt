package com.example.youtubeconverter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        firestoreEventListener(context)
    }

    private fun firestoreEventListener(context: Context) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        Toast.makeText(context, "here - 1", Toast.LENGTH_SHORT).show()
        val docRef = userId?.let { Firebase.firestore.collection("MusicPlayer").document(it) }
        docRef?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Toast.makeText(context, "here - 2", Toast.LENGTH_SHORT).show()

                var url = snapshot.data?.get("currentLink") as String
                url = url.trim()
                if(url != "") {
                    Toast.makeText(context, "here - 3", Toast.LENGTH_SHORT).show()
                    val data = hashMapOf("currentLink" to "")
                    docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener{
                            ScriptHandler.initPy(context)
                            ScriptHandler.sendDataToScript(url, context)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, "Link from website: null", Toast.LENGTH_LONG).show()
            }
        }
    }

}