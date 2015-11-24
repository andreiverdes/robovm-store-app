package org.robovm.store.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import org.robovm.store.R;
import org.robovm.store.model.ProductReview;

import java.util.Date;

/**
 * Created by andrei on 24/11/15.
 */
public class AddEditReviewFragment extends DialogFragment {

    public static final String TAG_STARS = "tag_stars";
    public static final String TAG_FULL_NAME = "tag_full_name";
    public static final String TAG_COMMENT = "tag_comment";


    private RatingBar mRatingBarView;
    private EditText mFullNameEditText;
    private EditText mCommentEditText;

    private ProductReview mCurrentReview;

    public AddEditReviewFragment(){
        this.mCurrentReview = new ProductReview();
    }
    public static AddEditReviewFragment newInstance() {
        return newInstance(null);
    }
    public static AddEditReviewFragment newInstance(ProductReview productReview){
        AddEditReviewFragment fragment = new AddEditReviewFragment();
        if(productReview != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(TAG_STARS, productReview.getStars());
            bundle.putString(TAG_FULL_NAME, productReview.getFullName());
            bundle.putString(TAG_COMMENT, productReview.getComment());
            fragment.setArguments(bundle);
        }
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
        if(LoginFragment.isRoboVMAccountEmailValid()) {
            final AlertDialog dialog = (AlertDialog) getDialog();
            if (dialog != null) {
                Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                    if (validateForm()) {
                        mCurrentReview.setFullName(mFullNameEditText.getText().toString());
                        mCurrentReview.setComment(mCommentEditText.getText().toString());
                        mCurrentReview.setDate(new Date());
                        mCurrentReview.setEmail(LoginFragment.ROBOVM_ACCOUNT_EMAIL);
                        mCurrentReview.setStars(mRatingBarView.getNumStars());
                        dialog.dismiss();
                        if(getArguments() == null){ //if we haven't passed as an argument our existing review
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

    }

    private void updateReview(){

    }

    private void injectViews(View pView){
        this.mRatingBarView = ((RatingBar) pView.findViewById(R.id.rating));
        this.mFullNameEditText = ((EditText) pView.findViewById(R.id.fullName));
        this.mCommentEditText = ((EditText) pView.findViewById(R.id.comment));
    }

    private void afterViews(){
        if(getArguments() != null){
            this.mRatingBarView.setRating(getArguments().getInt(TAG_STARS, 0));
            this.mFullNameEditText.setText(getArguments().getString(TAG_FULL_NAME, ""));
            this.mCommentEditText.setText(getArguments().getString(TAG_COMMENT, ""));
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
