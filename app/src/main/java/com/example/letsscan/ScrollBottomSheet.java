package com.example.letsscan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;

import com.example.letsscan.TP_Callables.ImageCallable;
import com.example.letsscan.TP_Callables.PDFCallable;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.example.letsscan.ThreadPoolManager.UiThreadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class ScrollBottomSheet extends BottomSheetDialogFragment implements UiThreadCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    int FINISH_SHARING = 151;
    Context context;
    int key;
    CustomThreadPoolManager mCustomThreadPoolManager;
    private BottomSheetListener mListener;
    public String name;
    ArrayList<String> pathlist;
    int quality = 30;
    String type = "pdf";
    UiHandler_FA uiHandler;

    public interface BottomSheetListener {
        void onButtonClicked(String str, int i);
    }

    public ScrollBottomSheet(ArrayList<String> arrayList, String str, int i, Context context2) {
        this.pathlist = arrayList;
        this.name = str;
        this.key = i;
        this.context = context2;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.scroll_bottom_sheet, viewGroup, false);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        final LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.post_press);
        final LinearLayout linearLayout2 = (LinearLayout) inflate.findViewById(R.id.pre_press);

        this.uiHandler = new UiHandler_FA(Looper.getMainLooper(), this);
        ((Button) inflate.findViewById(R.id.save_share_btn_frag)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("MYTAG", "CPP 1");
                ScrollBottomSheet scrollBottomSheet = ScrollBottomSheet.this;
                scrollBottomSheet.shareDocument(scrollBottomSheet.pathlist, ScrollBottomSheet.this.name, ScrollBottomSheet.this.key, ScrollBottomSheet.this.quality, ScrollBottomSheet.this.type);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.INVISIBLE);
            }
        });
        ((Button) inflate.findViewById(R.id.cancel_fragment)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ScrollBottomSheet.this.dismiss();
            }
        });
        final TabLayout tabLayout = (TabLayout) inflate.findViewById(R.id.doc_type_tab);
        tabLayout.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(TabLayout.Tab tab) {
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    ScrollBottomSheet.this.type = "pdf";
                } else {
                    ScrollBottomSheet.this.type = "image";
                }
                Log.d("TYPE", ScrollBottomSheet.this.type);
            }
        });
        ((SeekBar) inflate.findViewById(R.id.qualityBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (i == 0) {
                    ScrollBottomSheet.this.quality = 20;
                } else if (i == 1) {
                    ScrollBottomSheet.this.quality = 30;
                } else if (i == 2) {
                    ScrollBottomSheet.this.quality = 50;
                } else if (i != 3) {
                    ScrollBottomSheet.this.quality = 30;
                } else {
                    ScrollBottomSheet.this.quality = 100;
                }
                Log.d("QUALITY", String.valueOf(ScrollBottomSheet.this.quality));
            }
        });
        return inflate;
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        try {
            this.mListener = (BottomSheetListener) context2;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context2.toString() + "must implement bottom sheet listener");
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    private static class UiHandler_FA extends Handler {
        private WeakReference<ScrollBottomSheet> mTarget;

        public UiHandler_FA(Looper looper, ScrollBottomSheet scrollBottomSheet) {
            super(looper);
            this.mTarget = new WeakReference<>(scrollBottomSheet);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            ScrollBottomSheet scrollBottomSheet = (ScrollBottomSheet) this.mTarget.get();
            Log.d("MYTAG", "Back 2 UI thread");
            Bundle data = message.getData();
            ArrayList<Uri> parcelableArrayList = data.getParcelableArrayList("uri");
            String string = data.getString("type");
            int i = message.what;
            if (i != 2) {
                if (i == 3 && scrollBottomSheet != null) {
                    scrollBottomSheet.sharePDFnow(parcelableArrayList, string);
                }
            } else if (scrollBottomSheet != null) {
                scrollBottomSheet.savePDFnotify(string);
            }
        }
    }

    /* access modifiers changed from: private */
    public void savePDFnotify(String str) {
        Toast.makeText(this.context, "Saved to Documents/Let's Scan", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /* access modifiers changed from: package-private */
    public void sharePDFnow(ArrayList<Uri> arrayList, String str) {
        Intent intent;
        if (str.equals("pdf")) {
            intent = new Intent("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.STREAM", arrayList.get(0));
            intent.setType("application/pdf");
        } else {
            intent = new Intent("android.intent.action.SEND_MULTIPLE");
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
            intent.setType("image/jpegs");
        }
        startActivityForResult(Intent.createChooser(intent, "Share File"), this.FINISH_SHARING);
    }

    public void publishToUiThread(Message message) {
        UiHandler_FA uiHandler_FA = this.uiHandler;
        if (uiHandler_FA != null) {
            uiHandler_FA.sendMessage(message);
        }
    }

    public void shareDocument(ArrayList<String> arrayList, String str, int i, int i2, String str2) {
        Log.d("SharenSave", "share document called");
        ArrayList arrayList2 = new ArrayList();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            Context context2 = this.context;
            arrayList2.add(new File(context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"), it.next()).getAbsolutePath());
        }
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
        if (str2.equals("pdf")) {
            Log.d("SharenSave", "pdf callable adding");
            PDFCallable pDFCallable = new PDFCallable(arrayList2, this.context, str, i, i2);
            pDFCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
            this.mCustomThreadPoolManager.addCallable(pDFCallable);
            return;
        }
        Log.d("SharenSave", "image callable adding");
        ImageCallable imageCallable = new ImageCallable(arrayList2, this.context, str, i, i2);
        imageCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(imageCallable);
    }

    public void EncryptPdf(File file,String password) {

        try
        {
            PDDocument document = PDDocument.load(file);

            //Creating access permission object
            AccessPermission ap = new AccessPermission();

            //Creating StandardProtectionPolicy object
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);

            //Setting the length of the encryption key
            spp.setEncryptionKeyLength(128);

            //Setting the access permissions
            spp.setPermissions(ap);

            //Protecting the document
            document.protect(spp);

            System.out.println("Document encrypted");

            //Saving the document
            document.save(file.getAbsolutePath());
            //Closing the document
            document.close();
            Toast.makeText(this.context,"Pdf Encrypted",Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            Log.d("PdfBox-Android-Sample", "Exception thrown while encrypting file", e);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        dismiss();
    }
}
