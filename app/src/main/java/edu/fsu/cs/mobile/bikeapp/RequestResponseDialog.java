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

public class RequestResponseDialog extends DialogFragment {

    public static final String TAG = RequestResponseDialog.class.getCanonicalName();

    public interface RequestResponseDialogListener {
        void onAgree(String name);

        void onCancel(String name);
    }

    private RequestResponseDialogListener mListener;

    public RequestResponseDialog() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RequestResponseDialogListener) {
            mListener = (RequestResponseDialogListener) context;
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
        View rootView = inflater.inflate(R.layout.fragment_request_response_dialog, null);
        builder.setView(rootView);
        builder.setTitle("Help Response");
        builder.setPositiveButton("Yes", mClickListener);
        builder.setNegativeButton("No", mClickListener);
        return builder.create();
    }

    private DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            String requestDesc = "yes";
            if (which == DialogInterface.BUTTON_POSITIVE) {
                mListener.onAgree(requestDesc);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                mListener.onCancel(requestDesc);
                dialogInterface.dismiss();
            }
        }
    };

}