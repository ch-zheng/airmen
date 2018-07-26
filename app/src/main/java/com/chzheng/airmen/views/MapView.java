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

public class MapView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MapView";
    private DrawThread mDrawThread = new DrawThread();
    private boolean mIsCreated = false;

    public MapView(Context context, AttributeSet attrs) {
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
            //Canvas stuff
            final Paint backgroundPaint = new Paint(), foregroundPaint = new Paint();
            backgroundPaint.setColor(Color.WHITE);
            foregroundPaint.setColor(Color.RED);
            final int[] elevationColors = {
                    0xFFC8E6C9, 0xFFA5D6A7, 0xFF81C784,
                    0xFF66BB6A, 0xFF4CAF50, 0xFF43A047,
                    0xFF388E3C, 0xFF2E7D32, 0xFF1B5E20
            };
            //Handler registration
            mHandler = new Handler(getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.obj instanceof UpdateMemo) {
                        final UpdateMemo memo = (UpdateMemo) msg.obj;
                        final Canvas canvas = getHolder().lockCanvas();
                        canvas.drawPaint(backgroundPaint);
                        final float latitudeLength = canvas.getHeight() / memo.elevationTable.length;
                        final float longitudeLength = canvas.getWidth() / memo.elevationTable[0].length;
                        for (int y = 0; y < memo.elevationTable.length; y++) {
                            for (int x = 0; x < memo.elevationTable[0].length; x++) {
                                final Paint elevationPaint = new Paint();
                                final int colorIndex = memo.elevationTable[y][x] / 100;
                                elevationPaint.setColor(elevationColors[colorIndex < elevationColors.length ? colorIndex : elevationColors.length - 1]);
                                canvas.drawRect(
                                        x * longitudeLength, y * latitudeLength,
                                        x * longitudeLength + longitudeLength, y * latitudeLength + latitudeLength,
                                        elevationPaint
                                );
                            }
                        }
                        canvas.save();
                        canvas.translate(0, canvas.getHeight());
                        canvas.scale(1, -1);
                        canvas.drawCircle(memo.longitude * longitudeLength, memo.latitude * latitudeLength, latitudeLength / 2, foregroundPaint);
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