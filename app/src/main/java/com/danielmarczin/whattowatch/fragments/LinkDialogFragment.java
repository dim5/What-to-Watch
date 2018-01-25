package com.danielmarczin.whattowatch.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.danielmarczin.whattowatch.R;

/**
 * A dialog fragment with URLs enabled in the message box
 */
public class LinkDialogFragment extends DialogFragment {

    private static final String TITLE = "TITLE",
            MESSAGE = "MESSAGE";

    public LinkDialogFragment() {
    }

    public static LinkDialogFragment newInstance(@StringRes int title, @StringRes int message) {
        LinkDialogFragment fragment = new LinkDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title, message;
        try {
            title = getArguments().getInt(TITLE);
            message = getArguments().getInt(MESSAGE);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Message or title not found." +
                    " Please use the newInstance method to instantiate.");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_link_enabled_alert_dialog, null);

        TextView tvMessage = view.findViewById(R.id.dialog_message);
        tvMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.ok, null);
        return builder.create();
    }
}
