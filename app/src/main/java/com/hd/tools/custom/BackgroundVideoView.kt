package com.hd.tools.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView

/**
 * Created by hd on 2017/11/22.
 *
 */
class BackgroundVideoView : VideoView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
