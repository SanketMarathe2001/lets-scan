package com.example.letsscan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.Functions.Utils;
import com.example.letsscan.TP_Callables.ImageImportCallable;
import com.example.letsscan.TP_Callables.PDFImportCallable;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.example.letsscan.ThreadPoolManager.UiThreadCallback;
import com.example.letsscan.Views.GridLinesView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.opencv.videoio.Videoio;


public class CameraActivity extends AppCompatActivity implements UiThreadCallback {

    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int BATCH_LIMIT = 100;
    static String orientation_status = "normal";
    public static Size tvSize;
    int CAMERA_REQUEST_CODE = 2;
    private int PICK_IMAGES = 103;
    /* access modifiers changed from: private */
    public int PICK_PDF_FILE = 102;
    int PICK_REQUEST_CODE = 1;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.FLASHLIGHT"};
    final Rational aspectRatioNormal = new Rational(3, 4);
    final Rational aspectRatioRotated = new Rational(4, 3);
    Boolean batch_mode;
    LinearLayout bottomLinearLayout;
    ImageButton capture_btn;
    Boolean flash_auto;
    ImageButton flash_btn;
    Boolean flash_off;
    Boolean flash_on;
    ImageButton galleryBtn;
    Boolean grid;
    GridLinesView gridLinesView;
    ImageButton grid_btn;
    ImageButton home_btn;
    ArrayList<ImageAttrs> imageAttrs = new ArrayList<>();
    TextView image_count;
    FrameLayout image_count_fl_btn;
    ImageView image_icon_view;
    ImageCapture imgCap;
    Boolean is_capturing;
    private CustomThreadPoolManager mCustomThreadPoolManager;
    FrameLayout mainFrameLayout;
    ArrayList<String> pathlist = new ArrayList<>();
    private ProgressBar progressBar;
    LinearLayout progressBarPDF;
    Animation rotate_acw;
    Animation rotate_cw;
    Animation rotate_n2r;
    Animation rotate_r2n;
    TextureView textureView;
    private UiHandler uiHandler;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_camera);
        getWindow().setFlags(1024, 1024);
        this.textureView = (TextureView) findViewById(R.id.texture);
        this.gridLinesView = (GridLinesView) findViewById(R.id.gridLinesView);
        this.galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        this.capture_btn = (ImageButton) findViewById(R.id.buttoncapture);
        this.image_count = (TextView) findViewById(R.id.count_tv_cam_act);
        this.image_icon_view = (ImageView) findViewById(R.id.img_icon_cam_act);
        this.home_btn = (ImageButton) findViewById(R.id.home_btn);
        this.flash_btn = (ImageButton) findViewById(R.id.flashbtn);
        this.grid_btn = (ImageButton) findViewById(R.id.gridbtn);
        this.mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        this.bottomLinearLayout = (LinearLayout) findViewById(R.id.bottomLinearLayout);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBarCamera);
        this.progressBarPDF = (LinearLayout) findViewById(R.id.pdf_bar_camera);
        this.image_count_fl_btn = (FrameLayout) findViewById(R.id.img_n_count_btn);
        this.rotate_cw = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        this.rotate_acw = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse);
        this.rotate_n2r = AnimationUtils.loadAnimation(this, R.anim.rotate_n2r);
        this.rotate_r2n = AnimationUtils.loadAnimation(this, R.anim.rotate_r2n);
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, this.REQUIRED_PERMISSIONS, this.REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        CameraX.unbindAll();
        ArrayList<Size> cameraInfo = getCameraInfo();
        Size size = cameraInfo.get((int) (((double) cameraInfo.size()) * 0.1d));
        Size size2 = cameraInfo.get((int) (((double) cameraInfo.size()) * 0.25d));
        Size size3 = cameraInfo.get((int) (((double) cameraInfo.size()) * 0.4d));
        Log.d("MYTAG1", "SD height, width:" + size2.getHeight() + "" + size2.getWidth());
        Log.d("MYTAG1", "HD height, width:" + size.getHeight() + "" + size.getWidth());
        Log.d("MYTAG1", "LO height, width:" + size3.getHeight() + "" + size3.getWidth());
        final Size displaySize = Utils.getDisplaySize(getWindowManager());
        tvSize = Utils.calculateTvSize(displaySize, this.aspectRatioNormal);
        Preview preview = new Preview(new PreviewConfig.Builder().setTargetAspectRatio(this.aspectRatioNormal).setTargetResolution(tvSize).build());
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            public void onUpdated(Preview.PreviewOutput previewOutput) {
                ViewGroup viewGroup = (ViewGroup) CameraActivity.this.textureView.getParent();
                viewGroup.removeView(CameraActivity.this.textureView);
                viewGroup.addView(CameraActivity.this.textureView, 0);
                CameraActivity.this.setViewDimensions(displaySize, CameraActivity.tvSize);
                CameraActivity.this.textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                Log.d("MYTAG", "onUpdated Called");
            }
        });
        ImageCaptureConfig.Builder builder = new ImageCaptureConfig.Builder();
        builder.setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY);
        builder.setTargetAspectRatio(this.aspectRatioNormal);
        builder.setFlashMode(FlashMode.AUTO);

        if (SplashActivity.preferences.get(1).equals(1)) {
            Log.d("MYTAG", "Index 1 & height, width:" + size2.getHeight() + "" + size2.getWidth());
            builder.setTargetResolution(size2);
        } else if (SplashActivity.preferences.get(1).equals(2)) {
            Log.d("MYTAG", "Index 1 & height, width:" + size3.getHeight() + "" + size3.getWidth());
            builder.setTargetResolution(size3);
        } else {
            Log.d("MYTAG", "Index 1 & height, width:" + size.getHeight() + "" + size.getWidth());
            builder.setTargetResolution(size);
        };
        ImageCapture imageCapture = new ImageCapture(builder.build());
        this.imgCap = imageCapture;
        imageCapture.setFlashMode(FlashMode.AUTO);
        this.flash_auto = true;
        this.grid = false;
        this.batch_mode = false;
        this.is_capturing = false;
        this.home_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CameraActivity.this.finish();
            }
        });
        this.flash_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CameraActivity.this.flash_auto.booleanValue()) {
                    CameraActivity.this.flash_on = true;
                    CameraActivity.this.imgCap.setFlashMode(FlashMode.ON);
                    CameraActivity.this.flash_btn.setBackgroundResource(R.drawable.ic_flash_on);
                    CameraActivity.this.flash_auto = false;
                    CameraActivity.this.flash_off = false;
                } else if (CameraActivity.this.flash_on.booleanValue()) {
                    CameraActivity.this.flash_off = true;
                    CameraActivity.this.imgCap.setFlashMode(FlashMode.OFF);
                    CameraActivity.this.flash_btn.setBackgroundResource(R.drawable.ic_flash_off);
                    CameraActivity.this.flash_auto = false;
                    CameraActivity.this.flash_on = false;
                } else {
                    CameraActivity.this.flash_auto = true;
                    CameraActivity.this.imgCap.setFlashMode(FlashMode.AUTO);
                    CameraActivity.this.flash_btn.setBackgroundResource(R.drawable.ic_flash_auto);
                    CameraActivity.this.flash_on = false;
                    CameraActivity.this.flash_off = false;
                }
            }
        });
        this.grid_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CameraActivity.this.gridBtnFunction(CameraActivity.tvSize);
            }
        });
        this.capture_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                File file;
                CameraActivity cameraActivity = CameraActivity.this;
                try {
                    file = File.createTempFile("Image", ".jpg", cameraActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"));
                } catch (IOException e) {
                    e.printStackTrace();
                    file = null;
                }
                if (CameraActivity.this.pathlist.size() < 100) {
                    CameraActivity.this.setProgressBar(true);
                    CameraActivity.this.imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                        public void onImageSaved(File file) {
                            ((RequestBuilder) ((RequestBuilder) Glide.with((FragmentActivity) CameraActivity.this).load(file).centerCrop()).override(90, 120)).into(CameraActivity.this.image_icon_view);
                            CameraActivity.this.pathlist.add(file.getName());
                            CameraActivity.this.startCropping(file);
                            if (!CameraActivity.this.is_capturing.booleanValue()) {
                                CameraActivity.this.is_capturing = true;
                                CameraActivity.this.image_count.setVisibility(View.VISIBLE);
                                CameraActivity.this.image_count.setText("1");
                            }
                            TextView textView = CameraActivity.this.image_count;
                            textView.setText("" + CameraActivity.this.pathlist.size());
                        }

                        public void onError(ImageCapture.ImageCaptureError imageCaptureError, String str, Throwable th) {
                            Toast.makeText(CameraActivity.this.getBaseContext(), "Capture failed with error: " + str, Toast.LENGTH_LONG).show();
                            if (th != null) {
                                th.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                Toast.makeText(CameraActivity.this.getBaseContext(), "Batch size limit reached", Toast.LENGTH_LONG).show();
            }
        });
        this.galleryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] strArr = {CameraActivity.this.getString(R.string.import_gallery), CameraActivity.this.getString(R.string.import_pdf)};
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder.setTitle(R.string.choose_an_option);
                builder.setItems(strArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            CameraActivity.this.pickImage();
                            return;
                        }
                        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                        intent.addCategory("android.intent.category.OPENABLE");
                        intent.setType("application/pdf");
                        CameraActivity.this.startActivityForResult(intent, CameraActivity.this.PICK_PDF_FILE);
                    }
                });
                builder.show();
            }
        });
        this.image_count_fl_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CameraActivity.this.pathlist.size() != 0) {
                    CameraActivity.this.startImagePager();
                } else {
                    Toast.makeText(CameraActivity.this, "Capture atleast one image to proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new OrientationEventListener(this, 2) {
            public void onOrientationChanged(int i) {
                if ((i <= 315 || i >= 360) && ((i <= 0 || i >= 45) && (i <= 135 || i >= 225))) {
                    if (i < 225 || i > 315) {
                        if (i > 45 && i <= 135 && !"right".equals(CameraActivity.orientation_status)) {
                            CameraActivity.orientation_status = "right";
                            Log.d("MYTAG", "ORIENTATION -> Right, value :" + i);
                            CameraActivity.this.imgCap.setTargetAspectRatio(CameraActivity.this.aspectRatioRotated);
                            CameraActivity.this.imgCap.setTargetRotation(Surface.ROTATION_270);
                        }
                    } else if (!"left".equals(CameraActivity.orientation_status)) {
                        CameraActivity.orientation_status = "left";
                        Log.d("MYTAG", "ORIENTATION -> Left, value :" + i);
                        CameraActivity.this.imgCap.setTargetAspectRatio(CameraActivity.this.aspectRatioRotated);
                        CameraActivity.this.imgCap.setTargetRotation(Surface.ROTATION_90);
                    }
                } else if (!"normal".equals(CameraActivity.orientation_status)) {
                    CameraActivity.orientation_status = "normal";
                    Log.d("MYTAG", "ORIENTATION -> Normal, value :" + i);
                    CameraActivity.this.imgCap.setTargetAspectRatio(CameraActivity.this.aspectRatioNormal);
                    CameraActivity.this.imgCap.setTargetRotation(Surface.ROTATION_0);
                }
            }
        }.disable();
        CameraX.bindToLifecycle(this, preview, this.imgCap);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        if (!this.is_capturing.booleanValue()) {
            this.image_count.setVisibility(View.INVISIBLE);
            this.image_icon_view.setImageDrawable((Drawable) null);
            this.image_count.setText("0");
        }
        super.onRestart();
    }

    /* access modifiers changed from: private */
    public void setProgressBar(boolean z) {
        if (z) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else {
            this.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /* access modifiers changed from: protected */
    public void startCropping(File file) {
        Map<Integer, PointF> edgePoints = ScanUtils.getEdgePoints(file.getPath());
        this.imageAttrs.add(new ImageAttrs(edgePoints, 0.0f, "original"));
        Log.d("CROPIMG", String.valueOf(edgePoints));
        setProgressBar(false);
    }

    /* access modifiers changed from: package-private */
    public void startImagePager() {
        if (this.pathlist.size() > 1) {
            this.batch_mode = true;
        }
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra("pathlist", this.pathlist);
        intent.putParcelableArrayListExtra("imageattrs", this.imageAttrs);
        intent.putExtra("batch_mode", this.batch_mode);
        this.pathlist = new ArrayList<>();
        this.imageAttrs = new ArrayList<>();
        this.is_capturing = false;
        startActivityForResult(intent, this.CAMERA_REQUEST_CODE);
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Images"), this.PICK_IMAGES);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == this.PICK_PDF_FILE && i2 == -1) {
            if (intent != null) {
                Uri data = intent.getData();
                Log.i("MYTAG", "PDF Uri :" + data);
                try {
                    getPdfFromUri(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (i == this.PICK_IMAGES && i2 == -1) {
            try {
                getImagesFromData(intent);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } else if (i == this.CAMERA_REQUEST_CODE && i2 == -1 && intent != null) {
            Intent intent2 = new Intent();
            ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("pathlist");
            this.pathlist = stringArrayListExtra;
            Log.d("BackTracking", String.valueOf(stringArrayListExtra));
            intent2.putExtra("pathlist", this.pathlist);
            setResult(-1, intent2);
            finish();
        }
        super.onActivityResult(i, i2, intent);
    }

    private void importLoadStart() {
        this.progressBarPDF.setVisibility(View.VISIBLE);
        this.capture_btn.setClickable(false);
        this.grid_btn.setClickable(false);
        this.flash_btn.setClickable(false);
        this.galleryBtn.setClickable(false);
        this.image_count_fl_btn.setClickable(false);
    }

    private void getImagesFromData(Intent intent) throws IOException {
        importLoadStart();
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
        ImageImportCallable imageImportCallable = new ImageImportCallable(this, intent);
        imageImportCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(imageImportCallable);
    }

    private void getPdfFromUri(Uri uri) throws IOException {
        importLoadStart();
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
        PDFImportCallable pDFImportCallable = new PDFImportCallable(this, uri);
        pDFImportCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(pDFImportCallable);
    }

    public void publishToUiThread(Message message) {
        UiHandler uiHandler2 = this.uiHandler;
        if (uiHandler2 != null) {
            uiHandler2.sendMessage(message);
        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<CameraActivity> mTarget;

        public UiHandler(Looper looper, CameraActivity cameraActivity) {
            super(looper);
            this.mTarget = new WeakReference<>(cameraActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            CameraActivity cameraActivity = (CameraActivity) this.mTarget.get();
            if (cameraActivity != null) {
                cameraActivity.addFiles(message);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addFiles(Message message) {
        Log.d("TPM", "Received result from thread pool -> callable");
        Bundle data = message.getData();
        ArrayList<String> stringArrayList = data.getStringArrayList("path_list");
        ArrayList parcelableArrayList = data.getParcelableArrayList("image_attrs_list");
        this.pathlist.addAll(stringArrayList);
        this.imageAttrs.addAll(parcelableArrayList);
        this.progressBarPDF.setVisibility(View.INVISIBLE);
        this.capture_btn.setClickable(true);
        this.grid_btn.setClickable(true);
        this.flash_btn.setClickable(true);
        this.galleryBtn.setClickable(true);
        this.image_count_fl_btn.setClickable(true);
        startImagePager();
    }

    private void importPDF(String str, String str2) throws IOException {
        Log.d("MYTAG", "imorting pdf cp-4.5");
        Bitmap createBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Log.d("MYTAG", "imorting pdf cp-4.55");
        PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(new File(str), Videoio.CAP_INTELPERC_IMAGE_GENERATOR));
        Log.d("MYTAG", "imorting pdf cp-4.6");
        int pageCount = pdfRenderer.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            PdfRenderer.Page openPage = pdfRenderer.openPage(i);
            openPage.render(createBitmap, (Rect) null, (Matrix) null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            Log.d("MYTAG", "imorting pdf cp-5");
            FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/mypdf/" + i));
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
            Log.d("MYTAG", "imorting pdf cp-6");
            fileOutputStream.flush();
            fileOutputStream.close();
            openPage.close();
        }
    }

    /* access modifiers changed from: private */
    public void gridBtnFunction(Size size) {
        if (!this.grid.booleanValue()) {
            this.gridLinesView.tviewSize = size;
            this.gridLinesView.trigger();
            this.grid = true;
            this.grid_btn.setBackgroundResource(R.drawable.ic_grid_on_cam_act);
            return;
        }
        this.gridLinesView.tviewSize = new Size(0, 0);
        this.gridLinesView.trigger();
        this.grid = false;
        this.grid_btn.setBackgroundResource(R.drawable.ic_grid_off_cam_act);
    }

    public void onBackPressed() {
        if (this.pathlist.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((int) R.string.discard_images);
            builder.setMessage((CharSequence) getString(R.string.discard_images_dialog));
            builder.setPositiveButton((int) R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton((int) R.string.discard, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Iterator<String> it = CameraActivity.this.pathlist.iterator();
                    while (it.hasNext()) {
                        Utils.delete_image(it.next(), CameraActivity.this.getBaseContext());
                    }
                    CameraActivity.this.pathlist.clear();
                    CameraActivity.super.onBackPressed();
                }
            });
            builder.show();
            return;
        }
        super.onBackPressed();
    }

    public String getImageFilePath(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
        query.moveToFirst();
        String string = query.getString(0);
        String substring = string.substring(string.lastIndexOf(":") + 1);
        query.close();
        Cursor query2 = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id = ? ", new String[]{substring}, (String) null);
        query2.moveToFirst();
        String string2 = query2.getString(query2.getColumnIndex("_data"));
        query2.close();
        return string2;
    }

    /* access modifiers changed from: private */
    public void setViewDimensions(Size size, Size size2) {
        this.mainFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(size2.getWidth(), size2.getHeight()));
        this.textureView.setLayoutParams(new FrameLayout.LayoutParams(size2.getWidth(), size2.getHeight()));
        size.getHeight();
        size2.getHeight();
    }

    /* access modifiers changed from: package-private */
    public ArrayList<Size> getCameraInfo() {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        ArrayList<Size> arrayList = new ArrayList<>();
        try {
            for (Size size : ((StreamConfigurationMap) cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[0]).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(256)) {
                if (this.aspectRatioNormal.equals(new Rational(size.getHeight(), size.getWidth()))) {
                    arrayList.add(size);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != this.REQUEST_CODE_PERMISSIONS) {
            return;
        }
        if (allPermissionsGranted()) {
            startCamera();
            return;
        }
        Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean allPermissionsGranted() {
        for (String checkSelfPermission : this.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, checkSelfPermission) != 0) {
                return false;
            }
        }
        return true;
    }
}

