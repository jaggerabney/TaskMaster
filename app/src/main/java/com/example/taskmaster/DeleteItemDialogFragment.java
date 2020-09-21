package com.example.taskmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class DeleteItemDialogFragment extends DialogFragment {
    private int groupPosition, childPosition, elementType;

    public DeleteItemDialogFragment(int groupPosition, int childPosition, int elementType) {
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
        this.elementType = elementType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_elem_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity ma = (MainActivity) getActivity();
                        switch (elementType) {
                            case (ExpandableListView.PACKED_POSITION_TYPE_CHILD):
                                ma.deleteItemFromList(groupPosition, childPosition);
                                break;
                            case (ExpandableListView.PACKED_POSITION_TYPE_GROUP):
                                ma.deleteGroupFromList();
                                break;
                            default:
                                Toast.makeText(ma, getString(R.string.error_warning), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
