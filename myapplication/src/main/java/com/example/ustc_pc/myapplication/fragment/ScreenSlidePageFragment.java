package com.example.ustc_pc.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.activity.ActivityPaperG;
import com.example.ustc_pc.myapplication.activity.ActivityPaperReport;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.ScrollViewWithGridView;
import com.example.ustc_pc.myapplication.unit.ScrollViewWithListView;

import java.util.HashMap;

/**
 * Created by ustc-pc on 2015/5/11.
 */
public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{
    public static final int TYPE_QUESTION = 1;
    public static final int TYPE_ANSWER_SHEET = 2;
    public static final int TYPE_ANALYSIS = 3;
    public static final int TYPE_VIEW_NOTE_AND_FAVORITE_QUESTION = 4;
    private Question question;
    private Paper paper;
    private int iType = 1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        iType = getArguments().getInt("TYPE");
        paper = (Paper) getArguments().getSerializable("PAPER");
        question = (Question) getArguments().getSerializable("QUESTION");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        ViewGroup rootView = null;
        switch (iType){
            case TYPE_QUESTION:
                rootView = (ViewGroup)inflater.inflate(R.layout.layout_question, container,false);
                initQuestionView(rootView);
                break;
            case TYPE_ANSWER_SHEET:
                rootView = (ViewGroup)inflater.inflate(R.layout.layout_answer_sheets,container,false);
                ScrollViewWithGridView gridView = (ScrollViewWithGridView)rootView.findViewById(R.id.gridView_answer);
                AnswerListAdapter listAdapter = new AnswerListAdapter();
                gridView.setAdapter(listAdapter);
                Button submitBT = (Button)rootView.findViewById(R.id.button_submit);
                submitBT.setOnClickListener(this);
                break;
            case TYPE_ANALYSIS:
                rootView = (ViewGroup)inflater.inflate(R.layout.activity_paper_analysis_item,container, false);
                initAnalysisView(rootView);
                break;
            case TYPE_VIEW_NOTE_AND_FAVORITE_QUESTION:
                rootView = (ViewGroup)inflater.inflate(R.layout.activity_paper_analysis_item,container, false);
                initViewNoteAndFavoriteQuestion(rootView);
                break;
        }

        return rootView;
    }

    public int getiType(){
        return iType;
    }

    private void initViewNoteAndFavoriteQuestion(ViewGroup view) {
        AnalysisViewHolder viewHolder = new AnalysisViewHolder();
        viewHolder.contentTV = (TextView) view.findViewById(R.id.textView_question_content);
        viewHolder.contentSonTV = (TextView) view.findViewById(R.id.textView_question_content_son);
        viewHolder.listView = (ScrollViewWithListView) view.findViewById(R.id.listView_question_option);
        viewHolder.analysisTV = (TextView) view.findViewById(R.id.textView_analysis);
        viewHolder.analysisSonTV = (TextView) view.findViewById(R.id.textView_analysis_son);
        viewHolder.rightAnswerTV = (TextView)view.findViewById(R.id.textView_right_answer);

        viewHolder.userSelectedTV = (TextView)view.findViewById(R.id.textView_user_selected);
        viewHolder.isRespondRightTV = (TextView)view.findViewById(R.id.textView_is_respond_right);
        viewHolder.questionSpendTimeTV = (TextView)view.findViewById(R.id.textView_question_spend_time);

        String content = question._strSubject;
        if(content != null && content.length() > 0) {
            viewHolder.contentTV.setText(content);
        }else viewHolder.contentTV.setVisibility(View.GONE);

        String contentSon = question._strSubjectSon;
        if(contentSon != null && contentSon.length() > 0) {
            viewHolder.contentSonTV.setText(contentSon);
        }else viewHolder.contentSonTV.setVisibility(View.GONE);

        viewHolder.analysisTV.setText(Html.fromHtml(question._analysis));
        viewHolder.analysisSonTV.setText(question._analysisSon);

        //set right answer
        viewHolder.rightAnswerTV.setText(question._strAnswer);
        viewHolder.userSelectedTV.setText(question._strUserSelected);

        //spend time
        question._lSpendTime = question._lEndTime - question._lStartTime;
        viewHolder.questionSpendTimeTV.setText(String.valueOf((question._lSpendTime/1000)/60)+":"+String.valueOf((question._lSpendTime/1000)%60));

        if(question._isRight) viewHolder.isRespondRightTV.setText(getResources().getString(R.string.respond_right));
        else viewHolder.isRespondRightTV.setText(getResources().getString(R.string.respond_wrong));

        OptionListAdapter optionListAdapter = new OptionListAdapter();
        viewHolder.listView.setAdapter(optionListAdapter);

        //set user answer and spend time etc invisible for Note and Favorite questions
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

    private void initAnalysisView(ViewGroup view) {
        AnalysisViewHolder viewHolder = new AnalysisViewHolder();
        viewHolder.contentTV = (TextView) view.findViewById(R.id.textView_question_content);
        viewHolder.contentSonTV = (TextView) view.findViewById(R.id.textView_question_content_son);
        viewHolder.listView = (ScrollViewWithListView) view.findViewById(R.id.listView_question_option);
        viewHolder.analysisTV = (TextView) view.findViewById(R.id.textView_analysis);
        viewHolder.analysisSonTV = (TextView) view.findViewById(R.id.textView_analysis_son);
        viewHolder.rightAnswerTV = (TextView)view.findViewById(R.id.textView_right_answer);

        viewHolder.userSelectedTV = (TextView)view.findViewById(R.id.textView_user_selected);
        viewHolder.isRespondRightTV = (TextView)view.findViewById(R.id.textView_is_respond_right);
        viewHolder.questionSpendTimeTV = (TextView)view.findViewById(R.id.textView_question_spend_time);

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

        if(question._isRight) viewHolder.isRespondRightTV.setText(getResources().getString(R.string.respond_right));
        else viewHolder.isRespondRightTV.setText(getResources().getString(R.string.respond_wrong));

        OptionListAdapter optionListAdapter = new OptionListAdapter();
        viewHolder.listView.setAdapter(optionListAdapter);

    }

    private void initQuestionView(ViewGroup rootView) {
        QuestionViewHolder viewHolder = new QuestionViewHolder();
        viewHolder.contentTV = (TextView)rootView.findViewById(R.id.textView_question_content);
        String strContent = question._strSubject;
        if(strContent != null && strContent.length() > 0) {
            viewHolder.contentTV.setText(strContent);
        }else{
            viewHolder.contentTV.setVisibility(View.GONE);
        }
        viewHolder.contentSonTV = (TextView) rootView.findViewById(R.id.textView_question_content_son);
        String strContentSon = question._strSubjectSon;
        if(strContentSon != null && strContentSon.length() > 0) {
            viewHolder.contentSonTV.setText(strContentSon);
        }else{
            viewHolder.contentSonTV.setVisibility(View.GONE);
        }
        viewHolder.listView = (ListView) rootView.findViewById(R.id.listView_question_option);
        OptionListAdapter optionListAdapter = new OptionListAdapter();
        viewHolder.listView.setAdapter(optionListAdapter);
        viewHolder.listView.setOnItemClickListener(new ListViewItemClick(optionListAdapter));
        rootView.setTag(viewHolder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_submit:
                long lEndTime = System.currentTimeMillis();
                paper._lEndTime = lEndTime;
                Intent intent = new Intent();
                intent.setClass(this.getActivity(), ActivityPaperReport.class);
                intent.putExtra("PAPER", paper);
                startActivity(intent);
                Intent stopActivityPaperIntent = new Intent(ActivityPaperG.FINISH_ACTIVITY);
                stopActivityPaperIntent.putExtra("FINISH",true);
                getActivity().sendBroadcast(stopActivityPaperIntent);
                break;
        }
    }


    class ListViewItemClick implements AdapterView.OnItemClickListener {

        OptionListAdapter _myAdapter;
        public ListViewItemClick(OptionListAdapter myAdapter){
            _myAdapter = myAdapter;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<Integer, Boolean> hashMap = question._userSelectedMap;

            switch (question._iType){
                case Question.SingleSelect:
                    for(int i=0; i<question._options.size(); i++){
                        hashMap.put(i, false);
                    }
                    hashMap.put(position, true);
                    break;
                case Question.MultiSelect:
                    if(hashMap.get(position)) hashMap.put(position, false);
                    else hashMap.put(position, true);
                    break;
            }
            question._iUserHasDone = 1;
            question._userSelectedMap = hashMap;
            _myAdapter.notifyDataSetChanged();
//            if(question._iType == Question.SingleSelect) {
//                Intent intent = new Intent("com.example.ustc_pc.myapplication.questions.next");
//                _context.sendBroadcast(intent);
//            }
        }
    }

    class OptionListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return question._options.size();
        }

        @Override
        public Object getItem(int position) {
            return question._options.get(position);
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
            optionViewHolder.textView.setText((String)getItem(position));
            if(question._userSelectedMap.get(position)){
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_click));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }else{
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
                optionViewHolder.checkedTextView.setTextColor(-16777216);//black
            }

            return convertView;
        }
    }

    class AnswerListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return paper._questions.size();
        }

        @Override
        public Object getItem(int position) {
            return paper._questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = View.inflate(parent.getContext(), R.layout.layout_textview, null);
            TextView textView= (TextView)convertView.findViewById(R.id.textView_layout);
            Question question = paper._questions.get(position);
            int index = position + 1;
            textView.setText(""+index);
            if(question._iUserHasDone == 0){
                textView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_option_unclick));
            }else {
                textView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_option_click));
                textView.setTextColor(-1);//white
            }

            return convertView;
        }
    }

    class QuestionViewHolder {
        TextView contentTV, contentSonTV;
        ListView listView;
    }

    class AnalysisViewHolder{
        TextView contentTV, contentSonTV, rightAnswerTV, yourSelectedIsTV, userSelectedTV,isRespondRightTV, zuoDaYongShiTV, questionSpendTimeTV,miaoTV,
                analysisTV, analysisSonTV;
        ScrollViewWithListView listView;
    }

    class OptionViewHolder {
        CheckedTextView checkedTextView;
        TextView textView;
    }
}
