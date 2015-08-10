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
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseTestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_QUESTION = "question";
    public static final String ARG_KP_NAME = "AssessmentScoreKp";
    public static final String ARG_INDEX = "index";
    public static final String ARG_SUM = "sum";
    // TODO: Rename and change types of parameters
    private QuestionUnmultiSon mQuestion;
    private int mIndex;
    private int mSumQuestion;
    private String mStrKPName;
    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BaseTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseTestFragment newInstance(QuestionUnmultiSon param1, String strKPName, int index, int sum) {
        BaseTestFragment fragment = new BaseTestFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, param1);
        args.putSerializable(ARG_KP_NAME, strKPName);
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_SUM, sum);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arg = getArguments();
            mQuestion = (QuestionUnmultiSon) arg.getSerializable(ARG_QUESTION);
            mStrKPName = arg.getString(ARG_KP_NAME);
            mIndex = getArguments().getInt(ARG_INDEX, 0);
            mSumQuestion = getArguments().getInt(ARG_SUM, 0);
        }
    }

    QuestionRecyclerViewAdapter mRecyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_layout_question);
        mRecyclerViewAdapter = new QuestionRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        return view;
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

    class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.QuestionRecyclerViewHolder> {

        private int currPosition;
        @Override
        public QuestionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            QuestionRecyclerViewHolder holder = null;
            View view;
            switch (viewType) {
                case Util.TYPE_QUESTION_LAYOUT_HEADER:
                    view = View.inflate(getActivity(), R.layout.layout_question_header, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType, 0);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    view = View.inflate(getActivity(), R.layout.layout_question_father, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType, 0);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
                    view = View.inflate(getActivity(), R.layout.layout_question_option_item, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType, currPosition);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(QuestionRecyclerViewHolder holder, int position) {
            //the index in the question list
            switch (getItemViewType(position)) {
                case Util.TYPE_QUESTION_LAYOUT_HEADER:
                    holder.questionTypeTV.setText(mStrKPName);
                    int index = mIndex + 1;
                    holder.questionIndexTV.setText("" + index);
                    holder.questionSumTV.setText("" + mSumQuestion);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    holder.questionFatherTV.setText(Html.fromHtml(mQuestion.getStrSubject()));
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
//                    OptionListAdapter optionListAdapter = new OptionListAdapter(mQuestion.getOptions());
//                    holder.optionListView.setAdapter(optionListAdapter);
//                    holder.optionListView.setOnItemClickListener(new ListViewItemClick(optionListAdapter));
//                    setListViewHeightBasedOnChildren(holder.optionListView);
                    QuestionUnmultiSon.QuestionOption questionOption = mQuestion.getOptions().get(position - 2);
                    holder.checkedTextView.setText(questionOption.getID());
                    holder.optionTV.setText(questionOption.getStrOption());
                    if(questionOption.isSelected()){
                        holder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_click));
                        holder.checkedTextView.setTextColor(getResources().getColor(R.color.white));//white
                    }else{
                        holder.checkedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
                        holder.checkedTextView.setTextColor(getResources().getColor(R.color.black));//black
                    }
                    break;
            }
        }

        private void setListViewHeightBasedOnChildren(ListView optionListView) {
            ListAdapter listAdapter = optionListView.getAdapter();
            if (listAdapter == null) return;

            int totalHeight = 0;
            int count = listAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View listItem = listAdapter.getView(i, null, optionListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = optionListView.getLayoutParams();
            params.height = totalHeight + (optionListView.getDividerHeight() * (listAdapter.getCount() - 1));
            optionListView.setLayoutParams(params);
        }

        /**
         * 0:layout_question_header, 1:layout_question_father, 2:layout_question_son
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mQuestion.getOptions().size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            currPosition = position;
            switch (position) {
                case 0:
                    return Util.TYPE_QUESTION_LAYOUT_HEADER;
                case 1:
                    return Util.TYPE_QUESTION_LAYOUT_FATHER;
//                case 2:
//                    return Util.TYPE_QUESTION_LAYOUT_OPTION;
                default:
                    return Util.TYPE_QUESTION_LAYOUT_OPTION;
            }
        }

        public class QuestionRecyclerViewHolder extends RecyclerView.ViewHolder {
            // layout question header
            TextView questionTypeTV, questionIndexTV, questionSumTV;

            //layout question father
            TextView questionFatherTV;

            //layout option
            CheckedTextView checkedTextView;
            TextView optionTV;

            public QuestionRecyclerViewHolder(View view, int iType, int position) {
                super(view);
                switch (iType) {
                    case Util.TYPE_QUESTION_LAYOUT_HEADER:
                        questionTypeTV = (TextView) view.findViewById(R.id.textView_layout_question_header_question_type);
                        questionIndexTV = (TextView) view.findViewById(R.id.textView_layout_question_header_index);
                        questionSumTV = (TextView) view.findViewById(R.id.textView_layout_question_header_sum);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_FATHER:
                        questionFatherTV = (TextView) view.findViewById(R.id.textView_layout_question_father);
                        break;
                    case Util.TYPE_QUESTION_LAYOUT_OPTION:
//                        optionListView = (ListView)view.findViewById(R.id.listView_question_option_list);
                        checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTV_option);
                        optionTV = (TextView) view.findViewById(R.id.textView_option_content);
                        view.setOnClickListener(new OptionClickListener(position - 2));
                        break;
                }
            }
        }
    }

    class OptionClickListener implements View.OnClickListener{

        private int index;
        public OptionClickListener(int index){
            this.index = index;
        }
        @Override
        public void onClick(View view) {

            //single select
            if( !(mQuestion.isMultiSelect()) ){
                int optionSize = mQuestion.getOptions().size();
                for(int i =0; i<optionSize; i++){
                    if(mQuestion.getOptions().get(i).isSelected())
                        mQuestion.getOptions().get(i).setIsSelected(false);
                }
            }

            if(mQuestion.getOptions().get(index).isSelected())mQuestion.getOptions().get(index).setIsSelected(false);
            else mQuestion.getOptions().get(index).setIsSelected(true);
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

}
