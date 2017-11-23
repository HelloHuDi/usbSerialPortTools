package com.hd.tools.view

import android.view.View
import com.hd.serialport.listener.SerialPortMeasureListener
import com.hd.serialport.method.DeviceMeasureController
import com.hd.serialport.param.SerialPortMeasureParameter
import com.hd.serialport.utils.L
import com.hd.tools.R
import kotlinx.android.synthetic.main.device_measure_title.*
import java.io.OutputStream

/**
 * Created by hd on 2017/11/22.
 *
 */
class SerialPortMeasureActivity : MeasureActivity<String>() {

    override fun setLayoutId(): Int {
        return R.layout.activity_serial_port
    }

    override fun initContent() {
        setSpinnerAdapter(sp_baudrate, resources.getStringArray(R.array.baudrates))
        setSpinnerAdapter(sp_parity, resources.getStringArray(R.array.PARITY))
        tvFlag.text = resources.getString(R.string.flags)
        usb_title.visibility = View.GONE
    }

    override fun initDeviceList() {
        val serialMap = DeviceMeasureController.scanSerialPort()
        val paths = ArrayList<String>()
        for (path in serialMap) {
            paths.add(path.value)
            L.d(path.key + "==" + path.value)
        }
        mEntries.clear()
        mEntries.addAll(paths)
        mAdapter?.notifyDataSetChanged()
    }

    override fun setListTitleName(t: String): CharSequence? {
        return t
    }

    override fun startConnect() {
        DeviceMeasureController.measure(serialPortMeasureParameter = SerialPortMeasureParameter(port,
                baudRate = Integer.parseInt(sp_baudrate.selectedItem.toString()),
                flags = Integer.parseInt(sp_parity.selectedItem.toString())),
                serialPortMeasureListener = object : SerialPortMeasureListener {
                    override fun measureError(message: String) {
                        receiveData(message)
                    }

                    override fun measuring(path: String, data: ByteArray) {
                        receiveData(path, data)
                    }

                    override fun write(outputStream: OutputStream) {
                    }
                })

    }
}
