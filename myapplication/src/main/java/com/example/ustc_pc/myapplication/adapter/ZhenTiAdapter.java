package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.Paper;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/4/11.
 */
public class ZhenTiAdapter extends BaseAdapter{

    private ArrayList<Paper> datas;
    public ZhenTiAdapter(ArrayList<Paper> kpPaperses){
        datas = kpPaperses;
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
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.layout_zhenti_papers_item, parent);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.textView_zhenti_papers_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textView.setText(datas.get(position)._strName);
        return convertView;
    }

    class ViewHolder{
        TextView textView;
    }
}
