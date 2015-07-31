package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.Question;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/21.
 */
public class AnswerStAdapter extends BaseAdapter{
    private ArrayList<Question> _questions;

    public AnswerStAdapter(ArrayList<Question> questions){
        _questions = questions;

    }

    @Override
    public int getCount() {
        return _questions.size();
    }

    @Override
    public Object getItem(int position) {
        return _questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.layout_textview, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView)convertView.findViewById(R.id.textView_layout);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Question question = _questions.get(position);
        int index = position + 1;
        viewHolder.mTextView.setText(""+index);
        if(question._iUserHasDone == 0){
            viewHolder.mTextView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_option_unclick));

        }else {
            viewHolder.mTextView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_option_click));
            viewHolder.mTextView.setTextColor(-1);//white
        }

        return convertView;
    }

    class ViewHolder{
        TextView mTextView;
    }
}
