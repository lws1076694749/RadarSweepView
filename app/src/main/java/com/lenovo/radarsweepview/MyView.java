package com.lenovo.radarsweepview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 程序要点：
 * 1、整个过程是一个不断绘制的过程，所以我们需要一个Handler。
 * 2、核心的效果是一个扇形，所以我们还需要会绘制扇形。
 * 3、绘制的扇形的颜色有一个梯度变化的效果，所以我们需要知道Shader的使用。
 * 4、整个过程中扇形是不断变化位置的，我们还需要知道如何去旋转canvas。
 */
public class MyView extends View {
    private static final int MSG_RUN = 1;

    private Paint mCirclePaint; //绘制圆形
    private Paint mArcPaint;    //绘制扇形
    private Paint mLinePaint;   //绘制线条

    private RectF mRectF;
    private int mSweep;         //扇形角度

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RUN) {
                mSweep += 2;
                if (mSweep > 360)
                    mSweep = 0;
                postInvalidate();
                sendEmptyMessage(MSG_RUN);
            }
        }
    };

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(1.f);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(Color.GRAY);
        mArcPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(1.f);

        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
        mRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        //梯度变化的Shader,前两个参数是指梯度的圆心位置，后两个参数指的是梯度开始的颜色和结束的颜色
        mArcPaint.setShader(new SweepGradient(size / 2, size / 2, Color.GRAY, Color.BLACK));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        //旋转canvas，调用canvas.rotate方法，但是在旋转之前要保存现场，完毕之后要恢复现场
        canvas.save();
        canvas.rotate(mSweep, centerX, centerY);
        /**
         * 绘制扇形：drawArc( RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
         * 第一个参数是指的这个扇形所在圆的一个外接矩形
         * 第二个参数是扇形开始的位置，这里需要注意的是这个开始的位置是从坐标系的正东方向开始算起的
         * 第三个参数，指的是扇形的角度
         * 第四个参数如果设置false，绘制的是一段圆弧，true的话，绘制的才是一个实体的扇形。
         * */
        canvas.drawArc(mRectF, 0, mSweep, true, mArcPaint);
        canvas.restore();

        canvas.drawLine(0, centerY, getMeasuredWidth(), centerY, mLinePaint);
        canvas.drawLine(centerX, 0, centerX, getMeasuredHeight(), mLinePaint);

        canvas.drawCircle(centerX, centerY, centerX / 2, mCirclePaint);
        canvas.drawCircle(centerX, centerY, centerX, mCirclePaint);
    }

    public void start() {
        mHandler.sendEmptyMessage(MSG_RUN);
    }
}
