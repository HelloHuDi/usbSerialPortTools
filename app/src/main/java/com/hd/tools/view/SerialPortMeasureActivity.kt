package com.hd.tools.view

import android.view.View
import com.hd.tools.R
import kotlinx.android.synthetic.main.device_measure_title.*

class SerialPortMeasureActivity : MeasureActivity<String>() {

    override fun setLayoutId(): Int {
        return R.layout.activity_serial_port
    }

    override fun initContent() {
        setSpinnerAdapter(sp_baudrate, resources.getStringArray(R.array.baudrates))
        usb_title.visibility=View.GONE
    }

    override fun initDeviceList() {

    }

    override fun setListTitleName(t: String): CharSequence? {
        return t
    }

    override fun startConnect() {

    }
}
