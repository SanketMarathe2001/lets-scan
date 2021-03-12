package com.example.letsscan.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.letsscan.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileDialog extends AppCompatDialogFragment {
    Context context;
    /* access modifiers changed from: private */
    public FileDialogListener listener;
    String name;
    String task;

    public interface FileDialogListener {
        void applyName(String str);

        void applyRename(String str);
    }

    public FileDialog(String str, String str2, Context context2) {
        this.task = str;
        this.name = str2;
        this.context = context2;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.new_file_dialog, new LinearLayout(this.context));
        Button button = (Button) inflate.findViewById(R.id.dialog_ok_btn);
        Button button2 = (Button) inflate.findViewById(R.id.dialog_cancel_btn);
        final EditText editText = (EditText) inflate.findViewById(R.id.dialog_text_view);
        Button button3 = (Button) inflate.findViewById(R.id.skip_btn);
        if (this.task.equals("Rename")) {
            editText.setText(this.name);
        }
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = "New Doc " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                if (FileDialog.this.task.equals("Add")) {
                    FileDialog.this.listener.applyName(str);
                } else if (FileDialog.this.task.equals("Rename")) {
                    if (str != "") {
                        FileDialog.this.listener.applyRename(str);
                    } else {
                        System.out.print("Name not Applied \n");
                    }
                }
                create.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = editText.getText().toString();
                if (FileDialog.this.task.equals("Add")) {
                    FileDialog.this.listener.applyName(obj);
                } else if (FileDialog.this.task.equals("Rename")) {
                    if (obj != "") {
                        FileDialog.this.listener.applyRename(obj);
                    } else {
                        System.out.print("Name not Applied \n");
                    }
                }
                create.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                create.dismiss();
            }
        });
        return create;
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        try {
            this.listener = (FileDialogListener) context2;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context2.toString() + "must implement FileDialogListener");
        }
    }
}
