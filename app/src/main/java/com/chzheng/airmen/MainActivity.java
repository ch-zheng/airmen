package com.chzheng.airmen;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements PortDialogFragment.PortDialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: Kill running server/client instances
    }

    @Override
    public void onDialogPositiveClick(int port) {
        Intent intent = new Intent(this, LobbyOwnerActivity.class);
        intent.putExtra(Integer.toString(R.id.port), port);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick() {}

    public void createGame(View view) {
        DialogFragment dialog = new PortDialogFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    public void joinGame(View view) {
        startActivity(new Intent(this, ServerBrowserActivity.class));
    }
}
