package com.example.ustc_pc.myapplication.viewUnit;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by ustc-pc on 2015/7/14.
 */
public class CircularProgressDrawable extends Drawable {


    private Paint paint;

    /**
     * Ring progress
     */
    protected float progress;
    /**
     * Width of the filling ring, not radius
     */
    protected int ringWidth;

    /**
     * Color for the empty outer ring
     */
    protected int outlineColor;
    /**
     * Color for the completed ring
     */
    protected int ringColor;

    /**
     * Rectangle where the filling ring will be drawn into.
     */
    protected RectF arcElements;

    CircularProgressDrawable(int ringWidth, int outlineColor, int ringColor){
        this.ringWidth = ringWidth;
        this.outlineColor = outlineColor;
        this.ringColor = ringColor;

        paint = new Paint();
        paint.setAntiAlias(true);

        progress = 0;
        arcElements = new RectF();
    }
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        //Calculations on the components sizes
        int size = Math.min(bounds.height(), bounds.width());
        float outerRadius = (size / 2) - (ringWidth / 2);
        float offsetX = (bounds.width() - outerRadius * 2) / 2;
        float offsetY = (bounds.height() - outerRadius * 2) / 2;

        //Draw outline circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(outlineColor);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius, paint);

        int halfRingWidth = ringWidth / 2;
        float arcX0 = offsetX + halfRingWidth;
        float arcY0 = offsetY + halfRingWidth;
        float arcX = offsetX + outerRadius * 2 - halfRingWidth;
        float arcY = offsetY + outerRadius * 2 - halfRingWidth;

        //Outer Circle
        paint.setColor(ringColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ringWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        arcElements.set(arcX0, arcY0, arcX, arcY);
        canvas.drawArc(arcElements, 89, progress, false, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 1 - paint.getAlpha();
    }

    public void setProgress(float progress){
        this.progress = -360 * progress;

        invalidateSelf();
    }

    public void setRingColor(int ringColor){
        this.ringColor = ringColor;
    }

    public void setRingWidth(int ringWidth){
        this.ringWidth = ringWidth;
    }

    public void setOutlineColor(int outlineColor){
        this.outlineColor = outlineColor;
    }

    public static class Builder{
        /**
         * Width of the filling ring, not radius
         */
        protected int ringWidth;

        /**
         * Color for the empty outer ring
         */
        protected int outlineColor;
        /**
         * Color for the completed ring
         */
        protected int ringColor;

        public Builder setRingWidth(int ringWidth){
            this.ringWidth = ringWidth;
            return this;
        }

        public Builder setOutlineColor(int outlineColor){
            this.outlineColor = outlineColor;
            return this;
        }

        public Builder setRingColor(int ringColor){
            this.ringColor = ringColor;
            return this;
        }

        public CircularProgressDrawable create(){
            return new CircularProgressDrawable(ringWidth,outlineColor,ringColor);
        }
    }
}
