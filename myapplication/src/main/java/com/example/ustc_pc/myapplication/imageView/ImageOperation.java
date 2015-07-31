package com.example.ustc_pc.myapplication.imageView;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by ustc-pc on 2015/3/16.
 */
public class ImageOperation {

    public static boolean cancelPotentialWork(String url, ImageView imageView){
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask != null){
            final String bitmapData = bitmapWorkerTask.data;
            if((bitmapData == "") || ( !bitmapData.equals(url) )){
                bitmapWorkerTask.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if(imageView != null){
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable){
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
