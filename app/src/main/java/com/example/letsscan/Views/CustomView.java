package com.example.letsscan.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.letsscan.Dictionary.DictPagerActivity;
import com.example.letsscan.Functions.PreProcess;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {
    static final int CLICK = 3;
    static final int DRAG = 1;
    static final int NONE = 0;
    static final int ZOOM = 2;
    int bmHeight;
    int bmWidth;
    private Context context1;
    public boolean displayBox = false;
    /* access modifiers changed from: private */
    public float mBottom;
    private float[] mCriticPoints;
    public Bitmap mImage = null;
    private PointF mLastTouch = new PointF();
    /* access modifiers changed from: private */
    public Matrix mMatrix = new Matrix();
    /* access modifiers changed from: private */
    public float mOriginalBitmapHeight;
    /* access modifiers changed from: private */
    public float mOriginalBitmapWidth;
    /* access modifiers changed from: private */
    public float mRight;
    public float mScale = 1.0f;
    public ScaleGestureDetector mScaleDetector;
    private PointF mStartTouch = new PointF();
    /* access modifiers changed from: private */
    public float maxScale = 4.0f;
    /* access modifiers changed from: private */
    public float minScale = 1.0f;
    /* access modifiers changed from: private */
    public int mode = 0;
    private Paint paint;
    public List<RectF> rectangles = new ArrayList();
    public boolean visionApiStatus = false;
    public List<String> words;

    public CustomView(Context context) {
        super(context);
        this.context1 = context;
        init((AttributeSet) null);
    }

    public CustomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context1 = context;
        init(attributeSet);
    }

    public CustomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public CustomView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setColor(-16711936);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(2.0f);
        this.mScaleDetector = new ScaleGestureDetector(this.context1, new ScaleListener());
        this.mCriticPoints = new float[9];
    }

    public void imageTrigger(String str, List<String> list) {
        this.words = list;
        getImage(str);
    }

    public void getImage(String str) {
        Bitmap decodeFile = BitmapFactory.decodeFile(str);
        this.mImage = decodeFile;
        if (decodeFile != null) {
            Bitmap convert = PreProcess.convert(this.context1, decodeFile, str);
            this.mImage = convert;
            this.bmWidth = convert.getWidth();
            this.bmHeight = this.mImage.getHeight();
            return;
        }
        Log.d("MYTAG", "Image is null");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = this.mImage;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, this.mMatrix, (Paint) null);
        }
        List<RectF> list = this.rectangles;
        if (list != null && this.displayBox) {
            for (RectF mapRect : list) {
                RectF rectF = new RectF();
                this.mMatrix.mapRect(rectF, mapRect);
                Log.d("MYTAG", "Drawing Rectangle");
                canvas.drawRoundRect(rectF, 10.0f, 10.0f, this.paint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mScaleDetector.onTouchEvent(motionEvent);
        this.mMatrix.getValues(this.mCriticPoints);
        float[] fArr = this.mCriticPoints;
        float f = fArr[2];
        float f2 = fArr[5];
        float[] fArr2 = {motionEvent.getX(), motionEvent.getY()};
        PointF pointF = new PointF(motionEvent.getX(), motionEvent.getY());
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            this.mLastTouch.set(motionEvent.getX(), motionEvent.getY());
            this.mStartTouch.set(this.mLastTouch);
            System.out.print("DRAG\n");
            this.mode = 1;
        } else if (action == 1) {
            if (this.mode == 1 && this.displayBox) {
                boolean z = false;
                for (int i = 0; i < this.rectangles.size(); i++) {
                    RectF rectF = new RectF();
                    this.mMatrix.mapRect(rectF, this.rectangles.get(i));
                    if (rectF.contains(fArr2[0], fArr2[1])) {
                        ((DictPagerActivity) this.context1).dictRun(this.words.get(i));
                        z = true;
                    }
                }
                if (!z) {
                    ((DictPagerActivity) this.context1).emptyBSD();
                }
            }
            this.mode = 0;
            int abs = (int) Math.abs(pointF.x - this.mStartTouch.x);
            int abs2 = (int) Math.abs(pointF.y - this.mStartTouch.y);
            if (abs < 3 && abs2 < 3) {
                performClick();
            }
        } else if (action == 2) {
            int i2 = this.mode;
            if (i2 == 2 || (i2 == 1 && this.mScale > this.minScale)) {
                float f3 = pointF.x - this.mLastTouch.x;
                float f4 = pointF.y - this.mLastTouch.y;
                float round = (float) Math.round(this.mOriginalBitmapHeight * this.mScale);
                if (((float) Math.round(this.mOriginalBitmapWidth * this.mScale)) > ((float) getWidth())) {
                    float f5 = f + f3;
                    if (f5 <= 0.0f) {
                        float f6 = this.mRight;
                        if (f5 < (-f6)) {
                            f += f6;
                        }
                    }
                    f3 = -f;
                } else {
                    f3 = 0.0f;
                }
                if (round > ((float) getHeight())) {
                    float f7 = f2 + f4;
                    if (f7 <= 0.0f) {
                        float f8 = this.mBottom;
                        if (f7 < (-f8)) {
                            f2 += f8;
                        }
                    }
                    f4 = -f2;
                } else {
                    f4 = 0.0f;
                }
                this.mMatrix.postTranslate(f3, f4);
                this.mLastTouch.set(pointF.x, pointF.y);
            }
        } else if (action == 5) {
            this.mLastTouch.set(motionEvent.getX(), motionEvent.getY());
            this.mStartTouch.set(this.mLastTouch);
            this.mode = 2;
            System.out.print("ZOOM\n");
        } else if (action == 6) {
            this.mode = 0;
        }
        postInvalidate();
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            int unused = CustomView.this.mode = 2;
            return true;
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f = CustomView.this.mScale * scaleFactor;
            if (f >= CustomView.this.maxScale || f <= CustomView.this.minScale) {
                return true;
            }
            CustomView.this.mScale = f;
            float width = (float) CustomView.this.getWidth();
            float height = (float) CustomView.this.getHeight();
            CustomView customView = CustomView.this;
            float unused = customView.mRight = (customView.mOriginalBitmapWidth * CustomView.this.mScale) - width;
            CustomView customView2 = CustomView.this;
            float unused2 = customView2.mBottom = (customView2.mOriginalBitmapHeight * CustomView.this.mScale) - height;
            float access$500 = CustomView.this.mOriginalBitmapWidth * CustomView.this.mScale;
            float access$700 = CustomView.this.mOriginalBitmapHeight * CustomView.this.mScale;
            if (access$500 <= width || access$700 <= height) {
                CustomView.this.mMatrix.postScale(scaleFactor, scaleFactor, width / 2.0f, height / 2.0f);
            } else {
                CustomView.this.mMatrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            if (((double) CustomView.this.mScale) <= 1.15d) {
                DictPagerActivity.enableUserInput();
                return true;
            } else if (((double) CustomView.this.mScale) <= 1.15d) {
                return true;
            } else {
                DictPagerActivity.disableUserInput();
                return true;
            }
        }
    }

    public void setMaxZoom(float f) {
        this.maxScale = f;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        float f;
        super.onMeasure(i, i2);
        float measuredWidth = (float) getMeasuredWidth();
        float measuredHeight = (float) getMeasuredHeight();
        if (measuredWidth < ((float) this.bmWidth) || measuredHeight < ((float) this.bmHeight)) {
            f = measuredWidth > measuredHeight ? measuredHeight / ((float) this.bmHeight) : measuredWidth / ((float) this.bmWidth);
        } else {
            f = 1.0f;
        }
        this.mMatrix.setScale(f, f);
        this.mScale = 1.0f;
        float f2 = ((float) this.bmWidth) * f;
        this.mOriginalBitmapWidth = f2;
        float f3 = f * ((float) this.bmHeight);
        this.mOriginalBitmapHeight = f3;
        this.mMatrix.postTranslate((measuredWidth - f2) / 2.0f, (measuredHeight - f3) / 2.0f);
        postInvalidate();
    }
}
