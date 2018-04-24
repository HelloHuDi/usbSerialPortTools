package com.hd.tools.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.hd.splashscreen.text.SimpleConfig
import com.hd.splashscreen.text.SimpleSplashFinishCallback
import com.hd.tools.R
import kotlinx.android.synthetic.main.activity_splash.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * Created by hd on 2018/4/24 .
 * request permission
 */
class SplashActivity : BaseActivity(), SimpleSplashFinishCallback,EasyPermissions.PermissionCallbacks {

     private val RESULT_CODE = 100

     private val par = Manifest.permission.WRITE_EXTERNAL_STORAGE

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_splash)
         val simpleConfig = SimpleConfig(this)
         simpleConfig.callback = this
         screen.addConfig(simpleConfig)
         screen.start()
     }

    override fun loadFinish() {
         if (!isDestroyed) {
             requestPermission()
         }
     }

     private fun requestPermission() {
         if (EasyPermissions.hasPermissions(this, par)) {
             goMain()
         } else {
             EasyPermissions.requestPermissions(this, resources.getString(R.string.provide_capture_function), RESULT_CODE, par)
         }
     }

     private fun goMain() {
         startActivity(Intent(this, ChoosePatternActivity::class.java))
         finish()
     }

     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
     }

     override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
         if (requestCode == RESULT_CODE) {
             goMain()
         }
     }

     override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
         if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
             AppSettingsDialog.Builder(this).build().show()
         }
     }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
         super.onActivityResult(requestCode, resultCode, data)
         if (EasyPermissions.hasPermissions(this, par)) {
             goMain()
         }
     }
}
