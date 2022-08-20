 package com.example.youtubeconverter

import android.Manifest
import android.content.Context
import android.provider.Settings
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import com.example.youtubeconverter.ScriptHandler.initPy
import com.example.youtubeconverter.ScriptHandler.sendDataToScript
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

 class MainActivity : AppCompatActivity(), LifecycleObserver {

     private companion object{
         private const val STORAGE_PERMISSION_CODE = 100
     }
     private lateinit var createNotificationButton: Button
     private lateinit var signOutButton: Button
     lateinit var mGoogleSignInClient: GoogleSignInClient
     private var auth = FirebaseAuth.getInstance()


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         createNotificationButton = findViewById(R.id.btn)
         signOutButton = findViewById(R.id.btn_logout)
         NotificationHelper.createChannel(this)

         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             .requestIdToken(getString(R.string.client_id))
             .requestEmail()
             .build()
         mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

         signOutButton.setOnClickListener{
             mGoogleSignInClient.signOut().addOnCompleteListener {
                 val intent= Intent(this, LoginActivity::class.java)
                 startActivity(intent)
                 finish()
             }
         }

         createNotificationButton.setOnClickListener {
             if (checkPermission()){
                 NotificationHelper.showNotification(this)
             }
             else{
                 requestPermission()
             }
         }
     }

     private fun checkPermission(): Boolean{
         return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
             //Android is 11(R) or above
             Environment.isExternalStorageManager()
         }
         else{
             //Android is below 11(R)
             val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
             val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
             write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
         }
     }

     private fun requestPermission(){
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
             //Android is 11(R) or above
             try {
                 val intent = Intent()
                 intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                 val uri = Uri.fromParts("package", this.packageName, null)
                 intent.data = uri
                 storageActivityResultLauncher.launch(intent)
             }
             catch (e: Exception){
                 val intent = Intent()
                 intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                 storageActivityResultLauncher.launch(intent)
             }
         }
         else{
             //Android is below 11(R)
             ActivityCompat.requestPermissions(this,
                 arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                 STORAGE_PERMISSION_CODE
             )
         }
     }

     private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
         if(checkPermission())
             NotificationHelper.showNotification(this)
         else
             Toast.makeText(this, "External Storage Permission denied...", Toast.LENGTH_SHORT).show()
     }

     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<out String>,
         grantResults: IntArray
     ) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == STORAGE_PERMISSION_CODE){
             if (grantResults.isNotEmpty()){
                 //check each permission if granted or not
                 val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                 val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                 if (write && read){
                     //External Storage Permission granted
                     NotificationHelper.showNotification(this)
                 }
                 else{
                     Toast.makeText(this, "External Storage Permission denied...", Toast.LENGTH_SHORT).show()
                 }
             }
         }
     }


}