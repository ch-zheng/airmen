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
            final Paint backgroundPaint = new Paint(), foregroundPaint = new Paint(), textPaint = new Paint();
            backgroundPaint.setColor(Color.BLACK);
            foregroundPaint.setColor(Color.RED);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
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
                        //FIXME: 4/3 is a magical number that makes things work.
                        //FIXME CONTINUED: It cannot be saved as a variable, it must be typed out as '4/3'.
                        final UpdateMemo memo = (UpdateMemo) msg.obj;
                        final Canvas canvas = getHolder().lockCanvas();
                        canvas.drawPaint(backgroundPaint);
                        final float latitudeLength = canvas.getHeight() / (memo.elevationTable.length + 2);
                        final float longitudeLength = canvas.getWidth() / (memo.elevationTable[0].length + 2);
                        canvas.save();
                        canvas.translate(longitudeLength * 4/3, latitudeLength);
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
                        canvas.restore();
                        //Draw coordinate markings
                        textPaint.setTextSize(latitudeLength);
                        for (int i = 0; i <= memo.elevationTable[0].length; i += 2) {
                            //Longitude markings
                            canvas.drawText(String.valueOf(i), i * longitudeLength + longitudeLength * 4/3, canvas.getHeight() - latitudeLength / 3, textPaint);
                        }
                        canvas.save();
                        canvas.rotate(270, canvas.getWidth() / 2, canvas.getHeight() / 2);
                        for (int i = 0; i <= memo.elevationTable.length; i+= 2) {
                            //Latitude markings
                            canvas.drawText(String.valueOf(i), i * latitudeLength + latitudeLength * 4/3, longitudeLength, textPaint);
                        }
                        canvas.restore();
                        //Draw player position
                        canvas.save();
                        canvas.translate(0, canvas.getHeight());
                        canvas.scale(1, -1);
                        canvas.translate(longitudeLength * 4/3, latitudeLength * 4/3);
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