package com.example.marketplacesecondhand.dto.response;

import java.util.Date;

public class FeedbackResponse {
    int productId;
    int FeedbackerId;
    String FeedbackerName;
    String productName;
    String imageFeedbacker;
    int star;
    String feedback;
    Date createdAt;

    public FeedbackResponse(int productId, int feedbackerId, String feedbackerName, String productName, String imageFeedbacker, int star, String feedback, Date createdAt) {
        this.productId = productId;
        FeedbackerId = feedbackerId;
        FeedbackerName = feedbackerName;
        this.productName = productName;
        this.imageFeedbacker = imageFeedbacker;
        this.star = star;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    public FeedbackResponse() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getFeedbackerId() {
        return FeedbackerId;
    }

    public void setFeedbackerId(int feedbackerId) {
        FeedbackerId = feedbackerId;
    }

    public String getFeedbackerName() {
        return FeedbackerName;
    }

    public void setFeedbackerName(String feedbackerName) {
        FeedbackerName = feedbackerName;
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
