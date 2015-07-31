package com.example.ustc_pc.myapplication.imageView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.ustc_pc.myapplication.net.NetUtil;

import java.lang.ref.WeakReference;

/**
 * Created by ustc-pc on 2015/3/16.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;
    public  String data = "";

    public BitmapWorkerTask(ImageView imageView){
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        return NetUtil.getImageFromServer(data);
    }

    protected void onPostExecute(Bitmap bitmap){
        if(isCancelled())bitmap = null;

        if(imageViewWeakReference != null && bitmap != null){
            final ImageView imageView = imageViewWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask = ImageOperation.getBitmapWorkerTask(imageView);

            if(this == bitmapWorkerTask && imageView != null)imageView.setImageBitmap(bitmap);
        }
    }
}
