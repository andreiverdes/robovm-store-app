package org.robovm.store.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.model.ProductReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 23/11/15.
 */
public class ProductReviewsFragment extends Fragment{

    private ListView mReviewsListView;
    private final List<ProductReview> mReviews;
    private ReviewsAdapter mReviewsAdapter;

    public ProductReviewsFragment(){
        this.mReviews = new ArrayList<>();
    }

    public ProductReviewsFragment(List<ProductReview> pReviews) {
        this.mReviews = pReviews;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_reviews_list, null, true);
        this.injectViews(view);
        this.afterViews();
        return view;
    }

    private void injectViews(View pView){
        this.mReviewsListView = ((ListView) pView.findViewById(R.id.reviewsList));
    }

    private void afterViews(){
        this.mReviewsAdapter = new ReviewsAdapter(getActivity(), mReviews);
        this.mReviewsListView.setAdapter(mReviewsAdapter);
    }

    public static class ReviewsAdapter extends BaseAdapter{

        private LayoutInflater mLayoutInflater;
        private List<ProductReview> mReviews;

        public ReviewsAdapter(Context pContext, List<ProductReview> pReviews){
            this.mLayoutInflater = ((LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            this.mReviews = pReviews;
        }

        @Override
        public int getCount() {
            return mReviews.size();
        }

        @Override
        public ProductReview getItem(int position) {
            return mReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductReview review = getItem(position);
            View rowView = convertView;
            if(rowView == null){
                rowView = mLayoutInflater.inflate(R.layout.product_reviews_list_item, null);
                rowView.setTag(new ViewHolder(rowView));
            }
            ViewHolder viewHolder = ((ViewHolder) rowView.getTag());
            viewHolder.mRatingBarView.setRating(review.getStars());
            viewHolder.mFullNameView.setText(review.getFullName());
            viewHolder.mDateView.setText(review.getDate().toString());
            viewHolder.mCommentView.setText(review.getComment());
            return rowView;
        }

        public static class ViewHolder{
            public RatingBar mRatingBarView;
            public TextView mFullNameView;
            public TextView mDateView;
            public TextView mCommentView;
            public ViewHolder(View pView){
                this.mRatingBarView = ((RatingBar) pView.findViewById(R.id.rating));
                this.mFullNameView = ((TextView) pView.findViewById(R.id.fullName));
                this.mDateView = ((TextView) pView.findViewById(R.id.date));
                this.mCommentView = ((TextView) pView.findViewById(R.id.comment));
            }
        }
    }
}
