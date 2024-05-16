package com.example.DataBase.data;

public class BlogsDataWeb {
    String blogUrl;
    String blogTitle;
    String blogBody;

    public BlogsDataWeb(String blogUrl, String blogTitle, String blogBody) {
        this.blogUrl = blogUrl;
        this.blogTitle = blogTitle;
        this.blogBody = blogBody;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogBody() {
        return blogBody;
    }

    public void setBlogBody(String blogBody) {
        this.blogBody = blogBody;
    }
}
