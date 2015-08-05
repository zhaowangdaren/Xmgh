package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.Answer;
import com.example.ustc_pc.myapplication.unit.QuestionNew;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BasicAnalysisFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BasicAnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasicAnalysisFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DONE_QUESTION = "ARG_DONE_QUESTION";
    private static final String ARG_QUESTION = "ARG_QUESTION";
    private static final String ARG_ANSWER = "ARG_ANSWER";
    private static final String ARG_INDEX = "ARG_INDEX";
    private static final String ARG_KP_NAME = "ARG_KP_NAME";
    private static final String ARG_SUM_QUESTIONS_NUM = "ARG_SUM_QUESTIONS_NUM";
    // TODO: Rename and change types of parameters
    private DoneQuestion mDoneQuestion;
    private QuestionNew mQuestionNew;
    private Answer mAnswer;

    private int mIndex;
    private int mQuestionsNum;
    private String mStrKPName;


    private RecyclerView mRecyclerView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mDoneQuestion Parameter 1.
     * @param mQuestionNew Parameter 2.
     * @param mAnswer Parameter 3.
     * @return A new instance of fragment BasicAnalysisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasicAnalysisFragment newInstance(DoneQuestion mDoneQuestion,
                                                    QuestionNew mQuestionNew,
                                                    Answer mAnswer,
                                                    int index,
                                                    String strKpName,
                                                    int questionsNum) {
        BasicAnalysisFragment fragment = new BasicAnalysisFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DONE_QUESTION, mDoneQuestion);
        args.putSerializable(ARG_QUESTION, mQuestionNew);
        args.putSerializable(ARG_ANSWER, mAnswer);
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_KP_NAME, strKpName);
        args.putInt(ARG_SUM_QUESTIONS_NUM, questionsNum);
        fragment.setArguments(args);
        return fragment;
    }

    public BasicAnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDoneQuestion = (DoneQuestion)getArguments().getSerializable(ARG_DONE_QUESTION);
            mQuestionNew = (QuestionNew)getArguments().getSerializable(ARG_QUESTION);
            mAnswer = (Answer)getArguments().getSerializable(ARG_ANSWER);

            mIndex = getArguments().getInt(ARG_INDEX,0);
            mQuestionsNum = getArguments().getInt(ARG_SUM_QUESTIONS_NUM, 0);
            mStrKPName = getArguments().getString(ARG_KP_NAME);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_analysis, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_layout_question);
        QuestionRecyclerViewAdapter adapter = new QuestionRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.QuestionRecyclerViewHolder>{


        @Override
        public QuestionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            QuestionRecyclerViewHolder holder = null;
            View view;
            switch (viewType){
                case Util.TYPE_QUESTION_LAYOUT_HEADER:
                    view = View.inflate(getActivity() ,R.layout.layout_question_header, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    view = View.inflate(getActivity(), R.layout.layout_question_father, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
                    view = View.inflate(getActivity(), R.layout.layout_question_son, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                    view = View.inflate(getActivity(),R.layout.layout_question_analysis,null);
                    holder = new QuestionRecyclerViewHolder(view,viewType);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(QuestionRecyclerViewHolder holder, int position) {
            //the index in the question list
            switch (getItemViewType(position)){
                case Util.TYPE_QUESTION_LAYOUT_HEADER:
                    holder.questionTypeTV.setText(mStrKPName);
                    holder.questionIndexTV.setText(""+mIndex + 1);
                    holder.questionSumTV.setText(""+ mQuestionsNum);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    holder.questionFatherTV.setText(mQuestionNew.getStrSubject());
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
                    OptionListAdapter optionListAdapter = new OptionListAdapter(mQuestionNew.getQuestionSons().get(0).getOptions());
                    holder.optionListView.setAdapter(optionListAdapter);
//                    holder.optionListView.setOnItemClickListener(new ListViewItemClick(optionListAdapter));
                    break;
                case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                    holder.analysisTV.setText( Html.fromHtml(mAnswer.getAnswerSons().get(0).getStrAnalysis()) );
                    break;
            }
        }

        /**
         * 0:layout_question_header, 1:layout_question_father, 2:layout_question_son, 3:layou_question_analysis
         * @return
         */
        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position){
            switch (position){
                case 0:
                    return Util.TYPE_QUESTION_LAYOUT_HEADER;
                case 1:
                    return Util.TYPE_QUESTION_LAYOUT_FATHER;
                case 2:
                    return Util.TYPE_QUESTION_LAYOUT_OPTION;
                case 3:
                    return Util.TYPE_QUESTION_LAYOUT_ANALYSIS;
                default: return Util.TYPE_QUESTION_LAYOUT_FATHER;
            }
        }

        public class QuestionRecyclerViewHolder extends RecyclerView.ViewHolder{
            // layout question header
            TextView questionTypeTV, questionIndexTV, questionSumTV;

            //layout question father
            TextView questionFatherTV;

            //layout option
            ListView optionListView;

            //layout question analysis
            TextView analysisTV;

            public QuestionRecyclerViewHolder(View view, int iType){
                super(view);
                switch (iType){
                    case Util.TYPE_QUESTION_LAYOUT_HEADER:
                        questionTypeTV = (TextView) view.findViewById(R.id.textView_layout_question_header_question_type);
                        questionIndexTV = (TextView)view.findViewById(R.id.textView_layout_question_header_index);
                        questionSumTV = (TextView)view.findViewById(R.id.textView_layout_question_header_sum);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_FATHER:
                        questionFatherTV = (TextView)view.findViewById(R.id.textView_layout_question_father);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_OPTION:
                        optionListView = (ListView)view.findViewById(R.id.listView_question_option_list);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                        analysisTV = (TextView)view.findViewById(R.id.textView_question_analysis);
                        break;
                }
            }
        }
    }

    class OptionListAdapter extends BaseAdapter {
        List<QuestionNew.QuestionSon.QuestionOption> options;
        public OptionListAdapter(List<QuestionNew.QuestionSon.QuestionOption> options){
            this.options = options;
        }
        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public QuestionNew.QuestionSon.QuestionOption getItem(int i) {
            return options.get(i);

        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            OptionViewHolder optionViewHolder;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.layout_question_option_item, null);
                optionViewHolder = new OptionViewHolder();
                optionViewHolder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTV_option);
                optionViewHolder.textView = (TextView) convertView.findViewById(R.id.textView_option_content);
                convertView.setTag(optionViewHolder);
            }else{
                optionViewHolder = (OptionViewHolder) convertView.getTag();
            }

//            switch(position){
//                case 0:
//                    optionViewHolder.checkedTextView.setText(""+41);
//                    break;
//                case 1:
//                    optionViewHolder.checkedTextView.setText("B");
//                    break;
//                case 2:
//                    optionViewHolder.checkedTextView.setText("C");
//                    break;
//                case 3:
//                    optionViewHolder.checkedTextView.setText("D");
//                    break;
//                default:optionViewHolder.checkedTextView.setText("E");
//            }
            optionViewHolder.checkedTextView.setText(""+41+position);

            optionViewHolder.textView.setText(getItem(position).strOption);
            if(options.get(position).isAnswer){
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_answer_right));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }else if(options.get(position).isSelected){
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_answer_error));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }else{
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
            }
            if(options.get(position).isSelected){
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_click));
                optionViewHolder.checkedTextView.setTextColor(-1);//white
            }else{
                optionViewHolder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
                optionViewHolder.checkedTextView.setTextColor(-16777216);//black
            }

            return convertView;
        }

        class OptionViewHolder {
            CheckedTextView checkedTextView;
            TextView textView;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
