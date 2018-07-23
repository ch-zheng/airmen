package com.chzheng.airmen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class PortDialogFragment extends DialogFragment {
    private PortDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText portEditor = new EditText(getActivity());
        portEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        portEditor.setHint(R.string.port);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(portEditor)
                .setTitle(R.string.port)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int port = Integer.parseInt(portEditor.getText().toString());
                        mListener.onDialogPositiveClick(port);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (PortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    public interface PortDialogListener {
        public void onDialogPositiveClick(int port);
        public void onDialogNegativeClick();
    }
}
