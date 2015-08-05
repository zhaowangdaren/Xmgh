package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.QuestionNew;

import java.util.List;

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
    public static final String ARG_KP_NAME = "kp";
    public static final String ARG_INDEX = "index";
    public static final String ARG_SUM = "sum";
    // TODO: Rename and change types of parameters
    private QuestionNew mQuestionNew;
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
    public static BaseTestFragment newInstance(QuestionNew param1, String strKPName, int index, int sum) {
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
            mQuestionNew = (QuestionNew) arg.getSerializable(ARG_QUESTION);
            mStrKPName = arg.getString(ARG_KP_NAME);
            mIndex = getArguments().getInt(ARG_INDEX, 0);
            mSumQuestion = getArguments().getInt(ARG_SUM, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_layout_question);
        QuestionRecyclerViewAdapter adapter = new QuestionRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
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
                    holder.questionSumTV.setText(""+mSumQuestion);
                    break;
                case Util.TYPE_QUESTION_LAYOUT_FATHER:
                    holder.questionFatherTV.setText(mQuestionNew.getStrSubject());
                    break;
                case Util.TYPE_QUESTION_LAYOUT_OPTION:
                    OptionListAdapter optionListAdapter = new OptionListAdapter(mQuestionNew.getQuestionSons().get(0).getOptions());
                    holder.optionListView.setAdapter(optionListAdapter);
                    holder.optionListView.setOnItemClickListener(new ListViewItemClick(optionListAdapter));
                    break;
            }
        }

        /**
         * 0:layout_question_header, 1:layout_question_father, 2:layout_question_son
         * @return
         */
        @Override
        public int getItemCount() {
            return 3;
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
                }
            }
        }
    }

    class OptionListAdapter extends BaseAdapter{
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

    class ListViewItemClick implements AdapterView.OnItemClickListener {

        OptionListAdapter optionsAdapter;
        public ListViewItemClick(OptionListAdapter myAdapter){
            optionsAdapter = myAdapter;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //single select
            if( !mQuestionNew.getQuestionSons().get(0).getIsMultiSelect() ){
                int optionSize = optionsAdapter.getCount();
                for(int i = 0; i< optionSize ; i++){
                    if(optionsAdapter.getItem(i).isSelected)optionsAdapter.getItem(i).isSelected = false;
                }
            }

            if(optionsAdapter.getItem(position).isSelected) optionsAdapter.getItem(position).isSelected = false;
            else optionsAdapter.getItem(position).isSelected = true;
            optionsAdapter.notifyDataSetChanged();
        }
    }
}
