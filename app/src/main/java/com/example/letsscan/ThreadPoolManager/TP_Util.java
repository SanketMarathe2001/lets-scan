package com.example.letsscan.ThreadPoolManager;

import android.os.Bundle;
import android.os.Message;

public class TP_Util {
    public static final String EMPTY_MESSAGE = "<EMPTY_MESSAGE>";
    public static final String LOG_TAG = "BackgroundThread";
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final int MESSAGE_ID = 1;
    public static final int SAVE_PDF_ID = 2;
    public static final int SAVE_PDF_KEY = 2;
    public static final int SHARE_PDF_ID = 3;
    public static final int SHARE_PDF_KEY = 3;

    public static Message createMessage(int i, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_BODY, str);
        Message message = new Message();
        message.what = i;
        message.setData(bundle);
        return message;
    }
}
