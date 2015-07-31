package com.example.ustc_pc.myapplication.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.ScrollViewWithListView;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/2/7.
 */
public class ActivityPaperAnalysisAdapter extends PagerAdapter {
    private ArrayList<Question> _questions;
    private Context _context;
    private int _iType = 0;//0-paper analysis; 1-error question; 2-favorite question

    private DBHelper dbHelper;
    /**
     *
     * @param context
     * @param questions
     * @param type 0-paper analysis; 1-error question; 2-favorite question
     */
    public ActivityPaperAnalysisAdapter(Context context, ArrayList<Question> questions, int type){
        _context = context;
        _questions = questions;
        _iType = type;
    }

    @Override
    public int getCount() {
        return _questions.size();
    }

    /**这个方法，是从ViewGroup中移出当前View**/
    public void destroyItem(View container,int position,Object object){
        ((ViewGroup) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        // TODO Auto-generated method stub
        ScrollView view;
            view = (ScrollView)View.inflate(_context, R.layout.activity_paper_analysis_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.contentTV = (TextView) view.findViewById(R.id.textView_question_content);
            viewHolder.contentSonTV = (TextView) view.findViewById(R.id.textView_question_content_son);
            viewHolder.listView = (ScrollViewWithListView) view.findViewById(R.id.listView_question_option);
            viewHolder.analysisTV = (TextView) view.findViewById(R.id.textView_analysis);
            viewHolder.analysisSonTV = (TextView) view.findViewById(R.id.textView_analysis_son);
            viewHolder.rightAnswerTV = (TextView)view.findViewById(R.id.textView_right_answer);

            viewHolder.userSelectedTV = (TextView)view.findViewById(R.id.textView_user_selected);
            viewHolder.isRespondRightTV = (TextView)view.findViewById(R.id.textView_is_respond_right);
            viewHolder.questionSpendTimeTV = (TextView)view.findViewById(R.id.textView_question_spend_time);

        Question question = _questions.get(position);
        String content = question._strSubject;
        if(content != null && content.length() > 0) {
            viewHolder.contentTV.setText(content);
        }else viewHolder.contentTV.setVisibility(View.GONE);

        String contenSon = question._strSubjectSon;
        if(contenSon != null && contenSon.length() > 0) {
            viewHolder.contentSonTV.setText(contenSon);
        }else viewHolder.contentSonTV.setVisibility(View.GONE);

        viewHolder.analysisTV.setText(Html.fromHtml(question._analysis));
        viewHolder.analysisSonTV.setText(question._analysisSon);

        //set right answer
        viewHolder.rightAnswerTV.setText(question._strAnswer);
        viewHolder.userSelectedTV.setText(question._strUserSelected);

        //spend time
        question._lSpendTime = question._lEndTime - question._lStartTime;
        viewHolder.questionSpendTimeTV.setText(String.valueOf((question._lSpendTime/1000)/60)+":"+String.valueOf((question._lSpendTime/1000)%60));

        if(question._isRight) viewHolder.isRespondRightTV.setText(_context.getResources().getString(R.string.respond_right));
        else viewHolder.isRespondRightTV.setText(_context.getResources().getString(R.string.respond_wrong));

         MyAdapter myAdapter = new MyAdapter(position);
         viewHolder.listView.setAdapter(myAdapter);

        if(_iType == 2){
            viewHolder.userSelectedTV.setVisibility(View.GONE);
            viewHolder.isRespondRightTV.setVisibility(View.GONE);
            viewHolder.yourSelectedIsTV = (TextView) view.findViewById(R.id.textView_your_selected_is);
            viewHolder.yourSelectedIsTV.setVisibility(View.GONE);
            viewHolder.zuoDaYongShiTV = (TextView)view.findViewById(R.id.textView_zuodayongshi);
            viewHolder.zuoDaYongShiTV.setVisibility(View.GONE);
            viewHolder.miaoTV = (TextView)view.findViewById(R.id.textView_miao);
            viewHolder.miaoTV.setVisibility(View.GONE);
            viewHolder.questionSpendTimeTV.setVisibility(View.GONE);
        }

        view.setTag(viewHolder);
        ((ViewPager) container).addView(view);
        return view;
    }


    private String int2Letter(int index){
        String str = "";
        switch (index){
            case 0:
                str="A";
                break;
            case 1:
                str="B";
                break;
            case 2:
                str="C";
                break;
            case 3:
                str="D";
                break;
            case 4:
                str="E";
                break;
            case 5:
                str="F";
                break;
            default:
                str="G";
        }
        return str;
    }





     
    class MyAdapter extends BaseAdapter {

        private int _iCurrentIndex;
        public MyAdapter(int index){
            _iCurrentIndex = index;
        }
        @Override
        public int getCount() {
            return _questions.get(_iCurrentIndex)._options.size();
        }

        @Override
        public Object getItem(int position) {
            return _questions.get(_iCurrentIndex)._options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OptionViewHolder optionViewHolder;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.layout_question_option, null);
                optionViewHolder = new OptionViewHolder();
                optionViewHolder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTV_option);
                optionViewHolder.textView = (TextView) convertView.findViewById(R.id.textView_option_content);
                convertView.setTag(optionViewHolder);
            }else{
                optionViewHolder = (OptionViewHolder) convertView.getTag();
            }
            optionViewHolder.checkedTextView.setTextColor(-16777216);
            switch(position){
                case 0:
                    optionViewHolder.checkedTextView.setText("A");
                    break;
                case 1:
                    optionViewHolder.checkedTextView.setText("B");
                    break;
                case 2:
                    optionViewHolder.checkedTextView.setText("C");
                    break;
                case 3:
                    optionViewHolder.checkedTextView.setText("D");
                    break;
                default:optionViewHolder.checkedTextView.setText("E");
            }

            Question question = _questions.get(_iCurrentIndex);
            optionViewHolder.textView.setText(question._options.get(position));
            boolean answer = false;
            if(question._answerMap.get(position) != null) answer = question._answerMap.get(position);
            if(answer){
                optionViewHolder.checkedTextView.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.bg_answer_right));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }else{
                optionViewHolder.checkedTextView.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.bg_option_unclick));
            }
            boolean userSelected = false;
            if(question._userSelectedMap.get(position) != null)userSelected = question._userSelectedMap.get(position);
            if( userSelected && !answer){
                optionViewHolder.checkedTextView.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.bg_answer_error));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }

            return convertView;
        }


    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }


    class ViewHolder{
        TextView contentTV, contentSonTV, rightAnswerTV, yourSelectedIsTV, userSelectedTV,isRespondRightTV, zuoDaYongShiTV, questionSpendTimeTV,miaoTV,
                analysisTV, analysisSonTV;
        ScrollViewWithListView listView;
    }

    class OptionViewHolder{
        CheckedTextView checkedTextView;
        TextView textView;
    }
}
