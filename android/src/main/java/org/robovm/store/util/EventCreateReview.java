package org.robovm.store.util;

import org.robovm.store.model.ProductReview;

/**
 * Created by andrei on 24/11/15.
 */
public class EventCreateReview {
    private ProductReview mCurrentReview;

    public EventCreateReview(ProductReview pCurrentReview) {
        mCurrentReview = pCurrentReview;
    }

    public ProductReview getReview() {
        return mCurrentReview;
    }
}
