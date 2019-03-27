package com.assignment.animation.animationassignment.model;

/**
 * Created by Nakul on 8/8/18.
 */


public class Movie {
    private int id;
    private String title, thumbnailUrl, imageUrl;

    public Movie() {
    }

    public Movie(int id, String title, String thumbnailUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}