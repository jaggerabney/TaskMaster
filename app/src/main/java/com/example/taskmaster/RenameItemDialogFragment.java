package com.example.taskmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class RenameItemDialogFragment extends DialogFragment {
    private MainActivity ma;
    private EditText renameItem_editText;
    private int groupPosition, childPosition, elementType;

    public RenameItemDialogFragment(int groupPosition, int childPosition, int elementType) {
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
        this.elementType = elementType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.ma = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename");
        LayoutInflater li = getActivity().getLayoutInflater();
        View view = li.inflate(R.layout.renameitem_dialogfragment, null);
        renameItem_editText = view.findViewById(R.id.renameitem_dialogfragment_edittext);
        builder.setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = renameItem_editText.getText().toString();
                        ma.rename(groupPosition, childPosition, elementType, name);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
