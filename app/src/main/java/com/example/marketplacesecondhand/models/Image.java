package com.example.marketplacesecondhand.models;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("imageId")
    private Long imageId;

    @SerializedName("initialImage")
    private String initialImage;

    @SerializedName("currentImage")
    private String currentImage;

    public Image(Long imageId, String initialImage, String currentImage) {
        this.imageId = imageId;
        this.initialImage = initialImage;
        this.currentImage = currentImage;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getInitialImage() {
        return initialImage;
    }

    public void setInitialImage(String initialImage) {
        this.initialImage = initialImage;
    }

    public String getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(String currentImage) {
        this.currentImage = currentImage;
    }
}
