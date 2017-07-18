package com.zcn.arcprogressbar;

/**
 * Created by Administrator on 2017/6/28.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ArcProgressBar extends View {

    private Paint outerPaint;
    private Paint fgPaint;
    private Paint innerPaint;
    private float startAngle = 135.0f;//弧度开始点
    private float sweepAngle = 225.0f;
    private float arcAcrossAngle = 270.0f;//整个弧度跨越的度数
    private float bgThickness = 30;//背景弧形条的厚度，即bgPaint的粗度
    private float fgThickness = 26;
    /** The width of the view */
    private int width;
    /** The height of the view */
    private int height;
    /** The circle's center X coordinate */
    private float cx;
    /** The circle's center Y coordinate */
    private float cy;
    /** The radius of the outer circle */
    float outerRadius;
    /** The radius of the inner circle */
    private float innerRadius;
    /** The maximum progress amount */
    private int maxProgress = 5;
    //private int progress = 3;
    float left=20.0f;//Rect 左X坐标
    float right=420.0f;//Rect 右X坐标
    float top=20f;
    float bottom=420f;
    int progress = 3;
    int i = 0;

    int[] colors;
    Matrix mMatrix;
    private double markerPointX;
    private double markerPointY;

    //private float angleUnit = 180.0f / 256.0f;
    private float angleUnit  = arcAcrossAngle /maxProgress;;
    private RectF rect;
    public Point centerPoint = null;//弧形的圆心点
    //public float radius = 0;//弧形的半径
    public double degree;//当前的进度所对应的角度
    private Context context ;

    private Handler handler;

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ArcProgressBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setMainHandler(Handler handler){
        this.handler = handler;
    }

    /**
     * Gets the max progress.
     *
     * @return the max progress
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the max progress.
     *
     * @param maxProgress
     *            the new max progress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        angleUnit = arcAcrossAngle /maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {

        this.progress = progress;
    }

    private void init() {
        //背景Paint
        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setStyle(Style.STROKE);
        outerPaint.setStrokeWidth(bgThickness);
        outerPaint.setColor(Color.WHITE);
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
        outerPaint.setMaskFilter(blurMaskFilter);

        //绘制内圆
        innerPaint = new Paint();
        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setStyle(Paint.Style.FILL);
        colors = new int[]{Color.GREEN,Color.BLUE,Color.RED,Color.BLUE,Color.GREEN};
        mMatrix = new Matrix();

        //前景Paint
        fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setStyle(Style.STROKE);
        fgPaint.setStrokeWidth(fgThickness);
        fgPaint.setColor(Color.parseColor("#71dca2"));
        fgPaint.setStrokeCap(Paint.Cap.ROUND);
        BlurMaskFilter blurMaskFilter3 = new BlurMaskFilter(1, Blur.OUTER);
        fgPaint.setMaskFilter(blurMaskFilter3);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        if(height>250){
            height=250;
        }
        setMeasuredDimension(width, height);*/
        width = getWidth(); // Get View Width
        height = getHeight();// Get View Height

        int size = (width > height) ? height : width;

        cx = width / 2; // Center X for circle
        cy = height / 2; // Center Y for circle
        //modify by chunna.zheng at 2017-06-30c
        //this attribute can control is the circle is out of layout
        outerRadius = (size / 2)-50; // Radius of the outer circle / 2;
        innerRadius = outerRadius - bgThickness/2;//outRadious是向内外扩展

        left = cx - outerRadius; // Calculate left bound of our rect
        right = cx + outerRadius;// Calculate right bound of our rect
        top = cy - outerRadius;// Calculate top bound of our rect
        bottom = cy + outerRadius;// Calculate bottom bound of our rect

        //在此矩形中画弧形进度条
        rect = new RectF(left, top, right, bottom);
        centerPoint = new Point((int)cx, (int)cy);

        markerPointX = cx + outerRadius * Math.cos((progress*angleUnit+startAngle) * 3.14 / 180);
        markerPointY = cy + outerRadius * Math.sin((progress*angleUnit+startAngle) * 3.14 / 180);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("onSizeChanged");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		/*
		 * startAngle  开始角度
		 * sweepAngle  跨越角度
		 * false  不画中心和弧线的连线
		 */
        canvas.drawCircle(cx, cy, outerRadius, outerPaint);

        Shader shader = new SweepGradient(cx,cy,colors,null);
        mMatrix.setRotate((float) degree, cx, cy);
        shader.setLocalMatrix(mMatrix);
        innerPaint.setShader(shader);
        BlurMaskFilter blurMaskFilter2 = new BlurMaskFilter(1, Blur.OUTER);
        innerPaint.setMaskFilter(blurMaskFilter2);

        canvas.drawCircle(cx, cy, innerRadius, innerPaint);
        drawFg(canvas);
        canvas.drawCircle((float)markerPointX,(float)markerPointY, 20, innerPaint);
    }

    /**
     * 画Foreground
     * @param canvas
     */
    private void drawFg(Canvas canvas) {
        for(i=0;i< progress;i++){
            canvas.drawArc(rect, startAngle + angleUnit * i, angleUnit, false, fgPaint);
        }
    }

    //float coordinateUnit=256/(right-left);
    float x=0;
    float y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();

                if(!(x>=left&&x<=right&&y>=top&&y<=(top+outerRadius))){
                    return true;
                }

                degree = Math.toDegrees(Math.atan((y - centerPoint.y)
                        / (x - centerPoint.x)));
                if(degree>=0){
                    progress =(int) Math.round(degree/angleUnit);
                }else{
                    progress =(int) Math.round((arcAcrossAngle +degree)/angleUnit);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                x=event.getX();
                y = event.getY();
                if(!(x>=left&&x<=right&&y>=top&&y<=(top+outerRadius))){
                    return true;
                }

                degree = Math.toDegrees(Math.atan((y - centerPoint.y)
                        / (x - centerPoint.x)));
                if(degree>=0){
                    progress =(int) Math.round(degree/angleUnit);
                    markerPointX = cx + outerRadius * Math.cos((progress*angleUnit+startAngle) * 3.14 / 180);
                    markerPointY = cy + outerRadius * Math.sin((progress*angleUnit+startAngle) * 3.14 / 180);
                }else{
                    progress = (int) Math.round((arcAcrossAngle +degree)/angleUnit);
                    markerPointX = cx + outerRadius * Math.cos((progress*angleUnit+startAngle) * 3.14 / 180);
                    markerPointY = cy + outerRadius * Math.sin((progress*angleUnit+startAngle) * 3.14 / 180);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Chunna.zheng", "=====ACTION_UP=====");
                break;
        }

        return true;
    }
}