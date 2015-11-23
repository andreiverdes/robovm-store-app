package org.robovm.store.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;
import com.squareup.okhttp.internal.http.HttpMethod;
import okio.Buffer;
import okio.BufferedSource;
import org.apache.commons.io.IOUtils;
import org.robovm.store.model.Product;
import org.robovm.store.model.ProductReview;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 23/11/15.
 */
public class MockReviewsInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        return isGetProductsRequest(request)
                ? response.newBuilder()
                    .body(new ReviewsResponseBodyDecorator(response.body()))
                    .build()
                : response;
    }

    private boolean isGetProductsRequest(Request pRequest){
        return pRequest.urlString().contains("products") && pRequest.method().equals("GET");
    }

    public static class ReviewsResponseBodyDecorator extends ResponseBody{

        private ResponseBody mOriginalBody;
        private Gson mGson;

        public ReviewsResponseBodyDecorator(ResponseBody pResponseBody){
            this.mOriginalBody = pResponseBody;
            this.mGson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:sss")
                    .create();
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
                Type listType = new TypeToken<ArrayList<ProductReview>>() {}.getType();
                List<ProductReview> reviews = null;
                InputStream inputStream = null;
                if(product.getId().equals("robovm-t-shirt-male")){
                    inputStream = MockReviewsInterceptor.class.getResourceAsStream("/mock_men_tshirt_reviews.json");
                } else if (product.getId().equals("robovm-t-shirt-female")){
                    inputStream = MockReviewsInterceptor.class.getResourceAsStream("/mock_women_tshirt_reviews.json");
                }
                if(inputStream != null) {
                    try {
                        reviews = mGson.fromJson(IOUtils.toString(inputStream, Charset.defaultCharset()), listType);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(reviews == null){
                    reviews = new ArrayList<>();
                }
                product.setReviews(reviews);
            }
        }
    }
}
