package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.Paper;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/4/18.
 */
public class ActivityHistoryAdapter extends BaseAdapter {
    ArrayList<Paper> papers;
    public ActivityHistoryAdapter(ArrayList<Paper> data){
        papers = data;
    }
    @Override
    public int getCount() {
        return papers.size();
    }

    @Override
    public Object getItem(int position) {
        return papers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.activity_history_test_item,null);
            viewHolder = new ViewHolder();
            viewHolder.mNameTV = (TextView) convertView.findViewById(R.id.textView_paper_name_history_test_item);
            viewHolder.mSpendTimeTV = (TextView)convertView.findViewById(R.id.textView_spend_time_history_paper_test_item);
            viewHolder.mScoreTV = (TextView)convertView.findViewById(R.id.textView_score_history_paper_test_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mNameTV.setText(papers.get(position)._strName);
        viewHolder.mSpendTimeTV.setText(papers.get(position)._strSpendTime);
        viewHolder.mScoreTV.setText(""+papers.get(position)._iScore);
        return convertView;
    }

    class ViewHolder{
        TextView mNameTV, mSpendTimeTV, mScoreTV;
    }
}
