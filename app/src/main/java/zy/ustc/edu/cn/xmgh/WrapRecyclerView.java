package zy.ustc.edu.cn.xmgh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/7/30.
 */
public class WrapRecyclerView extends RecyclerView {
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public void addHeaderView(View view){
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if(mAdapter != null){
            if(!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    public void addFootView(View view){
        mFootViews.clear();
        mFootViews.add(view);
        if(mAdapter != null){
            if(!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter){
        if(mHeaderViews.isEmpty() && mFootViews.isEmpty()){
            super.setAdapter(adapter);
        }else{
            adapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }
}
