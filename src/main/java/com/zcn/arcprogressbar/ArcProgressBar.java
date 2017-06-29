package com.zcn.arcprogressbar;

/**
 * Created by Administrator on 2017/6/28.
 */
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ArcProgressBar extends View {

    private Paint outerPaint;
    private Paint fgPaint;
    private Paint innerPaint;
    private float startAngle = 180.0f;
    private float sweepAngle = 180.0f;
    private float bgThickness = 30;//北京弧形条的厚度，即bgPaint的粗度
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

    //private float angleUnit = 180.0f / 256.0f;
    private float angleUnit  = 180/maxProgress;;
    private RectF rect;



    public Point centerPoint = null;//弧形的圆心点
    //public float radius = 0;//弧形的半径
    public double degree;//当前的进度所对应的角度

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcProgressBar(Context context) {
        super(context);
        init();
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
        angleUnit = 180/maxProgress;
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
        outerPaint.setColor(Color.GRAY);
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
        outerPaint.setMaskFilter(blurMaskFilter);
        //绘制内圆
        innerPaint = new Paint();
        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setStyle(Paint.Style.FILL);
        innerPaint.setColor(Color.BLACK);
        BlurMaskFilter blurMaskFilter2 = new BlurMaskFilter(1, Blur.OUTER);
        innerPaint.setMaskFilter(blurMaskFilter2);

        //前景Paint
        fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setStyle(Style.STROKE);
        fgPaint.setStrokeWidth(fgThickness);
        fgPaint.setColor(Color.parseColor("#ff00ff00"));
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
        outerRadius = (size / 2)-30; // Radius of the outer circle / 2;
        innerRadius = outerRadius - bgThickness/2;//outRadious是向内外扩展
        Log.d("Chunna.zheng==", outerRadius + " " + bgThickness + " " + innerRadius);

        left = cx - outerRadius; // Calculate left bound of our rect
        right = cx + outerRadius;// Calculate right bound of our rect
        top = cy - outerRadius;// Calculate top bound of our rect
        bottom = cy + outerRadius;// Calculate bottom bound of our rect

        Log.d("Chunna.zheng", "请输出maxProgress的值：" + maxProgress);
        Log.d("Chunna.zheng", "请输出progress的值：" + progress);

        //在此矩形中画弧形进度条
        rect = new RectF(left, top, right, bottom);
        centerPoint = new Point((int)cx, (int)cy);
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
        canvas.drawCircle(cx, cy, innerRadius, innerPaint);
        drawFg(canvas);
    }

    /**
     * 画Foreground
     * @param canvas
     */
    private void drawFg(Canvas canvas) {
        Log.d("Chunna.zheng","开始绘画了");
        for(i=0;i< progress;i++){
            Log.d("Chunna.zheng","进入循环体："+i);
            //fgPaint.setColor(Color.rgb(i, 256 - i, 0));
            fgPaint.setColor(Color.parseColor("#ff33b5e5"));
            canvas.drawArc(rect, 180 + angleUnit * i, angleUnit, false, fgPaint);
        }

    }

    float coordinateUnit=256/(right-left);

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
                    progress =(int) Math.round((180+degree)/angleUnit);
                }
                Log.d("Chunna.zheng", "输出ACTION_DOWN中degree值：" + degree);
                Log.d("Chunna.zheng","输出ACTION_DOWN中angleUnit值："+angleUnit);
                Log.d("Chunna.zheng","输出ACTION_DOWN中progress值："+progress);
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
                }else{
                    progress = (int) Math.round((180+degree)/angleUnit);
                }
                Log.d("Chunna.zheng","输出ACTION_MOVE中degree值："+degree);
                Log.d("Chunna.zheng","输出ACTION_MOVE中angleUnit值："+angleUnit);
                Log.d("Chunna.zheng","输出ACTION_MOVE中progress值："+progress);
                invalidate();
                break;
        }

        return true;
    }
}

