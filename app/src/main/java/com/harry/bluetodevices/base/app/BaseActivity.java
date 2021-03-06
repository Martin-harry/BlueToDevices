package com.harry.bluetodevices.base.app;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.util.StatusBarUtils;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc activity基类
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createViews());//初始化视图
        initView();//初始化控件
        initData();//初始化数据
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int createViews();

    //设置状态栏颜色
    public void setStatusBg(int i){
        if(i == 1){
            StatusBarUtils.setWindowStatusBarColor(this, R.color.SplGreen);//设置状态栏颜色为绿色
        }else if(i == 2){
            StatusBarUtils.setWindowStatusBarColorText(this, R.color.white);//设置状态栏颜色为白色
        }else if(i == 3){
            StatusBarUtils.setWindowStatusBarColor(this, R.color.startCor);//设置状态栏颜色为橘橙色
        }else if(i == 4){
            StatusBarUtils.setWindowStatusBarColor(this, R.color.black);//设置状态栏颜色为黑色
        }
    }

}
