package org.robovm.store.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import de.greenrobot.event.EventBus;
import org.robovm.store.R;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.Basket;
import org.robovm.store.model.Product;
import org.robovm.store.util.EventSetupActionBar;
import org.robovm.store.views.BadgeDrawable;

/**
 * Created by andrei on 23/11/15.
 */
public class ProductFragment extends Fragment {

    private static final int PAGES = 2;

    private ViewPager mProductViewPager;
    private FragmentStatePagerAdapter mProductPagerAdapter;
    private Product mProduct;
    private int mItemVerticalOffset;
    private BadgeDrawable basketBadge;

    public ProductFragment(){}

    public ProductFragment(Product pProduct, int pItemVerticalOffset) {
        mProduct = pProduct;
        mItemVerticalOffset = pItemVerticalOffset;
    }

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_viewpager, null, true);
        this.injectViews(view);
        this.afterViews();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem cartItem = menu.findItem(R.id.cart_menu_item);
        cartItem.setIcon(basketBadge = new BadgeDrawable(cartItem.getIcon()));

        Basket basket = RoboVMWebService.getInstance().getBasket();
        basketBadge.setCount(basket.size());
        basket.addOnBasketChangeListener(() -> basketBadge.setCountAnimated(basket.size()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void injectViews(View pView){
        this.mProductViewPager = ((ViewPager) pView.findViewById(R.id.viewpager));
    }

    private void afterViews(){
        this.setHasOptionsMenu(true);
        this.mProductPagerAdapter = new ProductPagerAdapter(getActivity(), getFragmentManager(), PAGES, mProduct, mItemVerticalOffset);
        this.mProductViewPager.setAdapter(mProductPagerAdapter);
    }

    public static class ProductPagerAdapter extends FragmentStatePagerAdapter{
        private Context mContext;
        private final Product mProduct;
        private final int mPagesCount;
        private int mSlidingDelta;

        public ProductPagerAdapter(Context pContext, FragmentManager fm, int pPagesCount, Product pProduct, int pSlidingDelta) {
            super(fm);
            this.mContext = pContext;
            this.mPagesCount = pPagesCount;
            this.mProduct = pProduct;
            this.mSlidingDelta = pSlidingDelta;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:{
                    ProductDetailsFragment productDetails = new ProductDetailsFragment(mProduct, mSlidingDelta);
                    productDetails.setAddToBasketListener((order) -> {
                        RoboVMWebService.getInstance().getBasket().add(order);
                        EventBus.getDefault().post(new EventSetupActionBar());
                    });
                    return productDetails;
                }
                case 1: return new ProductReviewsFragment(mProduct.getReviews());
            }
            return null;
        }

        @Override
        public int getCount() {
            return mPagesCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0
                    ? mContext.getString(R.string.tab_title_details)
                    : mContext.getString(R.string.tab_title_reviews);
        }
    }
}
