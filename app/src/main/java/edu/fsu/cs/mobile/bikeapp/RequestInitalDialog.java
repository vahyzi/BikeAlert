package edu.fsu.cs.mobile.bikeapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RequestInitalDialog extends DialogFragment {

    public static final String TAG = RequestInitalDialog.class.getCanonicalName();

    public interface RequestInitalDialogListener {
        void onSend(String name);

        void onCancel(String name);
    }

    private TextView mEditName;
    private EditText descEditText;

    private RequestInitalDialogListener mListener;

    public RequestInitalDialog() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RequestInitalDialogListener) {
            mListener = (RequestInitalDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MyDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_my_dialog, null);
        mEditName = rootView.findViewById(R.id.greeting);
        descEditText = rootView.findViewById(R.id.edit_desc);
        builder.setView(rootView);
        builder.setTitle("Alert Details");
        builder.setPositiveButton("Send", mClickListener);
        builder.setNegativeButton("Cancel", mClickListener);
        return builder.create();
    }

    private DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            String requestDesc = descEditText.getText().toString();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                mListener.onSend(requestDesc);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                mListener.onCancel(requestDesc);
                dialogInterface.dismiss();
            }
        }
    };

}
