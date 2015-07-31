package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/3/27.
 */
public class ActivityViewAssessmentAdapter extends BaseAdapter {

    ArrayList<AssessmentScore.DoneKpSocre> datas;
    public ActivityViewAssessmentAdapter(ArrayList<AssessmentScore.DoneKpSocre> datas){
        this.datas = datas;
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
            convertView = View.inflate(parent.getContext(), R.layout.activity_view_assessment_detail_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.colorIV = (ImageView)convertView.findViewById(R.id.imageView_score_detail_color);
            viewHolder.kpScoreTV = (TextView)convertView.findViewById(R.id.textView_score_detail_kp_score);
            viewHolder.kpNameTV = (TextView)convertView.findViewById(R.id.textView_score_detail_kp);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.colorIV.setBackgroundColor(datas.get(position)._iColor);
        viewHolder.kpScoreTV.setText(""+datas.get(position)._iScore);
        viewHolder.kpNameTV.setText(datas.get(position)._name);

        convertView.setTag(viewHolder);
        return convertView;
    }

    class ViewHolder {
        ImageView colorIV;
        TextView kpNameTV ,kpScoreTV;
    }
}
