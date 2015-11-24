package org.robovm.store.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.greenrobot.event.EventBus;
import org.robovm.store.R;
import org.robovm.store.model.Product;
import org.robovm.store.model.ProductReview;
import org.robovm.store.util.EventRefreshReviewsList;
import org.robovm.store.util.Gravatar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 23/11/15.
 */
public class ProductReviewsFragment extends Fragment{

    private ListView mReviewsListView;
    private ImageButton mFabButtonView;
    private View mEmptyListView;
    private String mProductId;
    private List<ProductReview> mReviews;
    private ProductReview mPersonalReview;
    private ReviewsAdapter mReviewsAdapter;

    public ProductReviewsFragment(){
        this.mReviews = new ArrayList<>();
    }

    public ProductReviewsFragment(String pProductId, List<ProductReview> pReviews) {
        this.mProductId = pProductId;
        this.mReviews = pReviews;
    }

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_reviews_list, null, true);
        this.injectViews(view);
        this.afterViews();
        return view;
    }

    @Override public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void injectViews(View pView){
        this.mReviewsListView = ((ListView) pView.findViewById(R.id.reviews_list));
        this.mEmptyListView = pView.findViewById(R.id.empty_view);
        this.mFabButtonView = ((ImageButton) pView.findViewById(R.id.fab));
    }

    private void afterViews(){
        this.mPersonalReview = this.getPersonalReview();
        this.mReviewsAdapter = new ReviewsAdapter(getActivity(), mProductId, mReviews);
        this.mReviewsListView.setAdapter(mReviewsAdapter);
        this.mReviewsListView.setEmptyView(mEmptyListView);
        setFabIconEdit();
        this.mFabButtonView.setOnClickListener(v ->
                AddEditReviewFragment.newInstance(mProductId, mPersonalReview)
                    .show(getFragmentManager(), null)
        );
    }

    private void setFabIconEdit() {
        if(mPersonalReview != null){
            mFabButtonView.setImageResource(R.drawable.ic_content_create);
        }
    }

    private ProductReview getPersonalReview(){
        if(LoginFragment.isRoboVMAccountEmailValid()){
            for (int i = 0; i < mReviews.size(); i++) {
                if(mReviews.get(i).getEmail().equals(LoginFragment.ROBOVM_ACCOUNT_EMAIL)){
                    return mReviews.get(i);
                }
            }
        }
        return null;
    }

    public void onEventMainThread(EventRefreshReviewsList pEvent){
        for(Product product : pEvent.getProducts()){
            if(product.getId().equals(mProductId)){
                mReviews = product.getReviews();
                break;
            }
        }
        this.mReviewsAdapter.update(mReviews);
        this.mPersonalReview = this.getPersonalReview();
        setFabIconEdit();
    }
    public static class ReviewsAdapter extends BaseAdapter{

        private LayoutInflater mLayoutInflater;
        private List<ProductReview> mReviews;
        private String mProductId;

        public ReviewsAdapter(Context pContext, String pProductId, List<ProductReview> pReviews){
            this.mProductId = pProductId;
            this.mLayoutInflater = ((LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            this.mReviews = pReviews == null ? new ArrayList<>() : pReviews;
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
            this.loadAvatar(review.getEmail(), viewHolder.mAvatarView);
            viewHolder.mRatingBarView.setRating(review.getStars());
            viewHolder.mFullNameView.setText(review.getFullName());
            viewHolder.mDateView.setText(review.getDate().toString());
            viewHolder.mCommentView.setText(review.getComment());
            return rowView;
        }

        private void loadAvatar(String pEmail, ImageView pImageView){
            Gravatar.getInstance().getImageBytes(pEmail, 200, Gravatar.Rating.G, bytes -> {
                if(bytes != null){
                    pImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                } else {
                    pImageView.setImageResource(R.drawable.icon);
                }
            });
        }

        public void update(List<ProductReview> pReviews) {
            this.mReviews = pReviews;
            this.notifyDataSetChanged();
        }

        public static class ViewHolder{
            public ImageView mAvatarView;
            public RatingBar mRatingBarView;
            public TextView mFullNameView;
            public TextView mDateView;
            public TextView mCommentView;
            public ViewHolder(View pView){
                this.mAvatarView = ((ImageView) pView.findViewById(R.id.avatar));
                this.mRatingBarView = ((RatingBar) pView.findViewById(R.id.rating));
                this.mFullNameView = ((TextView) pView.findViewById(R.id.fullName));
                this.mDateView = ((TextView) pView.findViewById(R.id.date));
                this.mCommentView = ((TextView) pView.findViewById(R.id.comment));
            }
        }
    }
}
