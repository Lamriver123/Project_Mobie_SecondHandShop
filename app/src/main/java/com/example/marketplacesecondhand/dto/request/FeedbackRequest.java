package com.example.marketplacesecondhand.dto.request;

import java.util.Date;

public class FeedbackRequest {
    int productId;
    int orderId;
    int feedbackerId;
    int star;
    String feedback;

    public FeedbackRequest(int productId, int feedbackerId, int star, String feedback, int orderId) {
        this.productId = productId;
        this.feedbackerId = feedbackerId;
        this.star = star;
        this.feedback = feedback;
        this.orderId = orderId;
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
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
