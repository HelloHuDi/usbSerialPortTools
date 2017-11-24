package com.hd.tools.view

import android.view.View
import com.hd.serialport.listener.SerialPortMeasureListener
import com.hd.serialport.method.DeviceMeasureController
import com.hd.serialport.param.SerialPortMeasureParameter
import com.hd.serialport.utils.L
import com.hd.tools.R
import kotlinx.android.synthetic.main.device_measure_title.*
import kotlinx.android.synthetic.main.serial_receive_send_test.*
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean

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
                        if (!portTest.get()) {
                            receiveData(path, data)
                        } else {
                            readTest(data)
                        }
                    }

                    override fun write(outputStream: OutputStream) {
                        if (portTest.get()) {
                            writeTest(outputStream)
                        }
                    }
                })
    }

    private fun readTest(data: ByteArray) {
        synchronized(mByteReceivedBackSemaphore) {
            var i = 0
            while (i < data.size) {
                if (data[i] == mValueToSend && !mByteReceivedBack) {
                    mValueToSend++
                    // This byte was expected
                    // Wake-up the sending thread
                    mByteReceivedBack = true
                } else {
                    // The byte was not expected
                    abnormalCount++
                }
                i++
            }
        }
    }

    private fun writeTest(outputStream: OutputStream) {
        synchronized(mByteReceivedBackSemaphore) {
            mByteReceivedBack = false
            try {
                outputStream.write(mValueToSend.toInt())
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            receiveCount++
            // Wait for 100ms before sending next byte, or as soon as
            // the sent byte has been read back.
            try {
                if (mByteReceivedBack) {
                    // Byte has been received
                    sendCount++
                } else {
                    // Timeout
                    loseCount++
                }
                runOnUiThread {
                    setTestCount(receiveCount.toString(), loseCount.toString(), sendCount.toString(), abnormalCount.toString())
                }
            } catch (ignored: InterruptedException) {
            }
        }
    }

    override fun sendData(data: String) {
        stopSendDataTest(null)
        resetTestCount()
        super.sendData(data)
    }

    private val portTest = AtomicBoolean(false)
    private val mByteReceivedBackSemaphore = Any()
    private var mValueToSend: Byte = 0
    private var mByteReceivedBack: Boolean = false
    private var sendCount = 0
    private var receiveCount = 0
    private var loseCount = 0
    private var abnormalCount = 0

    fun startSendDataTest(v: android.view.View) {
        portTest.set(true)
        resetTestCount()
        controlledPort(v)
    }

    private fun resetTestCount() {
        setTestCount("0", "0", "0", "0")
    }

    private fun setTestCount(receiveCount: String, loseCount: String, sendCount: String, abnormalCount: String) {
        tv_receiveDataCount.text = receiveCount
        tv_loseDataCount.text = loseCount
        tv_sendDataCount.text = sendCount
        tv_abnormalDataCount.text = abnormalCount
    }

    fun stopSendDataTest(v: android.view.View?) {
        portTest.set(false)
    }

}
