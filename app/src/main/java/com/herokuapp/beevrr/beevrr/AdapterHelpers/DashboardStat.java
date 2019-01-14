/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.AdapterHelpers;

public class DashboardStat {
    private String header;
    private String count;

    public DashboardStat(String header, String count) {
        this.header = header;
        this.count = count;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
