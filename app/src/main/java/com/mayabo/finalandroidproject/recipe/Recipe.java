package com.mayabo.finalandroidproject.recipe;

/**
 * This is the final project
 * Topic is specify to Recipe Search Engine
 * This project is a group project
 * Each person take one topic
 * My Topic is : Recipe
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class Recipe {

    protected String title;
    protected String url;
    protected String imgUrl;



    protected long id;



    /**Recipe constructor
     * This will take
     * @Param title, imgUrl, url, id
     * this will be the object that every instance of it will be initialize
     * */
    public Recipe(String title,String imgUrl,String url,long id) {
        this.title = title;
        this.url = url;
        this.imgUrl = imgUrl;
        this.id = id;

    }

    /**Extra Recipe constructor
     * @Param with only three first parameters as the fist constructor
     * */

    public Recipe(String title,String imgUrl,String url) {
        this.title = title;
        this.url = url;
        this.imgUrl = imgUrl;
        this.id = 0;
    }

    public String getTitle() {
        return title;
    }

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
