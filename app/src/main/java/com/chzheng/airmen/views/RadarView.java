package com.chzheng.airmen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.chzheng.airmen.R;
import com.chzheng.airmen.game.Coordinates;
import com.chzheng.airmen.game.Game;
import com.chzheng.airmen.game.Interceptor;
import com.chzheng.airmen.game.SerialEntity;
import com.chzheng.airmen.memos.UpdateMemo;

public class RadarView extends SurfaceView implements SurfaceHolder.Callback {
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
            final Paint backgroundPaint = new Paint(), backgroundPaint2 = new Paint(), framePaint = new Paint(), foregroundPaint = new Paint();
            backgroundPaint.setColor(getResources().getColor(R.color.colorBackground, null));
            backgroundPaint2.setColor(Color.BLACK);
            framePaint.setColor(getResources().getColor(R.color.colorAccent, null));
            framePaint.setStyle(Paint.Style.STROKE);
            foregroundPaint.setColor(Color.WHITE);
            //Handler registration
            mHandler = new Handler(getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.obj instanceof UpdateMemo) {
                        final UpdateMemo memo = (UpdateMemo) msg.obj;
                        final Canvas canvas = getHolder().lockCanvas();
                        //Draw radar frame
                        framePaint.setStrokeWidth((canvas.getWidth() + canvas.getHeight()) / 200);
                        canvas.drawPaint(backgroundPaint);
                        canvas.drawCircle(
                                canvas.getWidth() / 2,
                                canvas.getHeight() / 2,
                                (canvas.getWidth() - framePaint.getStrokeWidth()) / 2,
                                backgroundPaint2
                        );
                        //Draw concentric circles
                        final int circles = 4, radiusInterval = (canvas.getWidth() - (int) framePaint.getStrokeWidth()) / (circles * 2);
                        for (int i = circles; i > 0; i--) {
                            canvas.drawCircle(
                                    canvas.getWidth() / 2,
                                    canvas.getHeight() / 2,
                                    radiusInterval * i,
                                    framePaint
                            );
                        }
                        //Draw azimuth markings
                        canvas.save();
                        for(int i = 0; i < 6; i++) {
                            canvas.drawLine(framePaint.getStrokeWidth(), canvas.getHeight() / 2, canvas.getWidth() - framePaint.getStrokeWidth(), canvas.getHeight() / 2, framePaint);
                            canvas.rotate(30, canvas.getWidth() / 2, canvas.getHeight() / 2);
                        }
                        canvas.restore();
                        //Draw entities
                        canvas.save();
                        canvas.translate(0, canvas.getHeight());
                        canvas.scale(1, -1);
                        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
                        canvas.rotate(memo.bearing);
                        Coordinates center = memo.coordinates;
                        for (SerialEntity entity : memo.entities) {
                            if (entity.getType().equals(Interceptor.class.getName()) && Coordinates.distanceBetween(center, entity.getPosition()) < 4) {
                                Coordinates relativePosition = Coordinates.relativePosition(center, entity.getPosition());
                                canvas.drawCircle(
                                        (float) relativePosition.getLongitude() * radiusInterval,
                                        (float) relativePosition.getLatitude() * radiusInterval,
                                        canvas.getWidth() / 40,
                                        foregroundPaint
                                );
                            }
                        }
                        canvas.restore();
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                    return false;
                }
            });
        }

        public Handler getHandler() { return mHandler; }
    }
}