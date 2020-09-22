package com.example.taskmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.ArrayList;

public class DeleteDataDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_data_message)
                .setPositiveButton(R.string.delete_data_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            MainActivity ma = (MainActivity) getActivity();
                            ma.delete(ma.getUserData());
                            ma.recreate();
                            dismiss();
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Couldn't delete save file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.delete_data_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
