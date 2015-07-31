package com.example.ustc_pc.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.viewUnit.CircularProgressDrawable;
import com.example.ustc_pc.myapplication.viewUnit.ViewArc;
import com.example.ustc_pc.myapplication.unit.Paper;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/27.
 */
public class ActivityPapersAdapter extends BaseAdapter{
    private ArrayList<Paper> mPapers;
    private Context mContext;


    public ActivityPapersAdapter(Context context, ArrayList<Paper> papers){
        mContext = context;
        mPapers = papers;


    }

    @Override
    public int getCount() {
        return mPapers.size();
    }

    @Override
    public Object getItem(int position) {
        return mPapers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PapersViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.activity_papers_item, null);
            viewHolder = new PapersViewHolder();
            viewHolder._paperNameTV = (TextView)convertView.findViewById(R.id.textView_papers_item);
            viewHolder._paperPercentLL = (LinearLayout)convertView.findViewById(R.id.linearLayout_papers_item_progress);
            viewHolder.ivDrawable = (ImageView)convertView.findViewById(R.id.iv_drawable);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (PapersViewHolder)convertView.getTag();
        }
        viewHolder._paperNameTV.setText(mPapers.get(position)._strName);
        if(mPapers.get(position)._isDid) {
            convertView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_rounded_rectangle_white));

            int iArg = mPapers.get(position)._iScore * 360 / mPapers.get(position)._iNumQ;
            ViewArc viewArc = new ViewArc(mContext, iArg, 1);
            viewArc.invalidate();
            viewHolder._paperPercentLL.removeAllViews();
            viewHolder._paperPercentLL.addView(viewArc);
        }
        //Circular progress
        viewHolder.setIvDrawable();

        return convertView;
    }


    public class PapersViewHolder{
        public TextView _paperNameTV;
        public LinearLayout _paperPercentLL;
        public ImageView ivDrawable;

        private CircularProgressDrawable drawable;
        public PapersViewHolder(){
            drawable = new CircularProgressDrawable.Builder()
                    .setRingWidth(mContext.getResources().getDimensionPixelSize(R.dimen.drawable_ring_width))
                    .setOutlineColor(mContext.getResources().getColor(android.R.color.darker_gray))
                    .setRingColor(mContext.getResources().getColor(android.R.color.holo_green_light))
                    .create();
        }

        public void setIvDrawable(){
            ivDrawable.setImageDrawable(drawable);
        }

        public CircularProgressDrawable getDrawable(){
            return drawable;
        }
    }
}
