package com.Activity.socialconnect;

public class ModelBlog {

    String blogDescription;
    String blogImage;
    String blogUserId;


    public ModelBlog(){}

    public ModelBlog(String blogDescription, String blogImage, String blogUserId) {
        this.blogDescription = blogDescription;
        this.blogImage = blogImage;
        this.blogUserId = blogUserId;
    }

    public String getBlogDescription() {
        return blogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }

    public String getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }

    public String getBlogUserId() {
        return blogUserId;
    }

    public void setBlogUserId(String blogUserId) {
        this.blogUserId = blogUserId;
    }
}
