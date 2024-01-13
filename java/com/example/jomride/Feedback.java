package com.example.jomride;

public class Feedback {
    private float rating;
    private String feedbackText;

    // Required default constructor for Firebase
    public Feedback() {
    }

    // Constructor to initialize Feedback object
    public Feedback(float rating, String feedbackText) {
        this.rating = rating;
        this.feedbackText = feedbackText;
    }

    // Getter for rating
    public float getRating() {
        return rating;
    }

    // Setter for rating
    public void setRating(float rating) {
        this.rating = rating;
    }

    // Getter for feedbackText
    public String getFeedbackText() {
        return feedbackText;
    }

    // Setter for feedbackText
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}
