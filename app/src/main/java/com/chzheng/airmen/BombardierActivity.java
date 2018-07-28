package com.chzheng.airmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chzheng.airmen.memos.BombardierMemo;
import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.memos.UpdateMemo;
import com.chzheng.airmen.networking.ClientSender;
import com.chzheng.airmen.views.KnobView;

import java.util.ArrayList;

public class BombardierActivity extends AppCompatActivity {
    private static final String TAG = "Bombardier Activity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private int mCurrentRotationAngle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bombardier);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.bombardier);
        //Register Handler
        sHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof UpdateMemo) {
                    final UpdateMemo memo = (UpdateMemo) msg.obj;
                } else if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case SHUTDOWN:
                            startActivity(new Intent(BombardierActivity.this, ReviewActivity.class));
                            break;
                    }
                }
                return false;
            }
        });
        //Knob touch event
        findViewById(R.id.knob_aim_turret).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    final int rotationAngle = (int) ((KnobView) v).getRotationAngle();
                    mCurrentRotationAngle = rotationAngle;
                    ((TextView) findViewById(R.id.indicator_turret_aim)).setText(String.valueOf(rotationAngle));
                    Message message = Message.obtain();
                    message.obj = new BombardierMemo(false, false, rotationAngle);
                    ClientSender.sHandler.sendMessage(message);
                    return false;
                }
                return false;
            }
        });
    }

    public void calculateDistance(View view) {
        final int airspeed = Integer.valueOf(((TextInputEditText) findViewById(R.id.edit_airspeed_field)).getText().toString());
        final int height = Integer.valueOf(((TextInputEditText) findViewById(R.id.edit_height_field)).getText().toString());
        ((TextView) findViewById(R.id.indicator_distance)).setText(String.valueOf(airspeed + height)); //FIXME
    }

    public void onButtonClick(View view) {
        final Button buttonClicked = (Button) view;
        switch(buttonClicked.getId()) {
            case R.id.button_launch:
                Message message = Message.obtain();
                message.obj = new BombardierMemo(false, true, mCurrentRotationAngle);
                ClientSender.sHandler.sendMessage(message);
                break;
            case R.id.button_arm:
                buttonClicked.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int seconds = 10; seconds > 0; seconds--) {
                            final int currentCount = seconds;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonClicked.setText(String.format("Working... %1$d", currentCount)); //FIXME: Proper string localization
                                }
                            });
                            try { Thread.sleep(1000); }
                            catch (InterruptedException e) {}
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Button launchButton = findViewById(R.id.button_launch);
                                launchButton.setEnabled(!launchButton.isEnabled());
                                buttonClicked.setText(launchButton.isEnabled() ? getResources().getString(R.string.disarm) : getResources().getString(R.string.arm));
                                buttonClicked.setEnabled(true);
                            }
                        });
                    }
                }).start();
                break;
        }
    }
}
