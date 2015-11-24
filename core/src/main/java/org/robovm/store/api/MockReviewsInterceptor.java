package org.robovm.store.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;
import okio.Buffer;
import okio.BufferedSource;
import org.apache.commons.io.IOUtils;
import org.robovm.store.model.Product;
import org.robovm.store.model.ProductReview;
import org.robovm.store.util.Action;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrei on 23/11/15.
 */
public class MockReviewsInterceptor implements Interceptor, Action<Product> {

    private Map<String, List<ProductReview>> mReviewsCache;
    private Gson mGson;

    public MockReviewsInterceptor(){
        this.mReviewsCache = new HashMap<>();
        this.mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:sss")
                .create();
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if(isGetProductsRequest(request)){
            response = response.newBuilder()
                    .body(new ReviewsResponseBodyDecorator(mGson, mReviewsCache, response.body(), this))
                    .build();
        } else if(isCreateReview(request)){
            ProductReview productReview = getProductReview(request);
            List<ProductReview> reviews = mReviewsCache.get(productReview.getProductId());
            if(reviews != null){
                reviews.add(0, productReview);
            }
        } else if(isUpdateReview(request)){
            ProductReview productReview = getProductReview(request);
            List<ProductReview> reviews = mReviewsCache.get(productReview.getProductId());
            if(reviews != null){
                for(ProductReview itemProductReview : reviews){
                    if(itemProductReview.getId().equals(productReview.getId())){
                        reviews.set(reviews.indexOf(itemProductReview), productReview);
                    }
                }
            }
        }
        return response;
    }

    private ProductReview getProductReview(Request request) throws IOException {
        ProductReview productReview;
        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        String body = buffer.readUtf8();
        productReview = mGson.fromJson(body, ReviewRequest.class).review;
        return productReview;
    }

    private boolean isGetProductsRequest(Request pRequest){
        return pRequest.urlString().contains("products") && pRequest.method().equals("GET");
    }

    private boolean isCreateReview(Request pRequest){
        return pRequest.urlString().contains("review") && pRequest.method().equals("POST");
    }

    private boolean isUpdateReview(Request pRequest){
        return pRequest.urlString().contains("review") && pRequest.method().equals("PUT");
    }

    @Override
    public void invoke(Product product) {
        mReviewsCache.put(product.getId(), product.getReviews());
    }

    public static class ReviewsResponseBodyDecorator extends ResponseBody{

        private Gson mGson;
        private Map<String, List<ProductReview>> mCache;
        private ResponseBody mOriginalBody;
        private Action<Product> mAction;

        public ReviewsResponseBodyDecorator(Gson pGson, Map<String, List<ProductReview>> pCache, ResponseBody pResponseBody, Action<Product> pAction){
            this.mGson = pGson;
            this.mCache = pCache;
            this.mOriginalBody = pResponseBody;
            this.mAction = pAction;
        }

        @Override
        public MediaType contentType() {
            return mOriginalBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }

        @Override
        public BufferedSource source() throws IOException {
            ProductsResponse productsResponse = mGson.fromJson(mOriginalBody.charStream(), ProductsResponse.class);
            if(productsResponse.getProducts() != null) {
                this.addMockedReviews(productsResponse.getProducts());
            }
            return new Buffer().readFrom(IOUtils.toInputStream(mGson.toJson(productsResponse)));
        }

        private void addMockedReviews(List<Product> products) {
            for (Product product : products){
                List<ProductReview> reviews = mCache.get(product.getId());
                if(reviews == null) {
                    Type listType = new TypeToken<ArrayList<ProductReview>>() {
                    }.getType();
                    InputStream inputStream = null;
                    if (product.getId().equals("robovm-t-shirt-male")) {
                        inputStream = MockReviewsInterceptor.class.getResourceAsStream("/mock_men_tshirt_reviews.json");
                    } else if (product.getId().equals("robovm-t-shirt-female")) {
                        inputStream = MockReviewsInterceptor.class.getResourceAsStream("/mock_women_tshirt_reviews.json");
                    }
                    if (inputStream != null) {
                        try {
                            reviews = mGson.fromJson(IOUtils.toString(inputStream, Charset.defaultCharset()), listType);
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (reviews == null) {
                        reviews = new ArrayList<>();
                    }
                    for (ProductReview productReview : reviews) {
                        productReview.setProductId(product.getId());
                    }
                }
                product.setReviews(reviews);
                mAction.invoke(product);
            }
        }
    }
}
