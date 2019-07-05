package com.doyo.sdk.mvp;

/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     company: 磊华集团
 *     time   : 2019/6/19 14:54
 *     desc   : 列表类的view基类
 *
 * </pre>
 */
public interface IBaseListView extends AbstractView{

    /**
     * 显示加载更多错误
     */
    void showLoadMoreError();


    /**
     * 没有更多数据了
     */
    void showNoMoreData();

}