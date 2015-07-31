package com.example.ustc_pc.myapplication.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.imageView.AsyncDrawable;
import com.example.ustc_pc.myapplication.imageView.BitmapWorkerTask;
import com.example.ustc_pc.myapplication.imageView.ImageOperation;
import com.example.ustc_pc.myapplication.unit.Paper;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/3/16.
 */
public class ActivityIndividuationAdapter extends BaseAdapter {

    ArrayList<Paper> individuations;

    public ActivityIndividuationAdapter (ArrayList<Paper> datas){
        individuations = datas;
    }

    @Override
    public int getCount() {
        return individuations.size();
    }

    @Override
    public Object getItem(int position) {
        return individuations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.activity_individuation_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.textView_name_individuation);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_name_individuation);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTV.setText(individuations.get(position)._strName);
//        loadBitmap(parent.getResources(), individuations.get(position)._imageUrl, viewHolder.imageView);
        return convertView;
    }

    private void loadBitmap(Resources res, String url, ImageView imageView ){
        if(ImageOperation.cancelPotentialWork(url, imageView)){
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            Bitmap bitmap = null;
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(res, bitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }
    class ViewHolder {
        TextView nameTV;
        ImageView imageView;
    }
}
