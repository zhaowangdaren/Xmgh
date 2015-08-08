package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.db.DoneQuestionDBHelper;
import com.example.ustc_pc.myapplication.db.KPsDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseErrorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseErrorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mCourseID;

    private OnFragmentInteractionListener mListener;

    private ListView mLV;
    private List<KPs> mKPses;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CourseErrorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseErrorFragment newInstance(int param1) {
        CourseErrorFragment fragment = new CourseErrorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public CourseErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseID = getArguments().getInt(ARG_PARAM1);
            mKPses = new ArrayList<>();
        }
    }

    private LVAdapter mLvAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_error, container, false);
        mLV = (ListView) view.findViewById(R.id.listView_error_fragment);
        mLvAdapter = new LVAdapter();
        mLV.setAdapter(mLvAdapter);

        GetErrorQueAsync getErrorQueAsync = new GetErrorQueAsync(getActivity());
        getErrorQueAsync.execute(mCourseID);
        return view;
    }

    private class LVAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mKPses.size();
        }

        @Override
        public Object getItem(int i) {
            return mKPses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(parent.getContext(), R.layout.layout_listview_check_item, null);
            TextView textView = (TextView)convertView.findViewById(R.id.textView_labale_name_user_book);
            textView.setText(mKPses.get(position).getStrName());
            return convertView;
        }
    }
    private class GetErrorQueAsync extends AsyncTask<Integer, Integer, List<KPs>>{

        private Context context;
        private ProgressDialog progressDialog;
        public GetErrorQueAsync(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, getString(R.string.loading),null);
        }
        @Override
        protected List<KPs> doInBackground(Integer... integers) {
            int iCourseID = integers[0];
            DoneQuestionDBHelper doneQuestionDBHelper = DoneQuestionDBHelper.getInstance(context);
            List<String> strKPIDs = doneQuestionDBHelper.queryErrorKPs(iCourseID);
            KPsDBHelper kPsDBHelper = KPsDBHelper.getInstance(context);

            List<KPs> result = kPsDBHelper.queryKPsByKPID(strKPIDs);
            return result;
        }

        @Override
        protected void onPostExecute(List<KPs> result){
            if(result != null && !result.isEmpty()){
                mKPses.addAll(result);
                mLvAdapter.notifyDataSetChanged();
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
