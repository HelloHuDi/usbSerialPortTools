package com.hd.tools.view


/**
 * Created by hd on 2017/11/22 .
 *
 */
abstract class BaseActivity: android.support.v7.app.AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}