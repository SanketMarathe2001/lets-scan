// 
// Decompiled by Procyon v0.5.36
// 

package com.example.letsscan;

import java.util.Locale;
import android.util.Log;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.SeekBar;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;

public class SpeechSynthesis extends FragmentActivity
{
    private Button mButtonSpeak;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private TextToSpeech mTTS;
    private String mText;
    private int stop_position;
    
    private void speak() {
        this.mTTS.setPitch(1.0f);
        this.mTTS.setSpeechRate(1.0f);
        this.mTTS.speak((CharSequence)this.mText, 0, (Bundle)null, (String)null);
    }
    
    public void engineSetup(final Context context) {
        this.mTTS = new TextToSpeech(context,new TextToSpeech.OnInitListener() {
            public void onInit(int setLanguage) {
                if (setLanguage != 0) {
                    Log.e("TTS", "Initialization failed");
                    return;
                }
                setLanguage = SpeechSynthesis.this.mTTS.setLanguage(Locale.ENGLISH);
                if (setLanguage != -1 && setLanguage != -2) {
                    SpeechSynthesis.this.mTTS.getVoices();
                    return;
                }
                Log.e("TTS", "Language not supported");
            }
        });
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @Override
    protected void onDestroy() {
        final TextToSpeech mtts = this.mTTS;
        if (mtts != null) {
            mtts.stop();
            this.mTTS.shutdown();
        }
        super.onDestroy();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    public void startSpeech(final String mText) {
        this.mText = mText;
        if (mText != null) {
            this.mText = mText.replace("\n", " ");
        }
        this.speak();
    }
    
    public void stopSpeech() {
        this.mTTS.stop();
    }
}
