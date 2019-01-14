package com.herokuapp.beevrr.beevrr.AdapterHelpers;

public class Discussion {
    private String userName;
    private String proposition;
    private String argument;
    private String currentPhase;
    private String time;

    private int userID;
    private int discussionID;
    private int replyCount;
    private int voteCount;
    private int score;

    public Discussion(String userName, String proposition, String argument, String currentPhase,
                      String time, int userID, int discussionID, int replyCount, int voteCount,
                      int score) {
        this.userName = userName;
        this.proposition = proposition;
        this.argument = argument;
        this.currentPhase = currentPhase;
        this.time = time;
        this.userID = userID;
        this.discussionID = discussionID;
        this.replyCount = replyCount;
        this.voteCount = voteCount;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProposition() {
        return proposition;
    }

    public void setProposition(String proposition) {
        this.proposition = proposition;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(int discussionID) {
        this.discussionID = discussionID;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
