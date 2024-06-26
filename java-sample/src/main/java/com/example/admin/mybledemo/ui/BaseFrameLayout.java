package com.example.admin.mybledemo.ui;

import android.content.Context;


import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


public abstract class BaseFrameLayout extends FrameLayout {
    protected View mView;

    public BaseFrameLayout( Context context) {
        super(context);
        bindLayout(context, null, 0);
    }

    public BaseFrameLayout( Context context,  AttributeSet attrs) {
        super(context, attrs);
        bindLayout(context, attrs, 0);
    }

    public BaseFrameLayout( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindLayout(context, attrs, defStyleAttr);
    }

    protected void bindLayout(Context context, AttributeSet attrs, int defStyleAttr){
        mView = LayoutInflater.from(context).inflate(layoutId(), this, true);
        bindData();
        bindListener();
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}


}
