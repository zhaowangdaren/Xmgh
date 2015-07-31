package com.example.ustc_pc.myapplication.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.myInterface.DownloadFinishedInterface;
import com.example.ustc_pc.myapplication.unit.FileOperation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ustc-pc on 2015/2/3.
 * String...params 参数形式：url+fileName
 */
public class DownloadTask extends AsyncTask<String, Integer, String>{

    private Context context;
    private ProgressDialog progressDialog;
    private PowerManager.WakeLock mWakeLock;
    private DownloadFinishedInterface downloadFinishedInterface;
    public DownloadTask(Context context, ProgressDialog progressDialog, DownloadFinishedInterface downloadFinishedInterface){
        this.context = context;
        this.progressDialog= progressDialog;
        this.downloadFinishedInterface = downloadFinishedInterface;
    }
    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        try{
            URL url = new URL(params[0]+params[1]);

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
    protected void onPreExecute(){
        super.onPreExecute();
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
            downloadFinishedInterface.downloadFinished();
        }
    }

}
