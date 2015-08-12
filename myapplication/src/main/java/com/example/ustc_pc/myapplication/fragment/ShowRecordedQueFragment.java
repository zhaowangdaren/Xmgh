package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;
import com.example.ustc_pc.myapplication.unit.UnmultiSonAnalysis;
import com.example.ustc_pc.myapplication.unit.Util;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowRecordedQueFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowRecordedQueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowRecordedQueFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DONE_QUE = "ARG_DONE_QUE";
    private static final String ARG_QUESTION = "param2";
    private static final String ARG_ANALYSIS = "ARG_ANALYSIS";
    private static final String ARG_KP_NAME = "ARG_KP_NAME";
    private static final String ARG_INDEX = "ARG_INDEX";
    private static final String ARG_QUES_NUM = "ARG_QUES_NUM";

    // TODO: Rename and change types of parameters
    private DoneQuestion doneQuestion;
    private QuestionUnmultiSon questionUnmultiSon;
    private UnmultiSonAnalysis unmultiSonAnalysis;
    private String mStrKPName;
    private int mIndex, mQuestionsNum;
    private OnFragmentInteractionListener mListener;

    //View
    private RecyclerView mRV;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param doneQuestion Parameter 1.
     * @param questionUnmultiSon Parameter 2.
     * @param unmultiSonAnalysis Parameter 2.
     * @return A new instance of fragment ShowRecordedQueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowRecordedQueFragment newInstance(DoneQuestion doneQuestion
            , QuestionUnmultiSon questionUnmultiSon
            , UnmultiSonAnalysis unmultiSonAnalysis
            , String strKpName, int index, int questionsNum) {
        ShowRecordedQueFragment fragment = new ShowRecordedQueFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DONE_QUE, doneQuestion);
        args.putSerializable(ARG_QUESTION, questionUnmultiSon);
        args.putSerializable(ARG_ANALYSIS, unmultiSonAnalysis);
        args.putString(ARG_KP_NAME, strKpName);
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_QUES_NUM, questionsNum);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowRecordedQueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doneQuestion = (DoneQuestion) getArguments().getSerializable(ARG_DONE_QUE);
            questionUnmultiSon = (QuestionUnmultiSon) getArguments().getSerializable(ARG_QUESTION);
            unmultiSonAnalysis = (UnmultiSonAnalysis) getArguments().getSerializable(ARG_ANALYSIS);
            mStrKPName = getArguments().getString(ARG_KP_NAME);
            mIndex = getArguments().getInt(ARG_INDEX);
            mQuestionsNum = getArguments().getInt(ARG_QUES_NUM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_recorded_que, container, false);
        mRV = (RecyclerView) view.findViewById(R.id.recyclerView_layout_question);
        mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRV.setAdapter(new QuestionRecyclerViewAdapter());

        return view;
    }

    class QuestionRecyclerViewAdapter
            extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.QuestionRecyclerViewHolder>{


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
                    view = View.inflate(getActivity(), R.layout.layout_question_option_item, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                    view = View.inflate(getActivity(),R.layout.layout_question_analysis,null);
                    holder = new QuestionRecyclerViewHolder(view,viewType);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_NOTE:
                    view = View.inflate(getActivity(),R.layout.layout_question_note,null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
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
                    int index = mIndex + 1;
                    holder.questionIndexTV.setText(""+index);
                    holder.questionSumTV.setText(""+ mQuestionsNum);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    holder.questionFatherTV.setText(Html.fromHtml(questionUnmultiSon.getStrSubject()));
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
//                    OptionListAdapter optionListAdapter = new OptionListAdapter(mQuestion.getOptions());
//                    holder.optionListView.setAdapter(optionListAdapter);
//                    holder.optionListView.setOnItemClickListener(new ListViewItemClick(optionListAdapter));
                    QuestionUnmultiSon.QuestionOption questionOption = questionUnmultiSon.getOptions().get(position - 2);
                    holder.checkedTextView.setText(questionOption.getID());
                    holder.optionTV.setText(questionOption.getStrOption());
                    if(questionOption.isAnswer()){
                        holder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_answer_right));
                        holder.checkedTextView.setTextColor(getResources().getColor(R.color.white));//white
                        return;
                    }
                    //user select wrong
                    if( !(questionOption.isAnswer()) && questionOption.isSelected() ){
                        holder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_answer_error));
                        holder.checkedTextView.setTextColor(getResources().getColor(R.color.white));//white
                        return;
                    }

                    holder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
                    break;
                case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                    GetUserRightAnswer getUserRightAnswer = new GetUserRightAnswer(holder);
                    getUserRightAnswer.execute();
                    holder.yourAnswerTV.setText(doneQuestion.getStrUserAnswer());
                    holder.analysisTV.setText( Html.fromHtml(unmultiSonAnalysis.getStrAnalysis()) );
                    break;
                case Util.TYPE_QUESTION_LAYOUT_NOTE:
                    String strNote = doneQuestion.getStrNote();
                    if(strNote != null)
                        holder.noteTV.setText(strNote);
            }
        }
        private class GetUserRightAnswer extends AsyncTask<Integer, Integer, String> {

            private QuestionRecyclerViewHolder holder;
            public GetUserRightAnswer(QuestionRecyclerViewHolder holder){
                this.holder = holder;
            }

            @Override
            protected String doInBackground(Integer... integers) {
                List<String> answers = unmultiSonAnalysis.getAnswer();
                StringBuffer stringBuffer = new StringBuffer();
                for(String string : answers){
                    stringBuffer.append(string);
                }
                return stringBuffer.toString();
            }
            @Override
            protected void onPostExecute(String result){
                if(result == null || result.length() < 1)return;
                holder.rightAnswerTV.setText(result);
            }
        }

        /**
         * 0:layout_question_header, 1:layout_question_father, 2:layout_question_son, 3:layou_question_analysis
         * @return
         */
        @Override
        public int getItemCount() {
            return questionUnmultiSon.getOptions().size() + 4;
        }

        @Override
        public int getItemViewType(int position){
            if(position == 0)return Util.TYPE_QUESTION_LAYOUT_HEADER;

            if(position == 1)return Util.TYPE_QUESTION_LAYOUT_FATHER;

            if( position >= 2 && position <(questionUnmultiSon.getOptions().size() + 2) )
                return Util.TYPE_QUESTION_LAYOUT_OPTION;

            if(position >= (questionUnmultiSon.getOptions().size() + 2) && position < (getItemCount() - 1))
                return Util.TYPE_QUESTION_LAYOUT_ANALYSIS;
            if(position == (getItemCount() - 1))
                return Util.TYPE_QUESTION_LAYOUT_NOTE;
            return Util.TYPE_QUESTION_LAYOUT_ANALYSIS;
        }

        public class QuestionRecyclerViewHolder extends RecyclerView.ViewHolder{
            // layout question header
            TextView questionTypeTV, questionIndexTV, questionSumTV;

            //layout question father
            TextView questionFatherTV;

            //layout option
            CheckedTextView checkedTextView;
            TextView optionTV;

            //layout question analysis
            TextView yourAnswerTV, rightAnswerTV, analysisTV;

            //layout note
            TextView noteTV;

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
                        checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTV_option);
                        optionTV = (TextView) view.findViewById(R.id.textView_option_content);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_ANALYSIS:
                        yourAnswerTV = (TextView)view.findViewById(R.id.textView_your_answer);
                        rightAnswerTV = (TextView)view.findViewById(R.id.textView_right_answer);
                        analysisTV = (TextView)view.findViewById(R.id.textView_question_analysis);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_NOTE:
                        noteTV = (TextView)view.findViewById(R.id.textView_note_content);
                        break;
                }
            }
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
