package org.robovm.store.api;

import org.robovm.store.model.ProductReview;

/**
 * Created by andrei on 24/11/15.
 */
public class ReviewRequest {
    final ProductReview review;

    public ReviewRequest(ProductReview review) {
        this.review = review;
    }
}
