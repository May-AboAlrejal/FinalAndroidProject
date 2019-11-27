package com.mayabo.finalandroidproject.news;

import android.graphics.Bitmap;
import java.io.Serializable;
import java.util.Objects;

public class NewsApiResponse implements Serializable {
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;
    private String source;
    private String imageName;
    private long id;

    public static final String STATUS = "status";
    public static final String ARTICLES = "articles";
    public static final String AUTHOR = "author";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String URL_TO_IMAGE = "urlToImage";
    public static final String PUBLISHED_AT = "publishedAt";
    public static final String CONTENT = "content";
    public static final String SOURCE = "source";
    public static final String NAME = "name";
    public static final String IMAGE_NAME = "imageName";


    public NewsApiResponse() {
    }

    public NewsApiResponse(String author, String title, String description, String url, String urlToImage, String publishedAt, String content, String source) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsApiResponse that = (NewsApiResponse) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(url, that.url) &&
                Objects.equals(urlToImage, that.urlToImage) &&
                Objects.equals(publishedAt, that.publishedAt) &&
                Objects.equals(content, that.content) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, description, url, urlToImage, publishedAt, content, source);
    }
}

