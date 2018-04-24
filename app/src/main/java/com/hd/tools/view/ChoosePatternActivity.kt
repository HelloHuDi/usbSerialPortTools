package com.hd.tools.view

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.hd.tools.R

/**
 * Created by hd on 2017/11/22.
 *
 */
class ChoosePatternActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setContentView(R.layout.activity_choose_pattern)
    }

    /** serial port device measure*/
    fun measureSerial(v: android.view.View) {
        startActivity(intent.setClass(this,SerialPortMeasureActivity::class.java))
    }

    /** usb serial port device measure*/
    fun measureUsb(v: android.view.View) {
        startActivity(intent.setClass(this,UsbMeasureActivity::class.java))
    }

}
