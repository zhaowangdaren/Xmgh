package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.Question;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/2/4.
 */
public class ActivityPaperReportGVAdapter extends BaseAdapter {
    ArrayList<Question> questions;

    public ActivityPaperReportGVAdapter(ArrayList<Question> questions){
        this.questions = questions;
    }
    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.layout_question_option_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (CheckedTextView)convertView.findViewById(R.id.checkedTV_option);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Question question = questions.get(position);
        switch (question._iType){
            case 0:
                if(question._isRight){
                    viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_right));
                    viewHolder.textView.setTextColor(-1);//white
                }
                else {
                    viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_error));
                    viewHolder.textView.setTextColor(-1);//white
                }
                break;
            case 1:
                if(question._isRight)viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_right));
                else if(question._isHalfRight){
                    //viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_half_right));
                    viewHolder.textView.setTextColor(-1);//white
                }
                else {
                    viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_error));
                    viewHolder.textView.setTextColor(-1);//white
                }
                break;
        }
        int index = position + 1;
        viewHolder.textView.setText("" + index);
        return convertView;
    }

    class ViewHolder{
        CheckedTextView textView;
    }
}
