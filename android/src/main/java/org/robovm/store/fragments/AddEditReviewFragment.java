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
import org.robovm.store.model.ProductReview;

/**
 * Created by andrei on 24/11/15.
 */
public class AddEditReviewFragment extends DialogFragment {

    public static AddEditReviewFragment newInstance(ProductReview productReview){
        return new AddEditReviewFragment();
    }

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Review")
                .setPositiveButton("OK", (dialog, which) -> {
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                })
                .create();
        return alertDialog;
    }
}
