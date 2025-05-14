package com.example.marketplacesecondhand.dto.response;

import java.util.Date;

public class FeedbackResponse {
    int productId;
    int feedbackerId;
    String feedbackerName;
    String productName;
    String imageFeedbacker;
    int star;
    String feedback;
    Date createdAt;

    public FeedbackResponse(int productId, int feedbackerId, String feedbackerName, String productName, String imageFeedbacker, int star, String feedback, Date createdAt) {

        this.productId = productId;
        this.feedbackerId = feedbackerId;
        this.feedbackerName = feedbackerName;
        this.productName = productName;
        this.imageFeedbacker = imageFeedbacker;
        this.star = star;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getFeedbackerId() {
        return feedbackerId;
    }

    public void setFeedbackerId(int feedbackerId) {
        this.feedbackerId = feedbackerId;
    }

    public String getFeedbackerName() {
        return feedbackerName;
    }

    public void setFeedbackerName(String feedbackerName) {
        this.feedbackerName = feedbackerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageFeedbacker() {
        return imageFeedbacker;
    }

    public void setImageFeedbacker(String imageFeedbacker) {
        this.imageFeedbacker = imageFeedbacker;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
