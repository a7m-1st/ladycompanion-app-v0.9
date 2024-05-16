package com.example.DataBase.data;

public class PostData {
    int PostId; //PK
    String postTitle;
    String postBody;
    int likes;
    String dateOfPost;
    int UserId; //FK
    int userType;


    public PostData(int postId, String postTitle, String postBody, int likes, String dateOfPost, int userId, int userType) {
        PostId = postId;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.likes = likes;
        this.dateOfPost = dateOfPost;
        UserId = userId;
        this.userType = userType;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public void setDateOfPost(String dateOfPost) {
        this.dateOfPost = dateOfPost;
    }

    public int getPostId() {
        return PostId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public int getUserId() {
        return UserId;
    }
    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {this.likes = likes;}

    public String getDateOfPost() {return dateOfPost;}

    public int getUserType() {return userType;}

}
