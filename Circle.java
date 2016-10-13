package com.john.neteasenews.splash.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by John on 2016/10/13.
 */
public class Circle extends SurfaceView implements SurfaceHolder.Callback {

    private OnCircleAnimationListener mListener;

    private int mWidth;
    private int mHeight;
    private SurfaceHolder mHolder;
    private static final String TEXT_CIRCLE = "跳过";

    private int mDegree = 0;

    private long mDelay = 3000; // 默认运行时间3秒
    private boolean mStop = false;
    private Paint mClearPaint;

    public Circle(Context context) {
        super(context);

        //
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        // 下面的代码会能够去除掉圆圈之外的黑色背景
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(100, 100);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 获取宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mListener != null) {
            mListener.onAnimationPrepared(Circle.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e("John", "Circle" + " # " + "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public void setCircleAnimationListener(OnCircleAnimationListener listener) {
        mListener = listener;
    }

    public void startCircleAnimation() {
        new DrawThread().start();
    }

    public void setAnimationDelay(long delay) {
        mDelay = delay;
    }

    public void stopAnimation() {
        mStop = true;
    }

    public class DrawThread extends Thread {

        private Paint mPaint;
        private Paint mTextPaint;
        private Paint mArcPaint;

        public DrawThread() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mTextPaint = new TextPaint();
            mTextPaint.setTextSize(mHeight / 2-5);
            mTextPaint.setColor(Color.WHITE);
            mPaint.setColor(Color.RED);
            mArcPaint = new Paint();
            mArcPaint.setStrokeWidth((float) (mWidth / 2 * 0.1));
            mArcPaint.setColor(Color.BLUE);
            mArcPaint.setAntiAlias(true);
            mArcPaint.setStyle(Paint.Style.STROKE);

            //这是定义橡皮擦画笔
            mClearPaint = new Paint();
            mClearPaint.setAntiAlias(true);
            mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        }

        @Override
        public void run() {
            mListener.onAnimationStart(Circle.this);
            while (!mStop) {
                drawCircle();
                if (mDegree >= 360) {
                    mListener.onAnimationFinished(Circle.this);
                    break;
                }
                // 把总的动画时间分成360份
                long animation_duration = mDelay / 360;
                SystemClock.sleep(animation_duration);
                mDegree++;
            }
        }

        // 绘制圆圈
        private void drawCircle() {
            mListener.onAnimationRunning(Circle.this);
            Canvas canvas = mHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawRect(0, 0, mWidth, mHeight, mClearPaint);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            canvas.save();
            canvas.scale(0.9f, 0.9f, mWidth * 1f / 2, mHeight * 1f / 2);
            canvas.drawCircle(mWidth * 1.0f / 2, mHeight * 1.0f / 2, mWidth * 1.0f / 2, mPaint);
            float centerY = mHeight * 1f / 2;
            // 这个是获取每个文字的宽度，那么怎么知道有几个文字呢?
            float widths[] = new float[2];
            mTextPaint.getTextWidths(TEXT_CIRCLE, widths);
            canvas.drawText(TEXT_CIRCLE, 5, centerY + (widths[0] * 1f / 2) - 10, mTextPaint);
            canvas.restore();

            // 旋转坐标画圆圈
            canvas.save();
            canvas.rotate(-90, mWidth / 2, mHeight / 2);
            RectF rectF = new RectF((float) (mWidth / 2 * 0.1) / 2, (float) (mWidth / 2 * 0.1) / 2, mWidth - (float) (mWidth / 2 * 0.1) / 2, mHeight - (float) (mWidth / 2 * 0.1) / 2);
            canvas.drawArc(rectF, 0, mDegree, false, mArcPaint);
            canvas.restore();

            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    public interface OnCircleAnimationListener {
        void onAnimationPrepared(View view);

        void onAnimationFinished(View view);

        void onAnimationRunning(View view);

        void onAnimationStart(View view);
    }

}
