package com.example.ustc_pc.myapplication.unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc-pc on 2015/3/26.
 */
public class AssessmentScore {
    private Integer iSumSocre;
    private List<AssessmentScoreKp> assessmentScoreKps;

    public AssessmentScore(){
        assessmentScoreKps = new ArrayList<>();
    }

    public AssessmentScore(Integer iSumSocre, List<AssessmentScoreKp> AssessmentScoreKps) {
        this.iSumSocre = iSumSocre;
        this.assessmentScoreKps = AssessmentScoreKps;
    }

    public void addkp(AssessmentScoreKp kp){
        this.assessmentScoreKps.add(kp);
    }
    public Integer getiSumSocre() {
        return iSumSocre;
    }

    public void setiSumSocre(Integer iSumSocre) {
        this.iSumSocre = iSumSocre;
    }

    public List<AssessmentScoreKp> getAssessmentScoreKps() {
        return assessmentScoreKps;
    }

    public void setAssessmentScoreKps(List<AssessmentScoreKp> assessmentScoreKps) {
        this.assessmentScoreKps = assessmentScoreKps;
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
