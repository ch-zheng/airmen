package com.chzheng.airmen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.chzheng.airmen.memos.UpdateMemo;

public class RadarView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MapView";
    private DrawThread mDrawThread = new DrawThread();
    private boolean mIsCreated = false;

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawThread.start();
        mIsCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawThread.quitSafely();
        mIsCreated = false;
    }

    public void setUpdate(UpdateMemo memo) {
        Message message = new Message();
        message.obj = memo;
        if (mIsCreated) mDrawThread.getHandler().sendMessage(message);
    }

    private class DrawThread extends HandlerThread {
        private Handler mHandler;

        public DrawThread() {
            super("Draw thread");
        }

        @Override
        protected void onLooperPrepared() {
            //Paint setup
            final Paint backgroundPaint = new Paint(), backgroundPaint2 = new Paint(), framePaint = new Paint(), accentPaint = new Paint();
            backgroundPaint.setColor(0xFFF5F5F5);
            backgroundPaint2.setColor(Color.BLACK);
            framePaint.setColor(Color.GREEN);
            framePaint.setStyle(Paint.Style.STROKE);
            accentPaint.setColor(Color.WHITE);
            //Handler registration
            mHandler = new Handler(getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.obj instanceof UpdateMemo) {
                        final UpdateMemo memo = (UpdateMemo) msg.obj;
                        //Canvas drawing
                        final Canvas canvas = getHolder().lockCanvas();
                        framePaint.setStrokeWidth((canvas.getWidth() + canvas.getHeight()) / 200);
                        canvas.drawPaint(backgroundPaint);
                        canvas.drawCircle(
                                canvas.getWidth() / 2,
                                canvas.getHeight() / 2,
                                (canvas.getWidth() - framePaint.getStrokeWidth()) / 2,
                                backgroundPaint2
                        );
                        canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, canvas.getHeight(), framePaint);
                        canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, framePaint);
                        final int circles = 4, radiusInterval = (canvas.getWidth() - (int) framePaint.getStrokeWidth()) / (circles * 2);
                        for (int i = circles; i > 0; i--) {
                            canvas.drawCircle(
                                    canvas.getWidth() / 2,
                                    canvas.getHeight() / 2,
                                    radiusInterval * i,
                                    framePaint
                            );
                        }
                        getHolder().unlockCanvasAndPost(canvas);
                        canvas.save();
                        canvas.translate(0, canvas.getHeight());
                        canvas.scale(1, -1);
                        canvas.rotate(memo.direction);
                        canvas.restore();
                    }
                    return false;
                }
            });
        }

        public Handler getHandler() { return mHandler; }
    }
}