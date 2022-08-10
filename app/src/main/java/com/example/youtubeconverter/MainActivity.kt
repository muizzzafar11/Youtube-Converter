 package com.example.youtubeconverter

import android.Manifest
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

 class MainActivity : AppCompatActivity(), LifecycleObserver {

     private companion object{
         //PERMISSION request constant, assign any value
         private const val STORAGE_PERMISSION_CODE = 100
         private const val TAG = "PERMISSION_TAG"
     }

     private lateinit var createNotificationButton: Button

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         createNotificationButton = findViewById(R.id.btn)
         NotificationHelper.createChannel(this)


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