package com.hd.tools.help

import android.content.Context
import android.support.annotation.RawRes
import android.widget.FrameLayout
import android.widget.VideoView
import com.hd.serialport.utils.L
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Created by hd on 2017/11/20 .
 *
 */
class ShowVideo(private val context: Context,private val videoView: VideoView,//
                private @RawRes val videoRawId:Int, private val VIDEO_NAME:String){

    fun showVideo(){
        var videoFile = context.getFileStreamPath(VIDEO_NAME)
        if (!videoFile.exists()) {
            videoFile = copyVideoFile()
        }
        playVideo(videoFile)
    }

    fun stopVideo(){
        videoView.stopPlayback()
    }

    private fun copyVideoFile(): File? {
        try {
            val fos = context.openFileOutput(VIDEO_NAME, Context.MODE_PRIVATE)
            val inn = context.resources.openRawResource(videoRawId)
            val buff = ByteArray(1024)
            var len: Int
            while (true) {
                len = inn.read(buff)
                if (len == -1) break
                fos.write(buff, 0, len)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val videoFile = context.getFileStreamPath(VIDEO_NAME)
        if (!videoFile.exists()) {
            L.d("video file has problem, are you sure you have health_live.mp4 in res/raw folder?")
            return null
        }
        return videoFile
    }

    private fun playVideo(videoFile: File?) {
        if(videoFile==null)return
        videoView.setVideoPath(videoFile.path)
        videoView.layoutParams = FrameLayout.LayoutParams(-1, -1)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
    }

}