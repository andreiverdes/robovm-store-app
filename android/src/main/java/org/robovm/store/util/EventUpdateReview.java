package org.robovm.store.util;

import org.robovm.store.model.ProductReview;

/**
 * Created by andrei on 24/11/15.
 */
public class EventUpdateReview {
    private ProductReview mCurrentReview;

    public EventUpdateReview(ProductReview pCurrentReview) {
        this.mCurrentReview = pCurrentReview;
    }

    public ProductReview getReview() {
        return mCurrentReview;
    }
}
