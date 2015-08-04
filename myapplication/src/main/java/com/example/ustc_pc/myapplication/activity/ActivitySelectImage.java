package com.example.ustc_pc.myapplication.activity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ustc_pc.myapplication.BuildConfig;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.heightPerformanceImageView.ImageCache;
import com.example.ustc_pc.myapplication.heightPerformanceImageView.ImageFetcher;
import com.example.ustc_pc.myapplication.heightPerformanceImageView.RecyclingImageView;
import com.example.ustc_pc.myapplication.heightPerformanceImageView.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class ActivitySelectImage extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private GridView mGridView;
    private ArrayList<String> mDatas;

    private
    Intent result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_iamge);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);

        initView();
        initData();
    }


    private void initData() {
        mDatas = new ArrayList<>();
        GetImagesPathsAsyncTask getImagesPathsAsyncTask = new GetImagesPathsAsyncTask();
        getImagesPathsAsyncTask.execute();

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(this, mDatas);
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(android.R.drawable.ic_menu_gallery);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
    }


    private void initView() {
        mGridView = (GridView)findViewById(R.id.gridView_images);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_select_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_take_photo){
            openCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getImagePaths() {
        GetImagesPathsAsyncTask getImagePaths = new GetImagesPathsAsyncTask();
        getImagePaths.execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String path = mDatas.get(position - mAdapter.getNumColumns());
            path = Uri.decode(path);
            Uri uri = Uri.fromFile(new File(path));
            cropPhoto(uri);

    }

    private static final int OPEN_CAMERA_CODE = 10;
    private static final int OPEN_GALLERY_CODE = 11;
    private static final int CROP_PHOTO_CODE = 12;
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){
        if(resultCode == 1)return;
        if(data == null)return;
        switch (requestCode){
            case OPEN_CAMERA_CODE:
                Bundle bundle = data.getExtras();
                Uri uri = data.getData();
                cropPhoto(uri);
                break;
            case CROP_PHOTO_CODE:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                setResult(-1, data);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveUserPhoto(data);
                    }
                }).start();
                finish();
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX",100);
        intent.putExtra("outputY",100);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_PHOTO_CODE);
    }

    private void saveUserPhoto(Intent intent){
        Bundle bundle = intent.getExtras();
        Bitmap bitmap = (Bitmap)bundle.get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        userSharedPreference.setUserPhoto(imageString);

    }



    class GetImagesPathsAsyncTask extends AsyncTask<Integer, Integer, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = ActivitySelectImage.this.getContentResolver();
            Cursor cursor = contentResolver.query(imageURI, null,MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg"},MediaStore.Images.Media.DATE_MODIFIED);
            if(cursor == null)return null;

            ArrayList<String> result = new ArrayList<>();
            while(cursor.moveToNext()){
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                result.add(filePath);
            }
            cursor.close();
            Collections.reverse(result);
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            if( !result.isEmpty()){
                mDatas.clear();
                mDatas.addAll(result);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    class ImageAdapter extends BaseAdapter{

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        private ArrayList<String> aDatas;

        public ImageAdapter(Context context, ArrayList<String> datas){
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
            aDatas = datas;
        }

        @Override
        public int getCount() {
            if(getNumColumns() == 0) {
                return 0;
            }
            return aDatas.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : aDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage(aDatas.get(position - mNumColumns), imageView);
            return imageView;
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }

}
