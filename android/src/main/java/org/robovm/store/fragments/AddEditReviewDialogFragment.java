package org.robovm.store.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import org.robovm.store.R;
import org.robovm.store.model.ProductReview;
import org.robovm.store.util.EventCreateReview;
import org.robovm.store.util.EventUpdateReview;

import java.util.Date;

/**
 * Created by andrei on 24/11/15.
 */
public class AddEditReviewDialogFragment extends DialogFragment {

    public static final String TAG_STARS = "tag_stars";
    public static final String TAG_PRODUCT_ID = "tag_product_id";
    public static final String TAG_REVIEW_ID = "tag_review_id";
    public static final String TAG_FULL_NAME = "tag_full_name";
    public static final String TAG_COMMENT = "tag_comment";
    public static final String TAG_NEW_REVIEW = "tag_new_review";


    private RatingBar mRatingBarView;
    private EditText mFullNameEditText;
    private EditText mCommentEditText;

    private ProductReview mCurrentReview;
    private boolean mIsNewReview;

    public AddEditReviewDialogFragment(){
        this.mCurrentReview = new ProductReview();
    }
    public static AddEditReviewDialogFragment newInstance(String pProductId) {
        return newInstance(pProductId, null);
    }
    public static AddEditReviewDialogFragment newInstance(String pProductId, ProductReview productReview){
        AddEditReviewDialogFragment fragment = new AddEditReviewDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_PRODUCT_ID, pProductId);
        bundle.putBoolean(TAG_NEW_REVIEW, true);
        if(productReview != null) {
            bundle.putBoolean(TAG_NEW_REVIEW, false);
            bundle.putInt(TAG_STARS, productReview.getStars());
            bundle.putString(TAG_REVIEW_ID, productReview.getId());
            bundle.putString(TAG_FULL_NAME, productReview.getFullName());
            bundle.putString(TAG_COMMENT, productReview.getComment());
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        if(LoginFragment.isRoboVMAccountEmailValid()){
            alertDialogBuilder.setPositiveButton(R.string.dialog_add_review_positive, (dialog, which) -> {
               //do nothing here, override onStart to stop the dialog to dismiss when clicking OK
            });
            contentView = LayoutInflater.from(getActivity()).inflate(R.layout.product_reviews_add,null,false);
            this.injectViews(contentView);
            this.afterViews();
        } else {
            contentView = LoginFragment.createInstructions(LayoutInflater.from(getActivity()),null,null);
        }
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(R.string.dialog_add_review_title)
                .setView(contentView)
                .setNegativeButton(R.string.dialog_add_review_negative, (dialog, which) -> {
                    dismiss();
                });
        return alertDialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            if(LoginFragment.isRoboVMAccountEmailValid()) {
                Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                    if (validateForm()) {
                        mCurrentReview.setFullName(mFullNameEditText.getText().toString());
                        mCurrentReview.setComment(mCommentEditText.getText().toString());
                        mCurrentReview.setDate(new Date());
                        mCurrentReview.setEmail(LoginFragment.ROBOVM_ACCOUNT_EMAIL);
                        mCurrentReview.setStars((int)mRatingBarView.getRating());
                        dialog.dismiss();
                        if(mIsNewReview){
                            createReview();
                        } else {
                            updateReview();
                        }
                    }
                });
            }
        }
    }

    private void createReview(){
        EventBus.getDefault().post(new EventCreateReview(mCurrentReview));
    }

    private void updateReview(){
        EventBus.getDefault().post(new EventUpdateReview(mCurrentReview));
    }

    private void injectViews(View pView){
        this.mRatingBarView = ((RatingBar) pView.findViewById(R.id.rating));
        this.mFullNameEditText = ((EditText) pView.findViewById(R.id.fullName));
        this.mCommentEditText = ((EditText) pView.findViewById(R.id.comment));
    }

    private void afterViews(){
        this.mRatingBarView.setStepSize(1f);
        if(getArguments() != null){
            this.mRatingBarView.setRating(getArguments().getInt(TAG_STARS, 0));
            this.mFullNameEditText.setText(getArguments().getString(TAG_FULL_NAME, ""));
            this.mCommentEditText.setText(getArguments().getString(TAG_COMMENT, ""));
            this.mCurrentReview.setProductId(getArguments().getString(TAG_PRODUCT_ID, null));
            this.mCurrentReview.setId(getArguments().getString(TAG_REVIEW_ID, null));
            this.mIsNewReview = getArguments().getBoolean(TAG_NEW_REVIEW, true);
        }
    }

    private boolean validateForm() {
        return validateRatingBar(mRatingBarView)
                && validateEditText(mFullNameEditText)
                && validateEditText(mCommentEditText);
    }

    private boolean validateRatingBar(RatingBar pRatingBarView) {
        if(pRatingBarView.getRating() == 0){
            toast("Please give your stars rating!");
            return false;
        }
        return true;
    }

    private boolean validateEditText(EditText pEditText) {
        if(pEditText.getText() == null || pEditText.getText().toString().isEmpty()){
            toast("Please input your " + pEditText.getHint()+"!");
            return false;
        }
        return true;
    }


    private void toast(String pMessage){
        Toast.makeText(getActivity(), pMessage, Toast.LENGTH_SHORT).show();
    }
}
