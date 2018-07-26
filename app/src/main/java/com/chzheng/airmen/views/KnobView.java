package com.chzheng.airmen.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chzheng.airmen.R;

public class KnobView extends View {
    private static final String TAG = "KnobView";
    private float mRotationAngle = 0; //Measured in degrees clockwise from north
    private Bitmap mDialImage = BitmapFactory.decodeResource(getResources(), R.drawable.circlearrow);
    private Paint mPaint = new Paint();

    public KnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    //FIXME, the picture is fucked up
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDialImage = Bitmap.createScaledBitmap(mDialImage, canvas.getWidth(), canvas.getHeight(), false);
        canvas.save();
        canvas.rotate(mRotationAngle, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawBitmap(mDialImage, 0, 0, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            final int[] center = new int[2];
            getLocationOnScreen(center);
            mRotationAngle = ((float) Math.toDegrees(Math.atan2(event.getY() - getHeight() / 2, event.getX() - getWidth() / 2)) + 450) % 360;
            invalidate();
            return true;
        } else return false;
    }

    public float getRotationAngle() { return mRotationAngle; }
}
