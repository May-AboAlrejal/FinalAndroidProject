package com.mayabo.finalandroidproject.recipe;

import android.graphics.Bitmap;

/**
 * This is the final project
 * Topic is specify to Recipe Search Engine
 * This project is a group project
 * Each person take one topic
 * My Topic is : Recipe
 *
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class Recipe {

    protected String title;
    protected String url;
    protected String imgUrl;
    protected String imageID;

    protected Bitmap image;


    protected long id;

    //empty constructor for initializing purposes
    public Recipe(){   }


    /**
     * Recipe constructor
     * This will take
     *
     * @Param title, imgUrl, url, id
     * this will be the object that every instance of it will be initialize
     */
    public Recipe(long id, String title, String imageID, String imgUrl, String url) {
        this.title = title;
        this.url = url;
        this.imgUrl = imgUrl;
        this.imageID = imageID;
        this.id = id;
    }

    /**
     * Extra Recipe constructor
     *
     * @Param with only three first parameters as the fist constructor
     */

    public Recipe(String title, String imageID, String imgUrl, String url) {
        this.imageID = imageID;
        this.title = title;
        this.url = url;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }


    /**
     * Simple setters and getters without customize anything
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getImageID() {
        return imageID;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
