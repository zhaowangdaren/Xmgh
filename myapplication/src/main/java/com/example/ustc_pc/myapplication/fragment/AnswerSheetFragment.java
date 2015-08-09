package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.QuestionNew;
import com.example.ustc_pc.myapplication.unit.ScrollViewWithGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnswerSheetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnswerSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerSheetFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_TEST_ID = "ARG_TEST_ID";

    // TODO: Rename and change types of parameters
    private List<QuestionNew> mQuestions;
    private long mlTestID;

    private ScrollViewWithGridView mGridView;
    private Button mSubmitBT;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questions Parameter 1.
     * @return A new instance of fragment AnswerSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnswerSheetFragment newInstance(List<QuestionNew> questions, long lTestID) {
        AnswerSheetFragment fragment = new AnswerSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTIONS, (Serializable) questions);
        args.putLong(ARG_TEST_ID, lTestID);
        fragment.setArguments(args);
        return fragment;
    }

    public AnswerSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestions = (List<QuestionNew>)getArguments().getSerializable(ARG_QUESTIONS);
            mlTestID = getArguments().getLong(ARG_TEST_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer_sheet, container, false);
        mGridView = (ScrollViewWithGridView) view.findViewById(R.id.gridView_answer_sheets);
        mSubmitBT = (Button)view.findViewById(R.id.button_submit_answer);
        mSubmitBT.setOnClickListener(this);
        if(mQuestions == null)mQuestions = new ArrayList<>();

        AnswerSheetAdapter answerSheetAdapter = new AnswerSheetAdapter();
        mGridView.setAdapter(answerSheetAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_submit_answer:
                onSubmitButtonPressed(null);
                break;
        }
    }

    class AnswerSheetAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mQuestions.size();
        }

        @Override
        public QuestionNew getItem(int i) {
            return mQuestions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view =  View.inflate(viewGroup.getContext(), R.layout.layout_answer_sheets_item, null);
            int index = position + 1;
            ((TextView)view).setText(""+index);
            if(hasDone(position)){
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_unclick));
            }else{
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_option_click));
                ((TextView)view).setTextColor(-1);//white
            }
            return view;
        }

        public boolean hasDone(int position){
            List<QuestionNew.QuestionSon.QuestionOption> options =
                    mQuestions.get(position).getQuestionSons().get(0).getOptions();
            for(int i = 0; i<options.size(); i++){
                if(options.get(i).isSelected)return true;
            }
            return false;
        }

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onSubmitButtonPressed(Uri uri) {
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
