package com.example.ustc_pc.myapplication.adapter;

import android.view.View;
import android.widget.AdapterView;

import com.example.ustc_pc.myapplication.unit.KnowledgePoint;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/24.
 */
public class TreeViewItemClickListener implements AdapterView.OnItemClickListener {
    private TreeViewAdapter treeViewAdapter;
    public TreeViewItemClickListener(TreeViewAdapter treeViewAdapter){
        this.treeViewAdapter = treeViewAdapter;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击的item代表的元素
        KnowledgePoint knowledgePoint = (KnowledgePoint)treeViewAdapter.getItem(position);
        //树中元素
        ArrayList<KnowledgePoint> allData = treeViewAdapter.getAllData();
        //根元素
        ArrayList<KnowledgePoint> groupDatas = treeViewAdapter.getGroupData();

        //点击没有子项的item，则直接返回
        if( !knowledgePoint.hasChildren){
            return;
        }

        if(knowledgePoint.isExpanded){
            knowledgePoint.isExpanded = false;
            ArrayList<KnowledgePoint> knowledgePointsToDel = new ArrayList<>();
            for(int i = position + 1; i<groupDatas.size(); i++){
                if(knowledgePoint._level >= groupDatas.get(i)._level)
                    break;
                knowledgePointsToDel.add(groupDatas.get(i));
            }
            groupDatas.removeAll(knowledgePointsToDel);
            treeViewAdapter.notifyDataSetChanged();
        }else{
            knowledgePoint.isExpanded = true;
            //从数据源中提取子节点数据添加进树，这里只是添加了下一级子节点
            int i = 1;//计数器放在for外面才能保证计数有效
            for(KnowledgePoint e: allData){
                if(e._parentId.equals(knowledgePoint._id)){
                    e.isExpanded = false;
                    groupDatas.add(position + i, e);
                    i++;
                }
            }
            treeViewAdapter.notifyDataSetChanged();
        }
    }
}
