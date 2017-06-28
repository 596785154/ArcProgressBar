package com.zcn.arcprogressbar;

/**
 * Created by Administrator on 2017/6/28.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MySeekBar extends ArcProgressBar {
    private Paint paint;

    private Point arcPoint = null;

    // private int drawableWidth = 10;
    // private int drawableHeight = 20;
    // private Point arcPoint = null;
    // private Point centerPoint = null;

    public MySeekBar(Context context) {
        super(context);
        init(context);

    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    /**
     * 初始化一些成员变量
     *
     * @param context
     */
    private void init(Context context) {

        // drawable = getResources().getDrawable(R.drawable.bg_seekbar_thumb);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(220, 115, 39));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(1.0f);
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.seek_thumb_normal);

        arcPoint = new Point((int) left, (int) (top + outerRadius));
        roateBitmap();
    }

    int bitmapWidth=0;//转动后的bitmap的宽
    int bitmapHeight=0;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawLine(20, 120, 220, 120, paint);
        if (arcPoint != null) {
//			canvas.save();
            bitmapWidth=bitmap2.getWidth();
            bitmapHeight=bitmap2.getHeight();
//			System.out.println(bitmapWidth+","+bitmapHeight);
            canvas.drawBitmap(bitmap2, arcPoint.x - bitmapWidth / 2,
                    arcPoint.y - bitmapHeight / 2, null);

            canvas.drawLine(centerPoint.x, centerPoint.y, arcPoint.x,
                    arcPoint.y, paint);
//			canvas.restore();

            if (bitmap2 != null) {
                bitmap2.recycle();
                bitmap2 = null;
            }

        }

    }

    private Bitmap bitmap;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                if (x < (arcPoint.x + bitmapWidth / 2)
                        && x > (arcPoint.x - bitmapWidth / 2)
                        && arcPoint.y > (arcPoint.y - bitmapHeight / 2)
                        && arcPoint.y < (arcPoint.y + bitmapHeight / 2)) {
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.seek_thumb_pressed);

                }
                setArcPoint(degree);
                roateBitmap();
                invalidate();

                // System.out.println("degree:" + degree);
                // invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                setArcPoint(degree);
                roateBitmap();
                break;
            case MotionEvent.ACTION_UP:

                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.mipmap.seek_thumb_normal);
                roateBitmap();
                invalidate();

                break;

        }
        return true;
    }

    private Bitmap bitmap2;
    Matrix matrix;

    /**
     * 选择bitmap
     */
    private void roateBitmap() {
        matrix = new Matrix();
        // matrix.postTranslate(arcPoint.x - drawableWidth / 2, arcPoint.y
        // - drawableHeight / 2);
        matrix.postRotate((float) degree - 90);
        // matrix.setRotate((float) degree-90, arcPoint.x, arcPoint.y);
        bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

    }

    /**
     * 计算出当前点击位置对应的弧线上的坐标点
     *
     * @param degree
     */
    public void setArcPoint(double degree) {
        int arcX;
        int arcY;
        double radians = Math.toRadians(degree);
        // System.out.println(radians);
        double incrementX = Math.cos(radians) * outerRadius;
        double incrementY = Math.sin(radians) * outerRadius;
        // System.out.println(incrementX+","+incrementY);
        if (degree > 0) {
            arcX = (int) (centerPoint.x - incrementX);
            arcY = (int) (centerPoint.y - incrementY);

        } else {
            arcX = (int) (centerPoint.x + incrementX);
            arcY = (int) (centerPoint.y + incrementY);
        }
        // System.out.println(arcX+","+arcY);
        arcPoint = new Point(arcX, arcY);
    }

    public Point getArcPoint() {
        return arcPoint;
    }
}
