package com.hd.tools.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by hd on 2017/6/12.
 *
 */
public class BackgroundVideoView extends VideoView {

    public BackgroundVideoView(Context context) {
        super(context);
    }

    public BackgroundVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackgroundVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
