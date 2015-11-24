package org.robovm.store.model;

import java.util.Date;

/**
 * Created by andrei on 21/11/15.
 */
public class ProductReview {
    private String id;
    private String productId;
    private int stars;
    private Date date;
    private String email;
    private String fullName;
    private String comment;

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
