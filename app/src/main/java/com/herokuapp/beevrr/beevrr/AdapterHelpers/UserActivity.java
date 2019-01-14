/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.AdapterHelpers;

public class UserActivity {
    private String action;
    private String opinion;
    private String proposition;
    private String date;

    public UserActivity(String action, String opinion, String proposition, String date) {
        this.action = action;
        this.opinion = opinion;
        this.proposition = proposition;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getProposition() {
        return proposition;
    }

    public void setProposition(String proposition) {
        this.proposition = proposition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
