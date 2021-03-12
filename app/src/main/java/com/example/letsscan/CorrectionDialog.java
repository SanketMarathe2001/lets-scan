package com.example.letsscan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CorrectionDialog extends AppCompatDialogFragment {
    String correct_word;
    /* access modifiers changed from: private */
    public EditText input;
    private FileDialogListener listener;
    String task;

    public interface FileDialogListener {
        void applyName(String str);

        void applyRename(String str);
    }

    public CorrectionDialog(String str, String str2) {
        this.task = str;
        this.correct_word = str2;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.input = new EditText(getActivity());
        if (this.task.equals("correction dialog")) {
            this.input.setText(this.correct_word);
        }
        builder.setView(this.input).setTitle("Name").setMessage("Enter the word :").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                CorrectionDialog.this.input.getText().toString();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.listener = (FileDialogListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + "must implement FileDialogListener");
        }
    }
}
