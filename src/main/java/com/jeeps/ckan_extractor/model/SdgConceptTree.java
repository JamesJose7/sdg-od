package com.jeeps.ckan_extractor.model;

import java.util.List;
import java.util.Map;

public class SdgConceptTree {
    private int goal;
    private List<String> goalConcepts;
    private Map<String, List<String>> targetsConcepts;
    private Map<String, List<String>> indicatorsConcepts;

    public SdgConceptTree() {}

    public SdgConceptTree(int goal, List<String> goalConcepts, Map<String, List<String>> targetsConcepts,
                          Map<String, List<String>> indicatorsConcepts) {
        this.goal = goal;
        this.goalConcepts = goalConcepts;
        this.targetsConcepts = targetsConcepts;
        this.indicatorsConcepts = indicatorsConcepts;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public List<String> getGoalConcepts() {
        return goalConcepts;
    }

    public void setGoalConcepts(List<String> goalConcepts) {
        this.goalConcepts = goalConcepts;
    }

    public Map<String, List<String>> getTargetsConcepts() {
        return targetsConcepts;
    }

    public void setTargetsConcepts(Map<String, List<String>> targetsConcepts) {
        this.targetsConcepts = targetsConcepts;
    }

    public Map<String, List<String>> getIndicatorsConcepts() {
        return indicatorsConcepts;
    }

    public void setIndicatorsConcepts(Map<String, List<String>> indicatorsConcepts) {
        this.indicatorsConcepts = indicatorsConcepts;
    }
}
