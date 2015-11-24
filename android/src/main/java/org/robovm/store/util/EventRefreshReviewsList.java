package org.robovm.store.util;

import org.robovm.store.model.Product;
import org.robovm.store.model.ProductReview;

import java.util.List;

/**
 * Created by andrei on 24/11/15.
 */
public class EventRefreshReviewsList {

    private List<Product> mProducts;

    public EventRefreshReviewsList(List<Product> pProducts) {

        mProducts = pProducts;
    }

    public List<Product> getProducts() {
        return mProducts;
    }
}