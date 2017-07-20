package com.zcn.arcprogressbar;

/**
 * Created by Administrator on 2017/6/28.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by lenovo on 2017/7/15.
 */

public class ArcProgressBar extends ProgressBar{
    //弧度转角度
    private static final double RADIAN = 180 / Math.PI;
    //半径
    private int mRadius;
    //画弧的笔
    private Paint mArcPaint;
    //画背景颜色的笔
    private Paint mBackPaint;
    //弧尽头的小圆点
    private Paint mPointPaint;
    //小圆点的X坐标
    private float mPointX ;
    //小圆点的Y坐标
    private float  mPointY ;
    //小圆点的半径
    private float mPointRadius;
    //小圆点的颜色
    private int mPointColor;
    //弧线宽度
    private float mArcWidth;
    //弧线颜色
    private int mArcColor;
    //起始角度,绘制的时候会用到
    private int mStartAngle;
    //初始角度
    private int mCalculateStartAngle;
    //最大角度
    private int mMaxAngle;
    //当前的角度
    private float mCurrentAngle = 0;
    //当前进度值
    private int mCurrentProgerss;
    //最大进度值
    private int mMaxProgress;
    //全部的颜色
    private int mBackcolor;

    private OnProgressChangeListener mListener;


    public ArcProgressBar(Context context)
    {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyleAttr){
        initAttrs(attrs, defStyleAttr);
        initPaint();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleSeekBar, defStyleAttr, 0);
        mArcWidth = a.getDimension(R.styleable.CircleSeekBar_arc_width, 20);
        mArcColor = a.getColor(R.styleable.CircleSeekBar_arc_color, Color.BLUE);
        mBackcolor = a.getColor(R.styleable.CircleSeekBar_back_color,Color.WHITE);
        mPointRadius = a.getDimension(R.styleable.CircleSeekBar_point_radius, mArcWidth / 2);
        mPointColor = a.getColor(R.styleable.CircleSeekBar_point_color, Color.GREEN);
        mStartAngle = a.getInt(R.styleable.CircleSeekBar_start_angle, 135);
        mMaxAngle = a.getInt(R.styleable.CircleSeekBar_max_angle, 270);
        mCurrentProgerss = a.getInt(R.styleable.CircleSeekBar_current_progress, 0);
        mMaxProgress = a.getInt(R.styleable.CircleSeekBar_max_progress, 100);
        a.recycle();
        //处理角度偏差
        mCalculateStartAngle = mStartAngle % 90;
    }

    private void initPaint(){
        //初始化背景的笔
        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setColor(mBackcolor);
        mBackPaint.setStrokeWidth(mArcWidth);
        mBackPaint.setStyle(Paint.Style.STROKE);
        //mBackPaint.setStrokeCap(Paint.Cap.ROUND);
        //初始化弧形的笔
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        //mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        //初始化小圆点的笔
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
//        //取宽度和高度的最小值
//        int diameter=Math.min(width,height);
        setMeasuredDimension(width, height);

    }

    private int measureWidth(int widthMeasureSpec){
        int specSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int specMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int result;
        if (specMode == View.MeasureSpec.EXACTLY){
            result = specSize;
        } else {
            result = 30;
            if (specMode == View.MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec){
        return measureWidth(heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom()) / 2;

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        RectF reactF = new RectF(getPaddingLeft() + mArcWidth, getPaddingTop() + mArcWidth, getPaddingLeft() + 2 * mRadius - mArcWidth, getPaddingTop() + 2 * mRadius - mArcWidth);
        canvas.drawArc(reactF, mStartAngle, mMaxAngle, false, mBackPaint);
        canvas.drawArc(reactF, mStartAngle, mCurrentAngle, false, mArcPaint);
        if (mCurrentAngle >= 0){
            if (mPointX == 0){
                mPointX = calculatePointX(true, Math.sin((90 - (float) (90 - Math.asin((double) (321 - mRadius) / (Math.sqrt(Math.pow(94 - mRadius, 2) + Math.pow(321 - mRadius, 2)))) * RADIAN)) / RADIAN));
                mPointY = calculatePointY((double) (321 - mRadius) / Math.sqrt(Math.pow(94 - mRadius, 2) + Math.pow(321 - mRadius, 2)));;
            }
            canvas.drawCircle(mPointX, mPointY, mPointRadius, mPointPaint);
        }
    }
    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!isValid(x, y)){
                    return false;
                }
                calculateAngle(x, y);
                calculateProgress();
                break;
            case MotionEvent.ACTION_MOVE:
                calculateAngle(x, y);
                calculateProgress();
                break;
            case MotionEvent.ACTION_UP:
                calculateAngle(x, y);
                //添加粘性效果
                if (mCurrentAngle <= 5){
                    mCurrentAngle = 0;
                }
                calculateProgress();
                break;
        }
        invalidate();
        return true;
    }

    private void calculateAngle(float x, float y){
        float angle;
        //斜边
        double hypotenuse;
        hypotenuse = Math.sqrt(Math.pow(x - mRadius, 2) + Math.pow(y - mRadius, 2));
        double sin = (double) (y - mRadius) / hypotenuse;
        float pointX;
        float pointY;
        boolean isLeft = x - mRadius < 0;
        if (isLeft) {
            angle = (float) (90 - Math.asin(sin) * RADIAN);
            //计算小圆点坐标
            pointX = calculatePointX(isLeft, sin);
            pointY = calculatePointY(sin);
            Log.d("c_angle", "left:" + angle);
        } else {
            angle = (float) (180 + 90 + Math.asin(sin) * RADIAN);
            //计算小圆点坐标
            pointX = calculatePointX(isLeft, sin);
            pointY = calculatePointY(sin);
            Log.d("c_angle", "right:" + angle);
        }
        if (angle >= mCalculateStartAngle && angle <= mMaxAngle + mCalculateStartAngle){
            mCurrentAngle = Math.round(angle - mCalculateStartAngle);
            Log.d("cur_angle", "mCurrentAngle:" + mCurrentAngle);
            mPointX = pointX;
            mPointY = pointY;
        }
    }

    /**
     * 计算小圆点的X坐标
     *
     * @param isLeft 判断点是否位于弧形的左半部分
     * @param sin    #calculateAngle计算出来的sin值
     * @return 小圆点的X坐标
     */
    private float calculatePointX(boolean isLeft, double sin){
        return isLeft ? (float) (mRadius - (mRadius - mArcWidth) * Math.sqrt(1 - sin * sin)) + getPaddingLeft() :
                (float) (mRadius + (mRadius - mArcWidth) * Math.sqrt(1 - sin * sin)) + getPaddingLeft();
    }

    private float calculatePointY(double sin){
        return (float) (mRadius + (mRadius - mArcWidth) * sin) + getPaddingTop();
    }

    private void calculateProgress(){
        mCurrentProgerss = Math.round(mCurrentAngle / mMaxAngle * mMaxProgress);
        if (mListener != null)
        {
            mListener.onProgress(mCurrentProgerss);
        }
    }

    /**
     * 判断点是否在弧形的半径内
     *
     * @param x
     * @param y
     * @return true在   false不在
     */
    private boolean isValid(float x, float y){
        return Math.pow(x - mRadius - getPaddingLeft(), 2) + Math.pow(y - mRadius - getPaddingTop(), 2) <= mRadius * mRadius;
    }

    public void setCurrentProgress(int progress){
        if (progress > mMaxProgress)
        {
            throw new IllegalArgumentException("progress must < mMaxProgress");
        }
        mCurrentProgerss = progress;
        mCurrentAngle = (float) mCurrentProgerss / mMaxProgress * mMaxAngle;
        float angle = mCurrentAngle + mCalculateStartAngle;
        //与calculateAngle方法求sin值相反
        boolean isLeft = angle <= 180;
        if (isLeft)
        {
            mPointX = calculatePointX(isLeft, Math.sin((90 - angle) / RADIAN));
            mPointY = calculatePointY(Math.sin((90 - angle) / RADIAN));
        } else
        {
            mPointX = calculatePointX(isLeft, Math.sin((angle - 180 - 90) / RADIAN));
            mPointY = calculatePointY(Math.sin((angle - 180 - 90) / RADIAN));
        }
        invalidate();
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener){
        this.mListener = listener;
    }

    public interface OnProgressChangeListener{
        void onProgress(int progress);
    }
}