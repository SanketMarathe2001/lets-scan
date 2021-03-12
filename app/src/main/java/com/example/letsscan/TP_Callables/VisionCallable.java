package com.example.letsscan.TP_Callables;

import android.graphics.RectF;
import java.io.PrintStream;
import java.util.Iterator;
import android.graphics.Bitmap;
import android.os.Message;
import android.os.Parcelable;
import android.os.Bundle;
import java.io.FileWriter;

import com.example.letsscan.Dictionary.VisionAPIData;
import com.example.letsscan.Functions.ImageUtil;
import com.example.letsscan.Functions.PreProcess;
import com.example.letsscan.Functions.Utils;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.Word;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.BoundingPoly;
import java.util.ArrayList;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import java.util.Arrays;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import android.graphics.BitmapFactory;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import android.os.Environment;
import com.google.gson.Gson;
import java.lang.ref.WeakReference;
import android.content.Context;
import java.util.concurrent.Callable;

public class VisionCallable implements Callable
{
    private int ID;
    private String abs_path;
    private Context context;
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    private String path;
    IOException ex,ex2 ;

    public VisionCallable(final String abs_path, final String path, final int id, final Context context) {
        this.context = context;
        this.ID = id;
        this.abs_path = abs_path;
        this.path = path;
    }

    @Override
    public Object call() throws Exception, IOException {
        Boolean b = false;
        Object o;
        try {
            final Gson gson = new Gson();
            final Context context = this.context;
            final StringBuilder sb = new StringBuilder();
            sb.append(Environment.DIRECTORY_PICTURES);
            sb.append("/.annotation/");
            final File externalFilesDir = context.getExternalFilesDir(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(this.path);
            sb2.append(".json");
            o = gson.fromJson(new BufferedReader(new FileReader(new File(externalFilesDir, sb2.toString()))), new TypeToken<VisionAPIData>() {}.getType());
            b = true;
            Log.d("VISION", "READ");
            b = b;
        }
        catch (IOException ex) {
            this.ex = ex;
            o = null;
        }
        ex.printStackTrace();
        if (!b) {
            while (true) {
                Object o2 = o;
                Label_0725_Outer:
                while (true) {
                    Vision.Builder builder;
                    Vision build;
                    Bitmap decodeFile;
                    String convert;
                    Image image;
                    StringBuilder sb3;
                    Feature feature;
                    AnnotateImageRequest annotateImageRequest;
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest;
                    BatchAnnotateImagesResponse batchAnnotateImagesResponse;
                    BatchAnnotateImagesResponse batchAnnotateImagesResponse2;
                    TextAnnotation fullTextAnnotation;
                    ArrayList<String> list;
                    ArrayList<BoundingPoly> list2;
                    String text;
                    Iterator<Page> iterator;
                    Iterator<Block> iterator2;
                    Iterator<Paragraph> iterator3;
                    Iterator<Word> iterator4;
                    Word word;
                    StringBuilder sb4;
                    Iterator<Symbol> iterator5;
                    String string;
                    int length;
                    PrintStream out;
                    StringBuilder sb5;
                    ArrayList<RectF> poly2Rect;
                    Gson gson2;
                    Context context2;
                    StringBuilder sb6;
                    File externalFilesDir2;
                    StringBuilder sb7;
                    FileWriter fileWriter;
                    Label_1307:Label_0761_Outer:
                    while (true) {
                        Label_1304:Label_0797_Outer:
                        while (true) {
                            Label_1301:
                            while (true) {
                                Label_1295: {
                                    try {
                                        Label_1198: {
                                            if (Thread.interrupted()) {
                                                break Label_1198;
                                            }
                                            o2 = o;
                                            Log.d("MYTAG", Thread.currentThread().getName());
                                            o2 = o;
                                            builder = new Vision.Builder(new NetHttpTransport(), new AndroidJsonFactory(), null);
                                            o2 = o;
                                            builder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyC9odgJZovyjbj28teyTT5vuq6pSB8gQ7k"));
                                            o2 = o;
                                            builder.setApplicationName("OCRDetection");
                                            o2 = o;
                                            build = builder.build();
                                            o2 = o;
                                            decodeFile = BitmapFactory.decodeFile(this.abs_path);
                                            o2 = o;
                                            convert = ImageUtil.convert(PreProcess.convert(this.context, decodeFile, this.abs_path));
                                            o2 = o;
                                            Log.d("MYTAG", "Obtained photo data");
                                            o2 = o;
                                            image = new Image();
                                            o2 = o;
                                            image.setContent(convert);
                                            o2 = o;
                                            sb3 = new StringBuilder();
                                            o2 = o;
                                            sb3.append("Photo data length :");
                                            o2 = o;
                                            sb3.append(convert.getBytes().length);
                                            o2 = o;
                                            Log.d("MYTAG", sb3.toString());
                                            o2 = o;
                                            feature = new Feature();
                                            o2 = o;
                                            feature.setType("DOCUMENT_TEXT_DETECTION");
                                            o2 = o;
                                            annotateImageRequest = new AnnotateImageRequest();
                                            o2 = o;
                                            annotateImageRequest.setImage(image);
                                            o2 = o;
                                            annotateImageRequest.setFeatures(Arrays.asList(feature));
                                            o2 = o;
                                            batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                                            o2 = o;
                                            batchAnnotateImagesRequest.setRequests(Arrays.asList(annotateImageRequest));
                                            o2 = o;
                                            Log.d("MYTAG", "Obtaining response");
                                            o2 = o;
                                            try {
                                                    batchAnnotateImagesResponse = build.images().annotate(batchAnnotateImagesRequest).execute();
                                                    o2 = o;
                                                        Log.d("MYTAG", "Response obtained");
                                                        batchAnnotateImagesResponse2 = batchAnnotateImagesResponse;
                                            }
                                            catch (IOException ex2) {
                                                batchAnnotateImagesResponse = null;
                                            }
                                            o2 = o;
                                            Log.d("MYTAG", "Error obtained");
                                            o2 = o;
                                            ex2.printStackTrace();
                                            batchAnnotateImagesResponse2 = batchAnnotateImagesResponse;
                                            o2 = o;
                                            Log.d("MYTAG", "Batch response obtained");
                                            o2 = o;
                                            Log.d("MYTAG", "Obtaining Text Annotation");
                                            o2 = o;
                                            fullTextAnnotation = new TextAnnotation();
                                            if (batchAnnotateImagesResponse2 != null) {
                                                o2 = o;
                                                fullTextAnnotation = batchAnnotateImagesResponse2.getResponses().get(0).getFullTextAnnotation();
                                            }
                                            o2 = o;
                                            list = new ArrayList<String>();
                                            o2 = o;
                                            list2 = new ArrayList<BoundingPoly>();
                                            o2 = o;
                                            text = fullTextAnnotation.getText();
                                            o2 = o;
                                            iterator = fullTextAnnotation.getPages().iterator();
                                            o2 = o;
                                            Label_0967: {
                                                if (!iterator.hasNext()) {
                                                    break Label_0967;
                                                }
                                                o2 = o;
                                                iterator2 = iterator.next().getBlocks().iterator();
                                                o2 = o;
                                                if (!iterator2.hasNext()) {
                                                    break Label_1307;
                                                }
                                                o2 = o;
                                                iterator3 = iterator2.next().getParagraphs().iterator();
                                                o2 = o;
                                                if (!iterator3.hasNext()) {
                                                    break Label_1304;
                                                }
                                                o2 = o;
                                                iterator4 = iterator3.next().getWords().iterator();
                                                o2 = o;
                                                if (!iterator4.hasNext()) {
                                                    break Label_1301;
                                                }
                                                o2 = o;
                                                word = iterator4.next();
                                                o2 = o;
                                                sb4 = new StringBuilder("");
                                                o2 = o;
                                                iterator5 = word.getSymbols().iterator();
                                                while (true) {
                                                    o2 = o;
                                                    if (!iterator5.hasNext()) {
                                                        break;
                                                    }
                                                    o2 = o;
                                                    sb4.append(iterator5.next().getText());
                                                }
                                                o2 = o;
                                                string = sb4.toString();
                                                o2 = o;
                                                length = string.length();
                                                o2 = o;
                                                Label_0936: {
                                                    if (length != 1) {
                                                        break Label_0936;
                                                    }
                                                    o = o2;
                                                    try {
                                                        if (!",.!?-{})([]&*+=<>`/".contains(string)) {
                                                            o = o2;
                                                            list.add(sb4.toString());
                                                            o = o2;
                                                            list2.add(word.getBoundingBox());
                                                        }
                                                        try {

                                                            break Label_1295;
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        o2 = (o = o);
                                                        out = System.out;
                                                        o = o2;
                                                        sb5 = new StringBuilder();
                                                        o = o2;
                                                        sb5.append(text);
                                                        o = o2;
                                                        sb5.append("\n");
                                                        o = o2;
                                                        out.print(sb5.toString());
                                                        o = o2;
                                                        poly2Rect = Utils.Poly2Rect(list2);
                                                        o = o2;
                                                        o2 = new VisionAPIData(this.ID, this.path, list, poly2Rect, text, true, false);
                                                        try {
                                                            gson2 = new Gson();
                                                            context2 = this.context;
                                                            sb6 = new StringBuilder();
                                                            sb6.append(Environment.DIRECTORY_PICTURES);
                                                            sb6.append("/.annotation/");
                                                            externalFilesDir2 = context2.getExternalFilesDir(sb6.toString());
                                                            sb7 = new StringBuilder();
                                                            sb7.append(this.path);
                                                            sb7.append(".json");
                                                            fileWriter = new FileWriter(new File(externalFilesDir2, sb7.toString()));
                                                            gson2.toJson(o2, fileWriter);
                                                            fileWriter.close();
                                                            Log.d("VISION", "WRITE");
                                                            o = o2;
                                                            try{
                                                                break;
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            finally {
                                                                throw new InterruptedException();
                                                            }
                                                        }
                                                        catch (InterruptedException o1) {
                                                            break Label_0725_Outer;
                                                        }
                                                        finally {
                                                            throw new InterruptedException();
                                                        }
                                                    }
                                                    catch (InterruptedException ex3) {
                                                        o2 = o;
                                                        o = ex3;
                                                    }
                                                }
                                            }
                                        }
                                        throw new InterruptedException();
                                    }
                                    catch (InterruptedException ex4) {}
                                    break Label_0725_Outer;
                                }
                                o = o2;
                                continue;
                            }
                            continue Label_0797_Outer;
                        }
                        continue Label_0761_Outer;
                    }
                    continue Label_0725_Outer;
                }
                ((Throwable)o).printStackTrace();
                o = o2;
                break;
            }
        }
        final Bundle data = new Bundle();
        data.putParcelable("dictData", (Parcelable)o);
        final Message message = new Message();
        message.setData(data);
        final WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference = this.mCustomThreadPoolManagerWeakReference;
        if (mCustomThreadPoolManagerWeakReference != null && mCustomThreadPoolManagerWeakReference.get() != null) {
            this.mCustomThreadPoolManagerWeakReference.get().sendMessageToUiThread(message);
        }
        return null;
    }

    public void setCustomThreadPoolManager(final CustomThreadPoolManager referent) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<CustomThreadPoolManager>(referent);
    }
}
