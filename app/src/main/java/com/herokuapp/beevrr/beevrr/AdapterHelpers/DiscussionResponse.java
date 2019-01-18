/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.AdapterHelpers;

public class DiscussionResponse {
    private int id;

    private String response;
    private String userName;
    private String opinion;
    private String time;

    private int proposition;
    private int userID;
    private int score;

    public DiscussionResponse(int id, String response, String userName, String opinion,
                              String time, int proposition, int userID, int score) {
        this.id = id;
        this.response = response;
        this.userName = userName;
        this.opinion = opinion;
        this.time = time;
        this.proposition = proposition;
        this.userID = userID;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getProposition() {
        return proposition;
    }

    public void setProposition(int proposition) {
        this.proposition = proposition;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
