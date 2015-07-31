package com.example.ustc_pc.myapplication.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.db.KPsDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.net.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "iCourseID";

    // TODO: Rename and change types of parameters
    private int mICourseID;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param iCourseID Parameter 1.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(int iCourseID) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, iCourseID);
        fragment.setArguments(args);
        return fragment;
    }

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mICourseID = getArguments().getInt(ARG_PARAM1);
        }
    }

    private RecyclerView mRecyclerView;

    private List<KPs> mAllKPses;
    private List<KPs> mShowingKPs;

    KPsAdapter mKPsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_course, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_kps);
        initKPs();

        return view;
    }

    private void initKPs() {
        mAllKPses = new ArrayList<>();
        mShowingKPs = new ArrayList<>();
        GetCourseKPsAsyncTask getCourseKPsAsyncTask = new GetCourseKPsAsyncTask(getActivity());
        getCourseKPsAsyncTask.execute(mICourseID);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mKPsAdapter = new KPsAdapter());
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

    /**
     * At first , it gets course's kps from db.If no anyone ,then get from server
     */
    class GetCourseKPsAsyncTask extends AsyncTask<Integer, Integer, List<KPs>>{

        private Context context;
        private ProgressDialog progressDialog;
        public GetCourseKPsAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, getString(R.string.loading),null);
        }
        @Override
        protected List<KPs> doInBackground(Integer... integers) {
            KPsDBHelper kPsDBHelper = KPsDBHelper.getInstance(context);
            int iCourseID = integers[0];
            List<KPs> kPses = kPsDBHelper.getCourseKPs(iCourseID);
            if(kPses == null ||kPses.isEmpty()){//get KPs from Server
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                try {
                    kPses = okHttpUtil.getKPs(new UserSharedPreference(context).getiUserID(),iCourseID);
                } catch (IOException e) {
                    Log.e("Error: ", "GetCourseKPsAsyncTask "+ e.toString());
                }
            }
            return kPses;
        }

        @Override
        protected void onPostExecute(List<KPs> result){
            progressDialog.dismiss();
            if(result != null && !result.isEmpty()) mAllKPses = result;
            mShowingKPs = add1LevelKPs(mAllKPses);
            mKPsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * add 1 level kps to showing adapter data list
     * @param allKPses
     * @return
     */
    public List<KPs> add1LevelKPs(List<KPs> allKPses){
        List<KPs> showingKPses = new ArrayList<>();
        if(allKPses == null || allKPses.isEmpty())return showingKPses;
        if(showingKPses == null )showingKPses = new ArrayList<>();

        for(KPs kPs : allKPses){
            if(kPs.getILevel() == Util.FIRST_LEVEL_KP){
                showingKPses.add(kPs);
                allKPses.remove(kPs);
            }
        }
        return showingKPses;
    }

    /**
     *
     * @param showingKPs  : Showing kps
     * @param position : click position
     * @param allKPs : for save all kps, but for save storage space ,we will remove kps which has been added to showing kps
     */
    public void addNextLevelKPs(List<KPs> showingKPs, int position, List<KPs> allKPs){
        if(allKPs == null || allKPs.isEmpty())return;
        KPs currentKP = showingKPs.get(position);
        currentKP.setIsExpand(true);
        //No child
        if(!currentKP.getHasChild())return;
        int currentLevel = currentKP.getILevel();
        int nextLevel = currentLevel + 1;
        //Do not has next level
        if( nextLevel > Util.LAST_LEVEL_KP )return;

        String currentKPID = currentKP.getStrKPID();
        int currentPostion = position;
        for(KPs kPs : allKPs){
            if(kPs.getILevel() == nextLevel && kPs.getStrFatherKPID().equals(currentKPID)){
                currentPostion = currentPostion + 1;
                showingKPs.add(currentPostion, kPs);
                mKPsAdapter.notifyItemChanged(currentPostion);
                allKPs.remove(kPs);
            }
        }
    }

    /**
     * recursion remove all son kps
     * @param showingKPs
     * @param position
     * @param allKPs
     */
    public void removeNextLevelKPs(List<KPs> showingKPs, int position, List<KPs> allKPs){
        KPs currentKP = showingKPs.get(position);
        currentKP.setIsExpand(false);
        //No child
        if(!currentKP.getHasChild())return;

        //Do not has next level
        int currentLevel = currentKP.getILevel();
        int nextLevel = currentLevel + 1;
        if( nextLevel > Util.LAST_LEVEL_KP)return;
        String strCurrentKPID = currentKP.getStrKPID();
        int size = showingKPs.size();
        for(int i = 0; i< size; i++){
            KPs kps = showingKPs.get(i);
            if(kps.getILevel() == nextLevel && kps.getStrFatherKPID().equals(strCurrentKPID)){
                removeNextLevelKPs(showingKPs, i, allKPs);
                allKPs.add(kps);
                showingKPs.remove(i);
                mKPsAdapter.notifyItemChanged(i);
            }
        }
    }

    private static int TYPE_HEADER = 1;
    private static int TYPE_ITEM = 2;

    class KPsAdapter extends RecyclerView.Adapter<KPsAdapter.KPsViewHolder>{

        @Override
        public KPsAdapter.KPsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            KPsViewHolder holder;
            if(viewType == TYPE_HEADER){
                holder = new KPsViewHolder(View.inflate(getActivity(),R.layout.layout_kps_header, null),viewType);
            }else{
                holder = new KPsViewHolder(View.inflate(getActivity(),R.layout.layout_kps_item, null),viewType);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(KPsAdapter.KPsViewHolder holder, int position) {

            if (getItemViewType(position) == TYPE_HEADER) {
                // TODO : Set score
                holder.contentTV.setText("0");
            } else {
                holder.contentTV.setText(mShowingKPs.get(position).getStrName());
                if(mShowingKPs.get(position).getIsExpand()){
                    holder.itemIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_24dp));
                }else{
                    holder.itemIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                }
                holder.itemIV.setOnClickListener(new OnItemIVClickListener(position));
            }
        }
        @Override
        public int getItemViewType(int position){
            if(position == 0)return TYPE_HEADER;
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return mAllKPses.size();
        }

        private class OnItemIVClickListener implements View.OnClickListener{
            private int position ;
            public OnItemIVClickListener(int position){
                this.position = position;
            }
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.imageView_kps_item:
                        if(mShowingKPs.get(position).getIsExpand())removeNextLevelKPs(mShowingKPs, position, mAllKPses);
                        else addNextLevelKPs(mShowingKPs, position, mAllKPses);
                }
            }
        }

        public class KPsViewHolder extends RecyclerView.ViewHolder {

            //Item
            private ImageView itemIV;
            //header score TextView's id same as item kp's name TextView
            private TextView contentTV;

            public KPsViewHolder(View itemView, int iType) {
                super(itemView);
                contentTV = (TextView) itemView.findViewById(R.id.textView_kps_content);
                if(iType == TYPE_ITEM){
                    itemIV = (ImageView)itemView.findViewById(R.id.imageView_kps_item);
                }
            }
        }
    }
}
