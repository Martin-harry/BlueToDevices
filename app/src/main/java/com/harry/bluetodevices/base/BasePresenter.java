package com.harry.bluetodevices.base;

import com.harry.bluetodevices.http.RetrofitHelper;
import com.harry.bluetodevices.http.RetrofitService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BasePresenter<V extends BaseView> {

    public V baseView;
    protected RetrofitService retrofitService = RetrofitHelper.getInstance().getServer();

    //存放RxJava中的订阅关系
    private CompositeDisposable mCompositeSubscription;

    public BasePresenter(V baseView) {
        this.baseView = baseView;
    }

    /**
     * 解除绑定
     */
    public void detachView() {
        baseView = null;
        onUnsubscribe();
    }

    /**
     * 返回 view
     */
   /*
   public V getBaseView() {
        return baseView;
    }*/

    /**
     * 添加订阅关系
     *
     * @param flowable
     * @param observer
     */
    public void addSubscription(Observable<?> flowable, BaseObserver observer) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        mCompositeSubscription.add(flowable//被观察者
                .subscribeOn(Schedulers.io())//定义事件在主线程消费
                .observeOn(AndroidSchedulers.mainThread())//定义请求事件发生在io线程
                .subscribeWith(observer));//使观察者订阅它
    }

    /**
     * RXjava取消订阅关系，以避免内存泄露
     */
    public void onUnsubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
        }
    }
}


