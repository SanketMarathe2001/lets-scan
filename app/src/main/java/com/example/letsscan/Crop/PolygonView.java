package com.example.letsscan.Crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;

import com.example.letsscan.ImagePagerActivity;
import com.example.letsscan.R;
import java.util.ArrayList;
import java.util.Map;

public class PolygonView extends FrameLayout {
    /* access modifiers changed from: private */
    public Context context;
    public Matrix inversePolyMat = new Matrix();
    /* access modifiers changed from: private */
    public Map<Integer, PointF> lastValid;
    private ImageView midPointer12;
    private ImageView midPointer13;
    private ImageView midPointer24;
    private ImageView midPointer34;
    /* access modifiers changed from: private */
    public Paint paint;
    private ImageView pointer1;
    private ImageView pointer2;
    private ImageView pointer3;
    private ImageView pointer4;
    public Matrix polyMat;
    /* access modifiers changed from: private */
    public PolygonView polygonView;
    public int position;

    public PolygonView(Context context2) {
        super(context2);
        this.context = context2;
        init();
    }

    public PolygonView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        this.context = context2;
        init();
    }

    public PolygonView(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        this.context = context2;
        init();
    }

    private void init() {
        this.polygonView = this;
        this.pointer1 = getImageView(0, 0);
        this.pointer2 = getImageView(getWidth(), 0);
        this.pointer3 = getImageView(0, getHeight());
        this.pointer4 = getImageView(getWidth(), getHeight());
        ImageView imageView = getImageView(0, getHeight() / 2);
        this.midPointer13 = imageView;
        imageView.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer1, this.pointer3));
        ImageView imageView2 = getImageView(0, getWidth() / 2);
        this.midPointer12 = imageView2;
        imageView2.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer1, this.pointer2));
        ImageView imageView3 = getImageView(0, getHeight() / 2);
        this.midPointer34 = imageView3;
        imageView3.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer3, this.pointer4));
        ImageView imageView4 = getImageView(0, getHeight() / 2);
        this.midPointer24 = imageView4;
        imageView4.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer2, this.pointer4));
        addView(this.pointer1);
        addView(this.pointer2);
        addView(this.midPointer13);
        addView(this.midPointer12);
        addView(this.midPointer34);
        addView(this.midPointer24);
        addView(this.pointer3);
        addView(this.pointer4);
        initPaint();
    }

    /* access modifiers changed from: protected */
    public void attachViewToParent(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.attachViewToParent(view, i, layoutParams);
    }

    private void initPaint() {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setColor(-16776961);
        this.paint.setStrokeWidth(2.0f);
        this.paint.setAntiAlias(true);
    }

    public Map<Integer, PointF> getPoints() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PointF(this.pointer1.getX(), this.pointer1.getY()));
        arrayList.add(new PointF(this.pointer2.getX(), this.pointer2.getY()));
        arrayList.add(new PointF(this.pointer3.getX(), this.pointer3.getY()));
        arrayList.add(new PointF(this.pointer4.getX(), this.pointer4.getY()));
        return ScanUtils.getOrderedPoints(arrayList);
    }

    public void setPoints(Map<Integer, PointF> map) {
        if (map.size() == 4) {
            setPointsCoordinates(map);
        }
    }

    private void setPointsCoordinates(Map<Integer, PointF> map) {
        this.pointer1.setX(map.get(0).x);
        this.pointer1.setY(map.get(0).y);
        this.pointer2.setX(map.get(1).x);
        this.pointer2.setY(map.get(1).y);
        this.pointer3.setX(map.get(2).x);
        this.pointer3.setY(map.get(2).y);
        this.pointer4.setX(map.get(3).x);
        this.pointer4.setY(map.get(3).y);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Canvas canvas2 = canvas;
        canvas2.drawLine(this.pointer1.getX() + ((float) (this.pointer1.getWidth() / 2)), this.pointer1.getY() + ((float) (this.pointer1.getHeight() / 2)), this.pointer3.getX() + ((float) (this.pointer3.getWidth() / 2)), this.pointer3.getY() + ((float) (this.pointer3.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer1.getX() + ((float) (this.pointer1.getWidth() / 2)), this.pointer1.getY() + ((float) (this.pointer1.getHeight() / 2)), this.pointer2.getX() + ((float) (this.pointer2.getWidth() / 2)), this.pointer2.getY() + ((float) (this.pointer2.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer2.getX() + ((float) (this.pointer2.getWidth() / 2)), this.pointer2.getY() + ((float) (this.pointer2.getHeight() / 2)), this.pointer4.getX() + ((float) (this.pointer4.getWidth() / 2)), this.pointer4.getY() + ((float) (this.pointer4.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer3.getX() + ((float) (this.pointer3.getWidth() / 2)), this.pointer3.getY() + ((float) (this.pointer3.getHeight() / 2)), this.pointer4.getX() + ((float) (this.pointer4.getWidth() / 2)), this.pointer4.getY() + ((float) (this.pointer4.getHeight() / 2)), this.paint);
        this.midPointer13.setX(this.pointer3.getX() - ((this.pointer3.getX() - this.pointer1.getX()) / 2.0f));
        this.midPointer13.setY(this.pointer3.getY() - ((this.pointer3.getY() - this.pointer1.getY()) / 2.0f));
        this.midPointer24.setX(this.pointer4.getX() - ((this.pointer4.getX() - this.pointer2.getX()) / 2.0f));
        this.midPointer24.setY(this.pointer4.getY() - ((this.pointer4.getY() - this.pointer2.getY()) / 2.0f));
        this.midPointer34.setX(this.pointer4.getX() - ((this.pointer4.getX() - this.pointer3.getX()) / 2.0f));
        this.midPointer34.setY(this.pointer4.getY() - ((this.pointer4.getY() - this.pointer3.getY()) / 2.0f));
        this.midPointer12.setX(this.pointer2.getX() - ((this.pointer2.getX() - this.pointer1.getX()) / 2.0f));
        this.midPointer12.setY(this.pointer2.getY() - ((this.pointer2.getY() - this.pointer1.getY()) / 2.0f));
    }

    private ImageView getImageView(int i, int i2) {
        ImageView imageView = new ImageView(this.context);
        imageView.setLayoutParams(new LayoutParams(-2, -2));
        imageView.setImageResource(R.drawable.circle);
        imageView.setX((float) i);
        imageView.setY((float) i2);
        imageView.setOnTouchListener(new TouchListenerImpl());
        return imageView;
    }

    private class MidPointTouchListenerImpl implements OnTouchListener {
        PointF DownPT = new PointF();
        PointF StartPT = new PointF();
        private ImageView mainPointer1;
        private ImageView mainPointer2;

        public MidPointTouchListenerImpl(ImageView imageView, ImageView imageView2) {
            this.mainPointer1 = imageView;
            this.mainPointer2 = imageView2;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            ((ImagePagerActivity) PolygonView.this.context).disableUserInput();
            if (action == 0) {
                PolygonView polygonView = PolygonView.this;
                Map unused = polygonView.lastValid = polygonView.getPoints();
                this.DownPT.x = motionEvent.getX();
                this.DownPT.y = motionEvent.getY();
                this.StartPT = new PointF(view.getX(), view.getY());
            } else if (action == 1) {
                if (ScanUtils.isValidShape(PolygonView.this.getPoints())) {
                    ((ImagePagerActivity) PolygonView.this.context).polygonShift(PolygonView.this.position, MathUtils.applyInvertMatrix(PolygonView.this.getPoints(), PolygonView.this.inversePolyMat));
                } else {
                    PolygonView.this.paint.setColor(SupportMenu.CATEGORY_MASK);
                    Toast.makeText(PolygonView.this.context, "Cannot crop this image", Toast.LENGTH_LONG).show();
                    PolygonView polygonView2 = PolygonView.this;
                    polygonView2.setPoints(polygonView2.lastValid);
                }
                PolygonView.this.paint.setColor(-16776961);
                ((ImagePagerActivity) PolygonView.this.context).enableUserInput();
            } else if (action == 2) {
                PointF pointF = new PointF(motionEvent.getX() - this.DownPT.x, motionEvent.getY() - this.DownPT.y);
                if (Math.abs(this.mainPointer1.getX() - this.mainPointer2.getX()) > Math.abs(this.mainPointer1.getY() - this.mainPointer2.getY())) {
                    if (this.mainPointer2.getY() + pointF.y + ((float) view.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.mainPointer2.getY() + pointF.y > 0.0f) {
                        view.setX((float) ((int) (this.StartPT.y + pointF.y)));
                        this.StartPT = new PointF(view.getX(), view.getY());
                        ImageView imageView = this.mainPointer2;
                        imageView.setY((float) ((int) (imageView.getY() + pointF.y)));
                    }
                    if (this.mainPointer1.getY() + pointF.y + ((float) view.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.mainPointer1.getY() + pointF.y > 0.0f) {
                        view.setX((float) ((int) (this.StartPT.y + pointF.y)));
                        this.StartPT = new PointF(view.getX(), view.getY());
                        ImageView imageView2 = this.mainPointer1;
                        imageView2.setY((float) ((int) (imageView2.getY() + pointF.y)));
                    }
                } else {
                    if (this.mainPointer2.getX() + pointF.x + ((float) view.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && this.mainPointer2.getX() + pointF.x > 0.0f) {
                        view.setX((float) ((int) (this.StartPT.x + pointF.x)));
                        this.StartPT = new PointF(view.getX(), view.getY());
                        ImageView imageView3 = this.mainPointer2;
                        imageView3.setX((float) ((int) (imageView3.getX() + pointF.x)));
                    }
                    if (this.mainPointer1.getX() + pointF.x + ((float) view.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && this.mainPointer1.getX() + pointF.x > 0.0f) {
                        view.setX((float) ((int) (this.StartPT.x + pointF.x)));
                        this.StartPT = new PointF(view.getX(), view.getY());
                        ImageView imageView4 = this.mainPointer1;
                        imageView4.setX((float) ((int) (imageView4.getX() + pointF.x)));
                    }
                }
            }
            PolygonView.this.polygonView.invalidate();
            return true;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }

    private class TouchListenerImpl implements OnTouchListener {
        PointF DownPT;
        PointF StartPT;

        private TouchListenerImpl() {
            this.DownPT = new PointF();
            this.StartPT = new PointF();
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            ((ImagePagerActivity) PolygonView.this.context).disableUserInput();
            if (action == 0) {
                PolygonView polygonView = PolygonView.this;
                Map unused = polygonView.lastValid = polygonView.getPoints();
                this.DownPT.x = motionEvent.getX();
                this.DownPT.y = motionEvent.getY();
                this.StartPT = new PointF(view.getX(), view.getY());
            } else if (action == 1) {
                if (ScanUtils.isValidShape(PolygonView.this.getPoints())) {
                    ((ImagePagerActivity) PolygonView.this.context).polygonShift(PolygonView.this.position, MathUtils.applyInvertMatrix(PolygonView.this.getPoints(), PolygonView.this.inversePolyMat));
                } else {
                    PolygonView.this.paint.setColor(SupportMenu.CATEGORY_MASK);
                    Toast.makeText(PolygonView.this.context, "Cannot crop this image", Toast.LENGTH_LONG).show();
                    PolygonView polygonView2 = PolygonView.this;
                    polygonView2.setPoints(polygonView2.lastValid);
                }
                PolygonView.this.paint.setColor(-16776961);
                ((ImagePagerActivity) PolygonView.this.context).enableUserInput();
            } else if (action == 2) {
                PointF pointF = new PointF(motionEvent.getX() - this.DownPT.x, motionEvent.getY() - this.DownPT.y);
                if (this.StartPT.x + pointF.x + ((float) view.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && this.StartPT.y + pointF.y + ((float) view.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.StartPT.x + pointF.x > 0.0f && this.StartPT.y + pointF.y > 0.0f) {
                    view.setX((float) ((int) (this.StartPT.x + pointF.x)));
                    view.setY((float) ((int) (this.StartPT.y + pointF.y)));
                    this.StartPT = new PointF(view.getX(), view.getY());
                }
            }
            PolygonView.this.polygonView.invalidate();
            return true;
        }
    }
}
