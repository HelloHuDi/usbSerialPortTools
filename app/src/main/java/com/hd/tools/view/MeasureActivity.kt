package com.hd.tools.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hd.serialport.method.DeviceMeasureController
import com.hd.serialport.utils.HexDump
import com.hd.tools.R
import kotlinx.android.synthetic.main.controller_port.*
import kotlinx.android.synthetic.main.device_measure_title.*
import kotlinx.android.synthetic.main.port_list.*
import org.jetbrains.anko.toast
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by hd on 2017/11/22 .
 *
 */
abstract class MeasureActivity<T> : BaseActivity() {

    protected val TAG = MeasureActivity::class.java.simpleName

    protected val mEntries = ArrayList<T>()

    protected var port: T? = null

    protected var mAdapter: ArrayAdapter<T>? = null

    private val open = AtomicBoolean(false)

    private var receiveNumber: Int = 0

    @LayoutRes
    abstract fun setLayoutId(): Int

    abstract fun initContent()

    abstract fun initDeviceList()

    abstract fun setListTitleName(t: T): CharSequence?

    abstract fun startConnect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayoutId())
        initContent()
        initAdapter()
        initDeviceList()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopConnect()
    }

    private fun initAdapter() {
        mAdapter = object : ArrayAdapter<T>(this, android.R.layout.simple_expandable_list_item_1, mEntries) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val deviceContent: TextView
                deviceContent = if (convertView == null) {
                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    inflater.inflate(android.R.layout.simple_list_item_1, null) as TextView
                } else {
                    convertView as TextView
                }
                deviceContent.textSize = resources.getDimension(R.dimen.text_size)
                if (port === mEntries[position]) {
                    deviceContent.setBackgroundColor(Color.RED)
                } else {
                    deviceContent.setBackgroundColor(Color.TRANSPARENT)
                }
                deviceContent.text = setListTitleName(mEntries[position])
                return deviceContent
            }
        }
        deviceList.adapter = mAdapter
        deviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (port != null && open.get()) {
                btn_open_or_close.callOnClick()
            }
            Log.d(TAG, "Pressed item " + position)
            if (position >= mEntries.size) {
                Log.w(TAG, "Illegal position.")
                return@OnItemClickListener
            }
            receiveNumber = 0
            port = mEntries[position]
            mAdapter?.notifyDataSetChanged()
        }
    }

    /** [btn_open_or_close]*/
    fun controlledPort(v: android.view.View) {
        if (open.get()) {//close
            open.set(false)
            stopConnect()
            btn_open_or_close.text = resources.getString(R.string.open_port)
        } else {//open
            if (port == null) {
                toast(resources.getString(R.string.choose_device))
            } else {
                open.set(true)
                startConnect()
                btn_open_or_close.text = resources.getString(R.string.open_port)
            }
        }
    }

    /** [btn_send]*/
    fun sendDataToPort(v: android.view.View) {
        if (!open.get()) {
            receiveData("请先打开串口")
            return
        }
        sendData(et_write_data.text.toString().trim())
    }

    /** [btn_clear]*/
    @SuppressLint("SetTextI18n")
    fun clearText(v: android.view.View) {
        tv_result.text = ""
        tv_receive_number.text = "receive : 0"
        receiveNumber = 0
    }

    fun setSpinnerAdapter(spinner: Spinner, data: Array<String>) {
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                if (convertView != null && convertView is TextView)
                    convertView.textSize = resources.getDimension(R.dimen.text_size)
                return super.getView(position, convertView, parent)
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun sendData(data: String) {
        val arrayList = ArrayList<ByteArray>()
        val ins = data.trim()
        if (cb_hex.isChecked) {// HEX
            if (ins.isNotEmpty()) {
                val hexs = ins.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val bytes = ByteArray(hexs.size)
                var okflag = true
                for (index in hexs.indices) {
                    val hex = hexs[index]
                    try {
                        val d = Integer.parseInt(hex, 16)
                        if (d > 255) {
                            receiveData(String.format("%s 大于0xff", hex))
                            okflag = false
                        } else {
                            bytes[index] = d.toByte()
                        }
                    } catch (e: NumberFormatException) {
                        receiveData(String.format("%s 不是十六进制数", hex))
                        e.printStackTrace()
                        okflag = false
                    }
                }
                arrayList.add(bytes)
                if (okflag && arrayList.size > 0) {
                    DeviceMeasureController.write(arrayList)
                }
            } else {
                receiveData("要发送的十六进制数据为空")
            }
        } else {
            try {
                arrayList.add(ins.toByteArray(charset("UTF-8")))
                DeviceMeasureController.write(arrayList)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                receiveData("转换编码失败")
            }
        }
    }

    private val handler = android.os.Handler()

    @SuppressLint("SetTextI18n")
    protected fun receiveData(string: String){
        runOnUiThread {
            tv_receive_number.text = "receive: " + receiveNumber
            tv_result.append(string + "\n")
            handler.post({ sv_result.fullScroll(ScrollView.FOCUS_DOWN) })
        }
    }

    protected fun receiveData(data: ByteArray){
        val result = if (cb_hex_rev.isChecked) {
            HexDump.toHexString(data)
        } else {
            val stringier = StringBuilder()
            for (b in data) {
                stringier.append(b.toInt()).append("   ")
            }
            stringier.toString()
        }
        receiveNumber += data.size
        receiveData(result)
    }

    private fun stopConnect() {
        DeviceMeasureController.stop()
    }

}