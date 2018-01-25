package com.hd.tools.view

import android.util.Log
import com.hd.serialport.config.UsbPortDeviceType
import com.hd.serialport.listener.UsbMeasureListener
import com.hd.serialport.method.DeviceMeasureController
import com.hd.serialport.param.UsbMeasureParameter
import com.hd.serialport.usb_driver.*
import com.hd.tools.R
import kotlinx.android.synthetic.main.device_measure_title.*



/**
 * Created by hd on 2017/11/22.
 *
 */
class UsbMeasureActivity : MeasureActivity<UsbSerialPort>() {

    override fun setLayoutId(): Int {
        return R.layout.activity_usb_measure
    }

    override fun initContent() {
        setSpinnerAdapter(sp_baudrate, resources.getStringArray(R.array.baudrates))
        setSpinnerAdapter(sp_data, resources.getStringArray(R.array.DATA_BITS))
        setSpinnerAdapter(sp_stop, resources.getStringArray(R.array.STOP_BITS))
        setSpinnerAdapter(sp_parity, resources.getStringArray(R.array.PARITY))
    }

    override fun initDeviceList() {
        val usbSerialDrivers = DeviceMeasureController.scanUsbPort()
        val usbSerialPort = ArrayList<UsbSerialPort>()
        for (usbSerialDriver in usbSerialDrivers)
            usbSerialPort.addAll(usbSerialDriver.ports)
        mEntries.clear()
        mEntries.addAll(usbSerialPort)
        mAdapter?.notifyDataSetChanged()
        Log.d(TAG, "Done refreshing, " + mEntries.size + " entries found.")
    }

    override fun setListTitleName(t: UsbSerialPort): CharSequence? {
        val driver = t.driver
        val subtitle = when (driver) {
            is ProlificSerialDriver -> "PL23xx "
            is CdcAcmSerialDriver -> "CDC_ACM "
            is Ch34xSerialDriver -> "CH34xx "
            is Cp21xxSerialDriver -> "CP21xx "
            is FtdiSerialDriver -> "FTDxx "
            else -> "unknown "
        }
        return "$subtitle\nvendorId:${driver.device.vendorId},\nproductId:${driver.device.productId},\nname:${driver.device.deviceName}"
    }

    override fun startConnect() {
        DeviceMeasureController.measure(usbSerialPort = port, usbMeasureParameter = UsbMeasureParameter(usbPortDeviceType = UsbPortDeviceType.USB_OTHERS,//
                baudRate = Integer.parseInt(sp_baudrate.selectedItem.toString()), dataBits = Integer.parseInt(sp_data.selectedItem.toString()), //
                stopBits = Integer.parseInt(sp_stop.selectedItem.toString()), parity = Integer.parseInt(sp_parity.selectedItem.toString())),//
                usbMeasureListener = object : UsbMeasureListener {
                    override fun measureError(message: String) {
                        receiveDataWithLineFeed(message)
                    }

                    override fun measuring(usbSerialPort: UsbSerialPort, data: ByteArray) {
                        receiveData(usbSerialPort,data)
                    }

                    override fun write(usbSerialPort: UsbSerialPort) {
                        usbPort=usbSerialPort
                    }
                })
    }

    private var usbPort: UsbSerialPort?=null

    override fun writeData(arrayList: java.util.ArrayList<ByteArray>) {
        arrayList.forEach { usbPort?.write(it,1000) }
    }

}


