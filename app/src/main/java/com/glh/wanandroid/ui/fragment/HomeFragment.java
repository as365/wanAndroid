package com.glh.wanandroid.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.doyo.sdk.adapter.BaseCompatAdapter;
import com.doyo.sdk.fragment.BaseListFragmentEx;
import com.doyo.sdk.mvp.AbstractPresenter;
import com.doyo.sdk.rx.RxBus;
import com.doyo.sdk.utils.GlideImageLoader;
import com.doyo.sdk.utils.JumpUtils;
import com.doyo.sdk.utils.UiUtils;
import com.glh.wanandroid.R;
import com.glh.wanandroid.bean.BannerData;
import com.glh.wanandroid.bean.FeedArticleData;
import com.glh.wanandroid.bean.FeedArticleListData;
import com.glh.wanandroid.constant.Constants;
import com.glh.wanandroid.core.event.AutoLoginEvent;
import com.glh.wanandroid.presenter.HomePresenter;
import com.glh.wanandroid.presenter.contract.HomeContract;
import com.glh.wanandroid.ui.activity.LoginActivity;
import com.glh.wanandroid.ui.adapter.ArticleListAdapter;
import com.glh.wanandroid.utils.MvpUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     company: 永无bug集团
 *     time   : 2019/05/09
 *     desc   : 首页底部-----主页fragment
 *
 * </pre>
 */

public class HomeFragment extends BaseListFragmentEx<HomePresenter, ArticleListAdapter> implements HomeContract.View {

    private Banner       mBanner;
    private List<String> mBannerTitleList;
    private List<String> mBannerUrlList;
    private boolean      isRecreate;
    private int          articlePosition;


    public static HomeFragment getInstance(boolean param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.ARG_PARAM1, param1);
        args.putString(Constants.ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isRecreate = getArguments().getBoolean(Constants.ARG_PARAM1);
    }


    @Override
    protected BaseCompatAdapter getAbstractAdapter() {

        mBannerUrlList = new ArrayList<>();
        mBannerTitleList = new ArrayList<>();

        mAdapter = new ArticleListAdapter(R.layout.item_search_pager, null);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> clickChildEvent(view,
                position));

        mAdapter.setOnItemClickListener((adapter, view, position) -> {

        });


        LinearLayout mHeaderGroup =
                (LinearLayout) LayoutInflater.from(_mActivity).inflate(R.layout.head_banner, null);
        mBanner = mHeaderGroup.findViewById(R.id.head_banner);

        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());

        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        mBanner.setBannerTitles(mBannerTitleList);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        mHeaderGroup.removeView(mBanner);
        mAdapter.addHeaderView(mBanner);

        return mAdapter;
    }


    @Override
    protected void getInitData() {
        firstPager = 0;
    }


    private void clickChildEvent(View view, int position) {
        switch (view.getId()) {
            case R.id.item_search_pager_like_iv:
                articlePosition = position;
                likeEvent(position);
                break;
            default:
                break;
        }
    }

    private void likeEvent(int position) {
        if (!mPresenter.getLoginStatus()) {
            JumpUtils.JumpToActivity(_mActivity, LoginActivity.class);
            return;
        }
        if (mAdapter.getData().size() <= 0 || mAdapter.getData().size() <= position) {
            return;
        }
        if (mAdapter.getData().get(position).collect) {
            mPresenter.cancelCollectArticle(position, mAdapter.getData().get(position));
        } else {
            mPresenter.addCollectArticle(position, mAdapter.getData().get(position));
        }
    }


    private boolean loggedAndNotRebuilt() {
        return !TextUtils.isEmpty(mPresenter.getLoginAccount())
                && !TextUtils.isEmpty(mPresenter.getLoginPassword())
                && !isRecreate;
    }


    @Override
    public void showAutoLoginSuccess() {
        if (isAdded()) {
            UiUtils.showSnackMessage(_mActivity, getString(R.string.auto_login_success));
            RxBus.getDefault().post(new AutoLoginEvent());
        }
    }

    @Override
    public void showAutoLoginFail() {

    }


    @Override
    public void showCollectArticleData(int position, FeedArticleData feedArticleData,
                                       FeedArticleListData feedArticleListData) {
        mAdapter.setData(position, feedArticleData);
        UiUtils.showSnackMessage(_mActivity, getString(R.string.collect_success));
    }

    @Override
    public void showCancelCollectArticleData(int position, FeedArticleData feedArticleData,
                                             FeedArticleListData feedArticleListData) {

        mAdapter.setData(position, feedArticleData);
        UiUtils.showSnackMessage(_mActivity, getString(R.string.cancel_collect_success));

    }

    @Override
    public void showCollectSuccess() {
        if (mAdapter != null && mAdapter.getData().size() > articlePosition) {
            mAdapter.getData().get(articlePosition).collect = true;
            mAdapter.setData(articlePosition, mAdapter.getData().get(articlePosition));
        }
    }

    @Override
    public void showCancelCollectSuccess() {
        if (mAdapter != null && mAdapter.getData().size() > articlePosition) {
            mAdapter.getData().get(articlePosition).collect = false;
            mAdapter.setData(articlePosition, mAdapter.getData().get(articlePosition));
        }
    }


    @Override
    public void showBannerData(List<BannerData> bannerDataList) {

        List<String> bannerImageList = new ArrayList<>();

        for (BannerData bannerData : bannerDataList) {
            mBannerTitleList.add(bannerData.title);
            bannerImageList.add(bannerData.imagePath);
            mBannerUrlList.add(bannerData.url);
        }

        mBanner.setImages(bannerImageList);
        mBanner.start();
    }


    @Override
    public void showLoginView() {
        mPresenter.getFeedArticleList(curPage, false);
    }

    @Override
    public void showLogoutView() {
        mPresenter.getFeedArticleList(curPage, false);
    }


    @Override
    protected AbstractPresenter initPresenter() {
        mPresenter = new HomePresenter(MvpUtils.initDataManager(), this);
        return mPresenter;
    }

    @Override
    protected void getData(int curPage, boolean isShow, String id) {

        mPresenter.getFeedArticleList(curPage, isShow);

        if (isRefresh) {
            mPresenter.getBannerData(false);
        }
    }

}
