package com.doyo.sdk.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.doyo.sdk.R;
import com.doyo.sdk.mvp.BaseSimplePresenter;
import com.doyo.sdk.mvp.IBaseNetView;


/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     company: 永无bug集团
 *     time   : 2019/05/20
 *     desc   : 网络请求加载状态view的fragment，主要用于显示加载中、空界面、加载失败等状态界面显示
 *
 * </pre>
 */

public abstract class BaseNetFragment<T extends BaseSimplePresenter> extends BaseMVPFragment<T> implements IBaseNetView {

    private static final int NORMAL_STATE  = 0;
    private static final int LOADING_STATE = 1;
    public static final  int ERROR_STATE   = 2;
    public static final  int EMPTY_STATE   = 3;

    protected ViewGroup           parent;
    private   LottieAnimationView mLoadingAnimation;
    protected ViewGroup           mNormalView;
    protected View                mErrorView;
    protected View                mLoadingView;
    protected View                mEmptyiew;

    protected int currentState = NORMAL_STATE;


    @Override
    protected void lazyFetchData() {

        if (getView() == null) {
            return;
        }

        mNormalView = getView().findViewById(R.id.normal_view);
        if (mNormalView == null) {
            throw new IllegalStateException(
                    "The subclass of RootActivity must contain a View named 'mNormalView'.");
        }
        if (!(mNormalView.getParent() instanceof ViewGroup)) {
            throw new IllegalStateException(
                    "mNormalView's ParentView should be a ViewGroup.");
        }
        parent = (ViewGroup) mNormalView.getParent();
        View.inflate(_mActivity, R.layout.loading_view, parent);
        View.inflate(_mActivity, R.layout.error_view, parent);
        mLoadingView = parent.findViewById(R.id.loading_group);
        mErrorView = parent.findViewById(R.id.error_group);
        TextView reloadTv = mErrorView.findViewById(R.id.error_reload_tv);
        reloadTv.setOnClickListener(v -> reload());
        mLoadingAnimation = mLoadingView.findViewById(R.id.loading_animation);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mNormalView.setVisibility(View.VISIBLE);
    }

    protected abstract void reload();

    @Override
    public void onDestroyView() {
        if (mLoadingAnimation != null) {
            mLoadingAnimation.cancelAnimation();
        }
        super.onDestroyView();
    }

    @Override
    public void showLoading() {

        if (currentState == LOADING_STATE || mLoadingView == null) {
            return;
        }
        hideCurrentView();
        currentState = LOADING_STATE;
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingAnimation.setAnimation("loading_bus.json");
        mLoadingAnimation.loop(true);
        mLoadingAnimation.playAnimation();
    }

    @Override
    public void showError() {
        if (currentState == ERROR_STATE) {
            return;
        }
        hideCurrentView();
        currentState = ERROR_STATE;
        mErrorView.setVisibility(View.VISIBLE);
    }


    @Override
    public void showNormal() {
        if (currentState == NORMAL_STATE) {
            return;
        }
        hideCurrentView();
        currentState = NORMAL_STATE;
        mNormalView.setVisibility(View.VISIBLE);
    }

    protected void hideCurrentView() {
        switch (currentState) {
            case NORMAL_STATE:
                if (mNormalView == null) {
                    return;
                }
                mNormalView.setVisibility(View.INVISIBLE);
                break;
            case LOADING_STATE:
                mLoadingAnimation.cancelAnimation();
                mLoadingView.setVisibility(View.GONE);
                break;
            case ERROR_STATE:
                mErrorView.setVisibility(View.GONE);
            case EMPTY_STATE:
                mEmptyiew.setVisibility(View.GONE);
            default:
                break;
        }
    }
}
