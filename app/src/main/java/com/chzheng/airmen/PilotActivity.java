package com.chzheng.airmen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.chzheng.airmen.databinding.ActivityPilotBinding;
import com.chzheng.airmen.memos.PilotMemo;
import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.memos.UpdateMemo;
import com.chzheng.airmen.networking.ClientSender;
import com.chzheng.airmen.views.KnobView;

public class PilotActivity extends AppCompatActivity {
    private static final String TAG = "Pilot Activity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private ActivityPilotBinding mBinding;
    private long mLastUpdateTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pilot);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.pilot);
        //Register Handler
        sHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof UpdateMemo) {
                    final UpdateMemo memo = (UpdateMemo) msg.obj;
                    if (System.currentTimeMillis() - mLastUpdateTime > 100) {
                        mBinding.setUpdate(memo);
                        mLastUpdateTime = System.currentTimeMillis();
                    }
                } else if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case SHUTDOWN:
                            startActivity(new Intent(PilotActivity.this, ReviewActivity.class));
                            break;
                    }
                }
                return false;
            }
        });
        findViewById(R.id.slider_throttle).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((TextView) findViewById(R.id.indicator_throttle)).setText(
                        String.valueOf(((SeekBar) findViewById(R.id.slider_throttle)).getProgress())
                );
                return false;
            }
        });
        findViewById(R.id.slider_altitude).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((TextView) findViewById(R.id.indicator_set_altitude)).setText(
                        String.valueOf(((SeekBar) findViewById(R.id.slider_altitude)).getProgress())
                );
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Message message = Message.obtain();
        message.obj = new PilotMemo(
                ((SeekBar) findViewById(R.id.slider_throttle)).getProgress(),
                ((SeekBar) findViewById(R.id.slider_altitude)).getProgress(),
                (int) ((KnobView) findViewById(R.id.knob_direction)).getRotationAngle(),
                ((Switch) findViewById(R.id.toggle_engines)).isChecked(),
                ((Switch) findViewById(R.id.toggle_landing_gear)).isChecked()
        );
        ClientSender.sHandler.sendMessage(message);
        return super.dispatchTouchEvent(ev);
    }
}
