package com.example.letsscan.ThreadPoolManager;

import android.os.Message;

public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
