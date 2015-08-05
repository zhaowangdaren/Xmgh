package com.example.ustc_pc.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.FileOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivitySetting extends ActionBarActivity implements View.OnClickListener{

    private TextView mUpdateTV, mAboutTV;

    private float mVersion = 0;
    private String mURL, mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setTitle(R.string.action_settings);

        initView();


    }

    private void initView() {
        mUpdateTV = (TextView)findViewById(R.id.textView_check_update);
        mAboutTV = (TextView)findViewById(R.id.textView_about_app);

        mUpdateTV.setOnClickListener(this);
        mAboutTV.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_check_update:
                checkUpdate();
                break;
            case R.id.textView_about_app:
                showAboutApp();
                break;
        }
    }

    private void showAboutApp() {

    }

    private void checkUpdate() {
        if(!Util.isConnect(this)){
            Toast.makeText(this, getString(R.string.no_network_try_again), Toast.LENGTH_SHORT).show();
        }else{
            CheckUpdateAsync checkUpdateAsync = new CheckUpdateAsync();
            checkUpdateAsync.execute();
        }
    }

    private void showUpdateAble() {
        float curV = getCurrentVersionName();
        if(curV < mVersion){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.update_available));
            builder.setPositiveButton(R.string.update,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startDownloadAPK();
                }
            });
            builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.show();
        }else{
            Toast.makeText(this, getString(R.string.is_latest_version),Toast.LENGTH_SHORT).show();
        }
    }

    private void startDownloadAPK() {
        UpdateDownloadTask downloadTask = new UpdateDownloadTask(this);
        downloadTask.execute(mURL, mFileName);
    }

    private float getCurrentVersionName(){
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return Float.valueOf(packageInfo.versionName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private class CheckUpdateAsync extends AsyncTask<String, Integer, String>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivitySetting.this, null,getString(R.string.loading));
        }

        @Override
        protected String doInBackground(String... params) {
            String result = NetUtil.checkUpdate();
            return result;
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            if(result.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    mVersion = Float.valueOf(jsonObject.getString("version"));
                    mURL = jsonObject.getString("url");
                    mFileName = jsonObject.getString("fileName");
                    showUpdateAble();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private class UpdateDownloadTask extends AsyncTask<String, Integer, String>{

        private Context context;
        private ProgressDialog progressDialog;
        private PowerManager.WakeLock mWakeLock;
        public UpdateDownloadTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            //Take CPU lock to prevent CPU from going off if the user
            //presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            HttpURLConnection connection = null;
            try{
                URL url = new URL(params[0]);

                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "Server returned HTTP "+ connection.getResponseCode()+" "+
                            connection.getResponseMessage();
                }
                //This will be useful to display download percentage
                //might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                //download the file
                inputStream = connection.getInputStream();

                outputStream = new FileOutputStream(FileOperation.APP_PATH + params[1]);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while((count = inputStream.read(data)) != -1){
                    if(isCancelled()){
                        inputStream.close();
                        return null;
                    }
                    total += count;
                    if(fileLength > 0)//only if total length is known
                    {
                        publishProgress((int) (total * 100 / fileLength));
                        onProgressUpdate((int) (total * 100 / fileLength));
                    }
                    outputStream.write(data, 0, count);
                }

            }catch (Exception e){
                return e.toString();
            }finally {
                try{
                    if(outputStream != null)
                        outputStream.close();
                    if(inputStream != null)
                        inputStream.close();
                }catch (IOException ignored){}
                if(connection != null)
                    connection.disconnect();
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result){
            mWakeLock.release();
            progressDialog.dismiss();
            if(result != null){
                Log.e("Download error:", result);
            }else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                File file = new File(FileOperation.APP_PATH + mFileName);
                installApk(file);
            }
        }

        private void installApk(File file){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            startActivity(intent);
        }

    }
}
