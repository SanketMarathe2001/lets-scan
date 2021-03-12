// 
// Decompiled by Procyon v0.5.36
// 

package com.example.letsscan.AsyncTasks;

import java.util.List;
import java.util.Iterator;
import org.jsoup.select.Elements;
import java.io.IOException;
import android.util.Log;
import java.util.ArrayList;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;

import com.example.letsscan.recycler_views.L1DictData;
import com.example.letsscan.recycler_views.L2DictData;
import com.example.letsscan.recycler_views.L3DictData;

import android.os.AsyncTask;

public class DictAPITask extends AsyncTask<String, Void, L1DictData>
{
    public DictAsyncResponse delegate;
    
    public DictAPITask() {
        this.delegate = null;
    }
    
    protected L1DictData doInBackground(final String... array) {
        try {
            final L1DictData l1DictData = new L1DictData();
            final String str = array[0];
            final StringBuilder sb = new StringBuilder();
            sb.append("https://www.google.com/async/callback:5493?fc=ElAKKDBhN3pLUjdCVEJOa0dyeTh1LWYySFpkakswTjVuQmZteVp1aWJ4WmQSF1hjSWRYNEdKQnVuVno3c1B6LXV1a0FzGgtGZGtpLTZPRm1NSQ&fcv=2&async=term:");
            sb.append(str);
            sb.append(",corpus:en,hhdr:true,hwdgt:true,wfp:true,xpnd:true,ttl:hi,tsl:en,ptl:,_id:fc_5,_pms:s,_fmt:pc");
            final Elements select = Jsoup.connect(sb.toString()).get().select(".VpH2eb.vmod.XpoqFe");
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(str.substring(0, 1).toUpperCase());
            sb2.append(str.substring(1));
            l1DictData.word = sb2.toString();
            l1DictData.phonetic = select.select(".WI9k4c").select(".S23sjd").text();
            l1DictData.audio_link = select.select(".gycwpf.D5gqpe").select("source").attr("src");
            if (!l1DictData.phonetic.equals("") && !l1DictData.phonetic.equals(" ")) {
                l1DictData.phonetic_available = true;
                l1DictData.l1_dictdata_available = true;
            }
            else {
                l1DictData.phonetic_available = false;
            }
            for (final Element element : select.select("div > .vmod").select(".vmod > .vmod")) {
                final L2DictData l2DictData = new L2DictData();
                l2DictData.partOfSpeech = element.select(".vpx4Fd").select(".pgRvse.vdBwhd i").text();
                for (final Element element2 : element.select("div > ol").first().children().select("li")) {
                    final L3DictData l3DictData = new L3DictData();
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(".QIclbb.XpoqFe");
                    sb3.append(" div[data-dobid='dfn']");
                    l3DictData.definition = element2.select(sb3.toString()).text();
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append(".QIclbb.XpoqFe");
                    sb4.append(" .vk_gy");
                    final String text = element2.select(sb4.toString()).text();
                    if (!text.equals("") && !text.equals(" ")) {
                        l3DictData.example = text;
                        l3DictData.example_available = true;
                    }
                    else {
                        l3DictData.example_available = false;
                        l3DictData.example = "Example not available";
                    }
                    final ArrayList synonyms = new ArrayList<String>();
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append(".QIclbb.XpoqFe");
                    sb5.append(" > div.qFRZdb div.CqMNyc");
                    final Iterator iterator3 = element2.select(sb5.toString()).select("div[role='listitem']").iterator();
                    while (iterator3.hasNext()) {
                        synonyms.add(iterator3.next().equals(".gWUzU.F5z5N"));
                    }
                    if (synonyms.size() > 0) {
                        l3DictData.synonyms = (List<String>)synonyms;
                        l3DictData.synonyms_available = true;
                    }
                    else {
                        l3DictData.synonyms.add("Synonym not available");
                        l3DictData.synonyms_available = false;
                    }
                    if (!l3DictData.definition.equals("") && !l3DictData.definition.equals(" ")) {
                        Log.d("MYTAG", " Definition is not null");
                        l2DictData.l3DictData.add(l3DictData);
                    }
                }
                l1DictData.l2DictData.add(l2DictData);
            }
            if (l1DictData.l2DictData.size() > 0) {
                l1DictData.l2_dictdata_available = true;
                Log.d("MYTAG", " CP1");
                return l1DictData;
            }
            l1DictData.l2_dictdata_available = false;
            Log.d("MYTAG", " CP2");
            return null;
        }
        catch (IOException ex) {
            Log.d("IOExceptionLOG", "Failed at donInBackground -> First try");
            ex.printStackTrace();
            return null;
        }
    }
    
    protected void onPostExecute(final L1DictData l1DictData) {
        Log.i("MEANING", "Obtained");
        this.delegate.dictProcessFinish(l1DictData);
    }
}
