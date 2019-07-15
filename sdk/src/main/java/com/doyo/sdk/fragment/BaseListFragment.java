package com.doyo.sdk.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.doyo.sdk.R;
import com.doyo.sdk.adapter.BaseCompatAdapter;
import com.doyo.sdk.mvp.BaseSimplePresenter;
import com.doyo.sdk.mvp.IBaseListView2;
import com.doyo.sdk.utils.NetUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     company: 永无bug集团
 *     time   : 2019/05/20
 *     desc   : 列表类fragment的基类
 *
 * </pre>
 */

public abstract class BaseListFragment<P extends BaseSimplePresenter,
        A extends BaseCompatAdapter, D> extends BaseNetFragment<P> implements
        BaseQuickAdapter.RequestLoadMoreListener, IBaseListView2<D> {

    protected RecyclerView       mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;

    protected A       mAdapter;
    protected P       mPresenter;
    protected int     currentPage;
    protected String  id;
    protected boolean isRefresh  = true;
    /**
     * 是否有加载更多.默认是有的
     */
    protected boolean isHavaMore = true;


    @Override
    protected int getLayoutId() {
        return R.layout.base_recyclerview;
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        View.inflate(_mActivity, R.layout.empty_view, parent);
        mEmptyiew = parent.findViewById(R.id.empty_group);
        mEmptyiew.setOnClickListener(v -> reload());
        mEmptyiew.setVisibility(View.GONE);

        getInitData();
        setRefresh();

        currentPage = 1;
        getData(currentPage, true, id);

        if (NetUtils.isNetworkConnected()) {
            showLoading();
        }
    }


    @Override
    protected void initViews() {
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        mRefreshLayout = mView.findViewById(R.id.normal_view);
        mAdapter = (A) getAbstractAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        if (isHavaMore) {
            mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        }
    }

    protected abstract BaseCompatAdapter getAbstractAdapter();

    protected abstract void getInitData();


    private void setRefresh() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            currentPage = 1;
            isRefresh = true;
            getData(currentPage, false, id);
            refreshLayout.finishRefresh(1000);
        });
    }

    @Override
    public void reload() {
        currentPage = 1;
        isRefresh = true;
        getData(currentPage, false, id);
    }

    @Override
    public void onLoadMoreRequested() {
        currentPage++;
        isRefresh = false;
        getData(currentPage, false, id);
    }

    protected abstract void getData(int currentPage, boolean isShow, String id);

    @Override
    public void showLoadMoreError() {
        mAdapter.closeLoadAnimation();
        mAdapter.loadMoreFail();
    }


    @Override
    public void showNoMoreData() {
        mAdapter.loadMoreEnd();
    }

    @Override
    public void showEmpty() {
        if (currentState == EMPTY_STATE) {
            return;
        }
        hideCurrentView();
        currentState = EMPTY_STATE;
        mEmptyiew.setVisibility(View.VISIBLE);
    }

}
