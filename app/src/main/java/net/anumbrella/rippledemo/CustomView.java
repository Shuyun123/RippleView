package net.anumbrella.rippledemo;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * author：anumbrella
 * Date:17/3/4 上午10:57
 */

public class CustomView extends RelativeLayout {

    public CustomView(Context context) {
        super(context);
        Log.i("anumbrella","constructor1");
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i("anumbrella","constructor2");
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("anumbrella","constructor3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.i("anumbrella","constructor3");
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i("anumbrella","onAttachedToWindow()");
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("anumbrella","onConfigurationChanged()");
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.i("anumbrella","dispatchDraw()");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("anumbrella","onDraw()");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.i("anumbrella","draw()");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("anumbrella","onLayout()");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("anumbrella","onMeasure()");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("anumbrella", "onSizeChanged() " + " w = " + w + "  h = " + h + "  oldW = " + oldw + "  oldH = " + oldw);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("anumbrella","onTouchEvent()");
        return super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }


}
