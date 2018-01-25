package com.hd.tools.view

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.hd.tools.R
import com.hd.tools.help.ShowVideo
import kotlinx.android.synthetic.main.activity_choose_pattern.*
import org.jetbrains.anko.startActivity

/**
 * Created by hd on 2017/11/22.
 *
 */
class ChoosePatternActivity : BaseActivity() {

    private val showVideo by lazy { ShowVideo(this, bgVideoView, R.raw.health_live, "health_live.mp4") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setContentView(R.layout.activity_choose_pattern)
//        showVideo.showVideo()
    }

    override fun onStop() {
        super.onStop()
//        showVideo.stopVideo()
    }

    /** serial port device measure*/
    fun measureSerial(v: android.view.View) {
        startActivity<SerialPortMeasureActivity>()
    }

    /** usb serial port device measure*/
    fun measureUsb(v: android.view.View) {
        startActivity<UsbMeasureActivity>()
    }

}
