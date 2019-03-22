package com.example.pojos;

public class PageViewsPojo{
    private Long viewtime;
    private String userid;
    private String pageid;

    @Override
    public String toString() {
        return "PageViewsPojo{" +
                "viewtime=" + viewtime +
                ", userid='" + userid + '\'' +
                ", pageid='" + pageid + '\'' +
                '}';
    }

    public PageViewsPojo(Long viewtime, String userid, String pageid) {
        this.viewtime = viewtime;
        this.userid = userid;
        this.pageid = pageid;
    }

    public Long getViewtime() {

        return viewtime;
    }

    public void setViewtime(Long viewtime) {
        this.viewtime = viewtime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }
}
