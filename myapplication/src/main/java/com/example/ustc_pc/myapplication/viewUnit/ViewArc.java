package com.example.ustc_pc.myapplication.viewUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;

import java.util.ArrayList;

/**
 * Draw sector 画扇形
 * Created by ustc-pc on 2015/3/9.
 */
public class ViewArc extends View{

    private int _arg = 0;//做对的角度
    /**
     * 0-papers finished percent
     * 1-paper error percent
     * 2-view assessment
     */
    private int _type = 0;

    private ArrayList<AssessmentScore.AssessmentScoreKp> _datas;

    public ViewArc(Context context) {
        super(context);
    }

    public ViewArc(Context context, int arg, int type){
        super(context);
        _arg = arg;
        _type = type;
    }

    /**
     *
     * @param context
     * @param datas
     * @param arg
     * @param type 0-papers finished percent; 1-paper error percent; 2-view assessment
     */
    public ViewArc(Context context, ArrayList<AssessmentScore.AssessmentScoreKp> datas ,int arg, int type){
        super(context);
        _datas = datas;
        _arg = arg;
        _type = type;
    }
    @Override
    protected void onDraw(Canvas canvas){
        //Draw error area
        switch (_type){
            case 0:
               drawPapersFinishedPercent(canvas);
                break;
            case 1:
                drawPaperErrorPercent(canvas);
                break;
            case 2:
                drawAssessmentDetail(canvas);
                break;
        }
    }

    private void drawPapersFinishedPercent(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.holo_blue_light));

        RectF rectF = new RectF(0, 0, getResources().getDimension(R.dimen.paper_percent_size), getResources().getDimension(R.dimen.paper_percent_size));
        canvas.drawArc(rectF, 270, _arg, true, paint);
    }

    private void drawPaperErrorPercent(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //right percent
        paint.setColor(getResources().getColor(R.color.holo_blue_light));
        RectF rectF = new RectF(0, 0, getResources().getDimension(R.dimen.paper_percent_size), getResources().getDimension(R.dimen.paper_percent_size));
        canvas.drawArc(rectF, 270, _arg, true, paint);

        //Error percent
        paint.setColor(getResources().getColor(R.color.tomato));
        //            paint.setColor(1352256512);
        canvas.drawArc(rectF, 270 + _arg, 360 - _arg, true, paint);
    }

    private void drawAssessmentDetail(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //RectF rectF = new RectF(0,0,getResources().getDimension(R.dimen.score_percent_diameter),getResources().getDimension(R.dimen.score_percent_diameter));
        RectF rectF;
        int size = _datas.size();
        int averageArg = _arg / size;
        int startArg = 270;

        float x = getResources().getDimension(R.dimen.score_percent_diameter) ;
        float y = getResources().getDimension(R.dimen.score_percent_diameter) ;
        float basic = getResources().getDimension(R.dimen.score_percent_radius) ;
        for(int i = 0; i<size; i++){
            //TODO set different color for those kp
//            paint.setColor(_datas.get(i)._iColor);

            float x1 = (x-basic)  * ((float)_datas.get(i).getiProgress())  + basic ;
            float y1 = (y-basic) * (float)_datas.get(i).getiProgress()  + basic ;
            rectF = new RectF((x - x1 ) /2, (y - y1 ) / 2, (x + x1 ) / 2, (y + y1 ) / 2);
            canvas.drawArc(rectF, startArg, averageArg, true, paint);
            startArg += averageArg;
        }
    }



}
