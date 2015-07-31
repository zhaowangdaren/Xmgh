package com.example.ustc_pc.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.ustc_pc.myapplication.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * Created by ustc-pc on 2015/4/15.
 */
public class AdapterImages extends BaseAdapter {

    ArrayList<String> datas;
    private LruCache<String, Bitmap> mMemoryCache;

    public AdapterImages(Context context, ArrayList<String> datas){
        this.datas = datas;
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
                @Override
                protected int sizeOf(String key, Bitmap bitmap){
                    return bitmap.getByteCount() / 1024;
                }
        };

    }




    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position != 0) {
            convertView = View.inflate(parent.getContext(), R.layout.imges_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            Bitmap bitmap = getBitmapFromMemCache(datas.get(position));
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                BitmapWorkerTask getImageAsyncTask = new BitmapWorkerTask(imageView);
                getImageAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), datas.get(position));
            }
        }else{
            convertView = View.inflate(parent.getContext(),R.layout.layout_take_photo_item, null);
        }
        return convertView;
    }

    class BitmapWorkerTask extends AsyncTask<String, Integer, Bitmap>{

        private final WeakReference<ImageView> imageViewWeakReference;
        public String mPath;
        public BitmapWorkerTask(ImageView imageView){
            this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            mPath = params[0];
            Bitmap bitmap = decodeThumbBitmapForFile(params[0], 100, 100);
            addBitmapToMemoryCache(params[0], bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            if(isCancelled()) result = null;
            if(result != null && imageViewWeakReference != null){
                final ImageView imageView1 = imageViewWeakReference.get();
//                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView1);

//                if(imageView1 != null && this == bitmapWorkerTask)imageView1.setImageBitmap(result);
                if(imageView1 != null)imageView1.setImageBitmap(result);
            }
        }

        private Bitmap decodeThumbBitmapForFile(String path, int reqWidth, int reqHeight){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            /**
             * ALPHA_8：每个像素占用1byte内存
             *ARGB_4444:每个像素占用2byte内存
             *ARGB_8888:每个像素占用4byte内存
             *RGB_565:每个像素占用2byte内存
             */
//            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inDensity = 100;
            options.inScreenDensity = 100;
            options.inTargetDensity = 100;
            options.inScaled = true;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            return imageCrop(bitmap, 80, 80);
        }


        private int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        private Bitmap imageCrop(Bitmap bitmap, int showWidth, int showHeight){
            if(bitmap == null){
                return null;
            }
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();
            if(bWidth <= showWidth && bHeight <= showHeight)return bitmap;
            int startX = 0;
            int startY = 0;
            if(bWidth > showWidth){
                startX  = (bWidth - showWidth)/2;
            }
            if(bHeight > showHeight){
                startY = (bHeight - showHeight)/2;
            }

            Bitmap bmp = Bitmap.createBitmap(bitmap, startX,startY,showWidth, showHeight,null,false);
            return bmp;
        }
    }
}
