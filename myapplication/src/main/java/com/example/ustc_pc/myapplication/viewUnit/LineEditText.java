package com.example.ustc_pc.myapplication.viewUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by ustc-pc on 2015/5/21.
 */
public class LineEditText extends EditText {
    private Paint mPaint;

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(2);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawLine(0, getHeight() - 1, getWidth() - 2, getHeight() - 1, mPaint);

    }


    public void setFocus(){
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(4);
    }

    public void setUnfocus(){
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(2);
    }
}
