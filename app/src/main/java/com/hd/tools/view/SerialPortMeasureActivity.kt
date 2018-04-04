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
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

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
                        receiveDataWithLineFeed(message)
                    }

                    override fun measuring(path: String, data: ByteArray) {
                        if (!portTest.get()) {
                            receiveData(path, data)
                        } else {
                            readTest(data)
                        }
                    }

                    override fun write(outputStream: OutputStream) {
                        outputIO = outputStream
                    }
                })
    }

    override fun writeData(arrayList: ArrayList<ByteArray>) {
        arrayList.forEach { outputIO?.write(it) }
    }

    override fun sendData(data: String) {
        stopSendDataTest(null)
        resetTestCount()
        super.sendData(data)
    }

    private var outputIO: OutputStream? = null
    private val portTest = AtomicBoolean(false)
    private val mByteReceivedBackSemaphore = java.lang.Object()
    private var mValueToSend: Byte = 0
    private var mByteReceivedBack: Boolean = false
    private var sendCount = 0
    private var receiveCount = 0
    private var loseCount = 0
    private var abnormalCount = 0

    fun startSendDataTest(v: android.view.View) {
        if (port != null && open.get()) {
            resetTestCount()
            portTest.set(true)
            thread { writeTest() }
        } else if (port == null) {
            receiveDataWithLineFeed(resources.getString(R.string.choose_device))
        } else {
            receiveDataWithLineFeed(resources.getString(R.string.please_open_port_first))
        }
    }

    private fun writeTest() {
        while (portTest.get()) {
            synchronized (mByteReceivedBackSemaphore) {
                mByteReceivedBack = false
                try {
                    outputIO?.write(mValueToSend.toInt())
                } catch (e: IOException) {
                    L.e("write data test error :$e")
                    return
                }
                sendCount++
                // Wait for 550ms before sending next byte, or as soon as
                // the sent byte has been read back.
                try {
                    mByteReceivedBackSemaphore.wait(550)
                    if (mByteReceivedBack) {
                        // Byte has been received
                        receiveCount++
                    } else {
                        // Timeout
                        loseCount++
                    }
                    runOnUiThread {
                        setTestCount(receiveCount.toString(), loseCount.toString(), sendCount.toString(), abnormalCount.toString())
                    }
                } catch (ignored: InterruptedException) {
                    L.e("write data test error :$ignored")
                }
            }
        }
    }

    private fun readTest(data: ByteArray) {
        synchronized (mByteReceivedBackSemaphore) {
            var i = 0
            L.d("receive data :" + Arrays.toString(data))
            while (i < data.size) {
                if (data[i] == mValueToSend && !mByteReceivedBack) {
                    mValueToSend++
                    // This byte was expected
                    // Wake-up the sending thread
                    mByteReceivedBack = true
                    mByteReceivedBackSemaphore.notify()
                } else {
                    // The byte was not expected
                    abnormalCount++
                }
                i++
            }
        }
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
