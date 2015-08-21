package com.example.ustc_pc.myapplication.unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc-pc on 2015/3/26.
 */
public class AssessmentScore {
    private Integer iSumScore;
    private List<AssessmentScoreKp> kps;

    public AssessmentScore(){
        kps = new ArrayList<>();
    }

    public AssessmentScore(Integer iSumScore, List<AssessmentScoreKp> kps) {
        this.iSumScore = iSumScore;
        this.kps = kps;
    }

    public void addkp(AssessmentScoreKp kp){
        this.kps.add(kp);
    }
    public Integer getiSumScore() {
        return iSumScore;
    }

    public void setiSumScore(Integer iSumScore) {
        this.iSumScore = iSumScore;
    }

    public List<AssessmentScoreKp> getKps() {
        return kps;
    }

    public void setKps(List<AssessmentScoreKp> kps) {
        this.kps = kps;
    }

    public class AssessmentScoreKp {
        private String strKPName;
        private String strKPID;
        private Integer iProgress;

        public AssessmentScoreKp(String strKPName, String strKPID, Integer iProgress) {
            this.strKPName = strKPName;
            this.strKPID = strKPID;
            this.iProgress = iProgress;
        }

        public String getStrKPID() {
            return strKPID;
        }

        public void setStrKPID(String strKPID) {
            this.strKPID = strKPID;
        }

        public Integer getiProgress() {
            return iProgress;
        }

        public void setiProgress(Integer iProgress) {
            this.iProgress = iProgress;
        }

        public String getStrKPName() {
            return strKPName;
        }

        public void setStrKPName(String strKPName) {
            this.strKPName = strKPName;
        }
    }
}
