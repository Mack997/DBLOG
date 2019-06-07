package com.example.mayankagarwal.dblog.Model;

import java.util.Date;

public class Post {


    public String user_id, image, description, thumb_image;

    public Date timestamp;

    public Post(){

    }

    public Post(String user_id, String image, String desc, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image = image;
        this.description = desc;
        this.thumb_image = image_thumb;

        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
