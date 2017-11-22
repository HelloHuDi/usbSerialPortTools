package com.hd.tools

import android.app.Application
import com.hd.serialport.method.DeviceMeasureController


/**
 * Created by hd on 2017/8/29 .
 *
 */
class AIOApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DeviceMeasureController.init(this,true)
    }
}
