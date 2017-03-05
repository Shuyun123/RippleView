package net.anumbrella.ripplelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

/**
 * author：anumbrella
 * Date:17/2/27 下午5:05
 * <p>
 * 自定义水波纹控件学习(代码来自https://github.com/traex/RippleEffect)
 */

public class RippleView extends RelativeLayout {


    /**
     * 水波纹的颜色
     */
    private int rippleColor;


    /**
     * 水波纹扩散类型
     */
    private Integer rippleType;

    /**
     * 放大持续时间
     */
    private int zoomDuration;

    /**
     * 放大比例
     */
    private float zoomScale;

    /**
     * 放大动画类
     */
    private ScaleAnimation scaleAnimation;


    /**
     * 视图是否放大
     */
    private Boolean hasToZoom;

    /**
     * 是否从视图中心开始动画
     */
    private Boolean isCentered;


    /**
     * 帧速率
     */
    private int frameRate = 10;

    /**
     * 水波纹持续时间
     */
    private int rippleDuration = 400;


    /**
     * 水波纹透明度
     */
    private int rippleAlpha = 90;


    /**
     * canvas画布执行Handler
     */
    private Handler canvasHandler;

    /**
     * 水波纹画笔
     */
    private Paint paint;


    /**
     * 水波纹扩散内边距
     */
    private int ripplePadding;


    /**
     * 手势监听类
     */
    private GestureDetector gestureDetector;


    /**
     * 水波纹动画是否开始
     */
    private boolean animationRunning = false;


    /**
     * 时间统计
     */
    private int timer = 0;


    /**
     * 时间间隔
     */
    private int timerEmpty = 0;

    /**
     * 水波纹持续时间间隔
     */
    private int durationEmpty = -1;


    /**
     * 最大圆半径
     */
    private float radiusMax = 0;


    /**
     * 水波纹圆的坐标点
     */
    private float x = -1;
    private float y = -1;


    private Bitmap originBitmap;

    private OnRippleCompleteListener onCompletionListener;


    /**
     * 视图的宽和高
     */
    private int WIDTH;

    private int HEIGHT;


    /**
     * 定义水波纹类型
     */
    public enum RippleType {
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2);

        int type;

        RippleType(int type) {
            this.type = type;
        }
    }


    /**
     * 水波纹更新波纹Runnable
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };


    /**
     * 定义回调函数，当水波纹效果完成时调用
     */
    public interface OnRippleCompleteListener {
        void onComplete(RippleView rippleView);
    }


    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化方法
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, getResources().getColor(R.color.rippelColor));
        rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0);
        hasToZoom = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false);
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false);
        rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, rippleDuration);
        rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha);
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0);
        canvasHandler = new Handler();
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03f);
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 200);
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        paint.setAlpha(rippleAlpha);
        //使onDraw方法可以调用，以便被我们重写
        this.setWillNotDraw(false);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        //开启cache来绘制视图
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }

    /**
     * 绘制水波纹
     *
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (animationRunning) {
            canvas.save();
            if (rippleDuration <= timer * frameRate) {
                animationRunning = false;
                timer = 0;
                durationEmpty = -1;
                timerEmpty = 0;
                //android 23 会自动调用canvas.restore()；
                if (Build.VERSION.SDK_INT != 23) {
                    canvas.restore();
                }
                invalidate();
                if (onCompletionListener != null) {
                    onCompletionListener.onComplete(this);
                }
                return;
            } else {
                canvasHandler.postDelayed(runnable, frameRate);
            }

            if (timer == 0) {
                canvas.save();
            }

            canvas.drawCircle(x, y, (radiusMax * (((float) timer * frameRate) / rippleDuration)), paint);
            paint.setColor(Color.parseColor("#ffff4444"));


            if (rippleType == 1 && originBitmap != null && (((float) timer * frameRate) / rippleDuration) > 0.4f) {
                if (durationEmpty == -1) {
                    durationEmpty = rippleDuration - timer * frameRate;
                }
                timerEmpty++;
                final Bitmap tmpBitmap = getCircleBitmap((int) ((radiusMax) * (((float) timerEmpty * frameRate) / (durationEmpty))));
                canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                tmpBitmap.recycle();
            }
            paint.setColor(rippleColor);

            if (rippleType == 1) {
                if ((((float) timer * frameRate) / rippleDuration) > 0.6f) {
                    paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timerEmpty * frameRate) / (durationEmpty)))));
                } else {
                    paint.setAlpha(rippleAlpha);
                }
            } else {
                paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timer * frameRate) / rippleDuration))));
            }
            timer++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, w / 2, h / 2);
        scaleAnimation.setDuration(zoomDuration);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
    }


    /**
     * 启动水波纹动画，通过MotionEvent事件
     *
     * @param event
     */
    public void animateRipple(MotionEvent event) {
        createAnimation(event.getX(), event.getY());
    }

    /**
     * 启动水波纹动画，通过x，y坐标
     *
     * @param x
     * @param y
     */
    public void animateRipple(final float x, final float y) {
        createAnimation(x, y);
    }


    private void createAnimation(final float x, final float y) {
        if (this.isEnabled() && !animationRunning) {
            if (hasToZoom) {
                this.startAnimation(scaleAnimation);
            }

            radiusMax = Math.max(WIDTH, HEIGHT);

            if (rippleType != 2) {
                radiusMax /= 2;
            }

            radiusMax -= ripplePadding;

            if (isCentered || rippleType == 1) {
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            } else {
                this.x = x;
                this.y = y;
            }

            animationRunning = true;

            if (rippleType == 1 && originBitmap == null) {
                originBitmap = getDrawingCache(true);
            }
            invalidate();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            animateRipple(event);
            sendClickEvent(false);
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }


    /**
     * 发送一个点击事件，如果父视图是ListView实例
     *
     * @param isLongClick
     */
    private void sendClickEvent(final Boolean isLongClick) {
        if (getParent() instanceof AdapterView) {
            final AdapterView adapterView = (AdapterView) getParent();
            final int position = adapterView.getPositionForView(this);
            final long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick) {
                if (adapterView.getOnItemLongClickListener() != null) {
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
                }
            } else {
                if (adapterView.getOnItemClickListener() != null) {
                    adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
                }
            }
        }
    }


    /**
     * 设置水波纹的颜色
     *
     * @param rippleColor
     */
    public void setRippleColor(int rippleColor) {
        this.rippleColor = getResources().getColor(rippleColor);
    }

    public int getRippleColor() {
        return rippleColor;
    }

    public RippleType getRippleType() {
        return RippleType.values()[rippleType];
    }


    /**
     * 设置水波纹动画类型，默认为RippleType.SIMPLE
     *
     * @param rippleType
     */
    public void setRippleType(final RippleType rippleType) {
        this.rippleType = rippleType.ordinal();
    }

    public Boolean isCentered() {
        return isCentered;
    }

    /**
     * 设置水波纹动画是否开始从父视图中心开始，默认为false
     *
     * @param isCentered
     */
    public void setCentered(final Boolean isCentered) {
        this.isCentered = isCentered;
    }

    public int getRipplePadding() {
        return ripplePadding;
    }

    /**
     * 设置水波纹内边距，默认为0dip
     *
     * @param ripplePadding
     */
    public void setRipplePadding(int ripplePadding) {
        this.ripplePadding = ripplePadding;
    }

    public Boolean isZooming() {
        return hasToZoom;
    }

    /**
     * 在水波纹结束后，是否有放大动画，默认为false
     *
     * @param hasToZoom
     */
    public void setZooming(Boolean hasToZoom) {
        this.hasToZoom = hasToZoom;
    }

    public float getZoomScale() {
        return zoomScale;
    }

    /**
     * 设置放大动画比例
     *
     * @param zoomScale
     */
    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public int getZoomDuration() {
        return zoomDuration;
    }

    /**
     * 设置放大动画持续时间，默认为200ms
     *
     * @param zoomDuration
     */
    public void setZoomDuration(int zoomDuration) {
        this.zoomDuration = zoomDuration;
    }

    public int getRippleDuration() {
        return rippleDuration;
    }

    /**
     * 设置水波纹动画持续时间，默认为400ms
     *
     * @param rippleDuration
     */
    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }

    public int getFrameRate() {
        return frameRate;
    }

    /**
     * 设置水波纹动画的帧速率，默认为10
     *
     * @param frameRate
     */
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getRippleAlpha() {
        return rippleAlpha;
    }

    /**
     * 设置水波纹动画的透明度，默认为90，取值为0到255之间
     *
     * @param rippleAlpha
     */
    public void setRippleAlpha(int rippleAlpha) {
        this.rippleAlpha = rippleAlpha;
    }


    public void setOnRippleCompleteListener(OnRippleCompleteListener listener) {
        this.onCompletionListener = listener;
    }


    /**
     * 绘制扩散背景范围视图bitmap
     *
     * @param radius
     * @return
     */
    private Bitmap getCircleBitmap(final int radius) {
        final Bitmap output = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect((int) (x - radius), (int) (y - radius), (int) (x + radius), (int) (y + radius));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(x, y, radius, paint);
        //出来两图交叉情况
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, rect, rect, paint);
        return output;
    }


}
