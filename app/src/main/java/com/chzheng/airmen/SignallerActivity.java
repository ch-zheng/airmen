package com.chzheng.airmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.memos.UpdateMemo;
import com.chzheng.airmen.views.RadarView;

import java.util.ArrayList;

public class SignallerActivity extends AppCompatActivity {
    private static final String TAG = "Signaller Activity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private long mLastUpdateTime = System.currentTimeMillis();
    private RadarView mRadarView;
    private RecyclerView mMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signaller);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.signaller);
        mRadarView = findViewById(R.id.radar);
        mMessageList = findViewById(R.id.list);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));
        mMessageList.setAdapter(new MessagesAdapter(null));
    }

    @Override
    protected void onStart() {
        super.onStart();
        sHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof UpdateMemo) {
                    final UpdateMemo memo = (UpdateMemo) msg.obj;
                    if (System.currentTimeMillis() - mLastUpdateTime > 500) {
                        mRadarView.setUpdate(memo);
                        ((MessagesAdapter) mMessageList.getAdapter()).setMessages(memo.messages);
                        mLastUpdateTime = System.currentTimeMillis();
                    }
                } else if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case SHUTDOWN:
                            startActivity(new Intent(SignallerActivity.this, ReviewActivity.class));
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        sHandler = new Handler(Looper.getMainLooper());
    }

    private static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
        private ArrayList<String> mDataSet = new ArrayList<>();

        public MessagesAdapter(ArrayList<String> dataSet) {
            if (dataSet != null) mDataSet = dataSet;
        }

        @NonNull
        @Override
        public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setHorizontallyScrolling(false);
            textView.setAllCaps(true);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
            holder.mView.setText(mDataSet.get(position));
        }

        @Override
        public int getItemCount() { return mDataSet == null ? 0 : mDataSet.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mView;
            public ViewHolder(TextView view) {
                super(view);
                mView = view;
            }
        }

        public void setMessages(ArrayList<String> messages) {
            final int sizeDifference = messages.size() - mDataSet.size();
            if (sizeDifference > 0) {
                mDataSet = messages;
                if (sizeDifference == 1) notifyItemInserted(mDataSet.size() -1);
                else notifyItemRangeInserted(mDataSet.size() - sizeDifference, sizeDifference);
            }
        }
    }
}
