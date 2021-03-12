// 
// Decompiled by Procyon v0.5.36
// 

package com.example.letsscan.AsyncTasks;

import android.util.Log;
import java.util.Iterator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import android.os.AsyncTask;

import com.example.letsscan.recycler_views.TransData;

public class TransAPITask extends AsyncTask<String, Void, TransData>
{
    String content;
    public TranslateAsyncResponse delegate;
    
    public TransAPITask(final String content) {
        this.delegate = null;
        this.content = content;
    }
    
    protected TransData doInBackground(final String... array) {
        final TransData transData = new TransData();
        final String s = array[0];
        final Document bodyFragment = Jsoup.parseBodyFragment(this.content);
        final StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0, 1).toUpperCase());
        sb.append(s.substring(1));
        transData.word = sb.toString();
        final ArrayList<String> translation = new ArrayList<String>();
        final Iterator iterator = bodyFragment.select("tr.gt-baf-entry").iterator();
        while (iterator.hasNext()) {
            Element iterator1 = (Element) iterator;
            translation.add(((Element)iterator1.select("td").get(0)).text());
        }
        if (translation.size() > 0) {
            transData.translation = translation;
            transData.translation_available = true;
            return transData;
        }
        transData.translation.add("Synonym not available");
        transData.translation_available = false;
        return null;
    }
    
    protected void onPostExecute(final TransData transData) {
        Log.i("Translation", "Obtained");
        this.delegate.translateProcessFinish(transData);
    }
}
