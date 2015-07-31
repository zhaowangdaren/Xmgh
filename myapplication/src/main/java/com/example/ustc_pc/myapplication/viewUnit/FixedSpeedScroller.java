package com.example.ustc_pc.myapplication.viewUnit;

import android.content.Context;
import android.widget.Scroller;

/**
 * For set viewPager scroll speed
 * Created by ustc-pc on 2015/3/16.
 */
public class FixedSpeedScroller extends Scroller {
    private int mDuration = 5000;//speed
    public FixedSpeedScroller(Context context) {
        super(context);
    }
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration){
        super.startScroll(startX, startY,dx,dy,mDuration);
    }
    @Override
    public void startScroll(int startX, int startY, int dx, int dy){
        super.startScroll(startX, startY, dx,dy, mDuration);
    }
}
