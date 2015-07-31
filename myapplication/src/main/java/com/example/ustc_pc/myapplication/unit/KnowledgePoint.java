package com.example.ustc_pc.myapplication.unit;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/21.
 */
public class KnowledgePoint implements Serializable {

    public String _id = "";
    public String _parentId = "";//父id
    public int _level = 0;//在tree中层级
    public String _strTitle;//知识点名
    public boolean hasChildren = false;//是否有子元素
    public boolean isExpanded = false;//item是否展开
    public int _iQuestionSum = 0;//题目总数
    public int _iQuestionFinishNum = 0;//已完成题目数

    public ArrayList<Integer> _sonID;//子知识点id
    public ArrayList<Integer> _iQuestions;//知识点下所有问题的id

    public static final int NO_PARENT = -1;//该节点没有父节点，也就是level为0的节点
    public static final int TOP_LEVEL = 0;//该元素位于顶层
    public KnowledgePoint(String id, String title, int iQuestionSum){
        _id = id;
        _strTitle = title;
        _iQuestionSum = iQuestionSum;
    }

    public KnowledgePoint(String title, int level, String id,
                          String parentId,boolean hasChildren, boolean isExpanded){
        this._strTitle = title;
        this._level = level;
        this._id = id;
        _parentId = parentId;
        this.hasChildren = hasChildren;
        this.isExpanded = isExpanded;
    }

    public KnowledgePoint(String id, String title, int level, String parentId, boolean hasChild, boolean isExpand){
        _id = id;
        _strTitle = title;
        _level = level;
        _parentId = parentId;
        this.hasChildren = hasChild;
        this.isExpanded = isExpand;
    }

}
