package com.example.ustc_pc.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.activity.ActivityPapers;
import com.example.ustc_pc.myapplication.unit.KnowledgePoint;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/24.
 */
public class TreeViewAdapter extends BaseAdapter{
    private ArrayList<KnowledgePoint> mGroupData;
    private ArrayList<KnowledgePoint> mAllData;
    private Context mContext;

    private int indentionBase =10;//item行首缩进距离
    public TreeViewAdapter(ArrayList<KnowledgePoint> groupData, ArrayList<KnowledgePoint> childrenData, Context context){
        mGroupData = groupData;
        mAllData = childrenData;
        mContext = context;
    }

    public ArrayList<KnowledgePoint> getGroupData(){
        return mGroupData;
    }
    public ArrayList<KnowledgePoint> getAllData(){
        return mAllData;
    }
    @Override
    public int getCount() {
        return mGroupData.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.fragment_fragment3_tree_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.textView_knowledge_point);
            viewHolder.imageViewWrite = (ImageView)convertView.findViewById(R.id.imageView_write);
            viewHolder.imageViewHeader = (ImageView)convertView.findViewById(R.id.imageView_tree_open_close);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        KnowledgePoint knowledgePoint = mGroupData.get(position);
        int level = knowledgePoint._level;

        viewHolder.imageViewHeader.setPadding(indentionBase *(level + 1),
                viewHolder.imageViewHeader.getPaddingTop(),
                viewHolder.imageViewHeader.getPaddingRight(),
                viewHolder.imageViewHeader.getPaddingBottom());

        viewHolder.textView.setText(knowledgePoint._strTitle);

        if(knowledgePoint.hasChildren && !knowledgePoint.isExpanded){
            viewHolder.imageViewHeader.setImageResource(android.R.drawable.presence_online);
            viewHolder.imageViewHeader.setVisibility(View.VISIBLE);
        }else if(knowledgePoint.hasChildren && knowledgePoint.isExpanded){
            viewHolder.imageViewHeader.setImageResource(android.R.drawable.presence_busy);
            viewHolder.imageViewHeader.setVisibility(View.VISIBLE);
        }else if( !knowledgePoint.hasChildren){
            viewHolder.imageViewHeader.setImageResource(android.R.drawable.presence_online);
            viewHolder.imageViewHeader.setVisibility(View.INVISIBLE);
        }

        viewHolder.imageViewWrite.setOnClickListener(new WriteImageViewClickListener(knowledgePoint._id, knowledgePoint._strTitle));
        return convertView;
    }

    class WriteImageViewClickListener implements View.OnClickListener{

        private String _iKID = "-1";

        private String _strKid = "-1", _strFatherKP_id = "", _strKPName = "";
        public WriteImageViewClickListener(String iKnoweledgeID){
            _iKID = iKnoweledgeID;
        }

        public WriteImageViewClickListener(String knoweledgeID, String name){
            _iKID = knoweledgeID;
            _strKPName = name;
        }

        @Override
        public void onClick(View v) {
            if(_iKID.equals("-1")){
                Log.i("Error: ","WriteImageViewClickListener can not get the knoweledge point ID !!!!");
            }else{
                if (v.getId() == R.id.imageView_write){
                    Intent intent = new Intent();
                    intent.setClass(mContext, ActivityPapers.class);
                    intent.putExtra("ID", _iKID);
                    intent.putExtra("NAME", _strKPName);
                    mContext.startActivity(intent);
                }
            }

        }
    }

    class ViewHolder{
        TextView textView;
        ImageView imageViewHeader, imageViewWrite;
    }

}
