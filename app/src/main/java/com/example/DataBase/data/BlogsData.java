package com.example.DataBase.data;

public class BlogsData {
    int blogid;
    String blogtitle;
    String blogbody;
    int likes;
    String dateofpost;
    int agencyid;
    String agencytype;
    String link;

    public BlogsData(int blogid, String blogtitle, String blogbody, int likes, String dateofpost, int agencyid, String agencytype, String link) {
        this.blogid = blogid;
        this.blogtitle = blogtitle;
        this.blogbody = blogbody;
        this.likes = likes;
        this.dateofpost = dateofpost;
        this.agencyid = agencyid;
        this.agencytype = agencytype;
        this.link = link;
    }

    public void setLink(String l) {
        link = l;
    }

    public String getLink() {
        if(link != null && !link.isEmpty())
            return link;
        else return null;
    }

    public void setBlogtitle(String blogtitle) {
        this.blogtitle = blogtitle;
    }

    public void setBlogbody(String blogbody) {
        this.blogbody = blogbody;
    }

    public void setDateofpost(String dateofpost) {
        this.dateofpost = dateofpost;
    }

    public int getBlogid() {
        return blogid;
    }

    public String getBlogtitle() {
        return blogtitle;
    }

    public String getBlogbody() {
        return blogbody;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes= likes;
    }

    public String getDateofpost() {
        return dateofpost;
    }

    public int getAgencyid() {
        return agencyid;
    }

    public String getAgencytype() {
        return agencytype;
    }
}
