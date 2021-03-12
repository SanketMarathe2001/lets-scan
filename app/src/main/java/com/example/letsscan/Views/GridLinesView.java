package com.example.letsscan.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class GridLinesView extends View {
    private List<Line> lines;
    private Paint paint;
    public Size tviewSize;

    public GridLinesView(Context context) {
        super(context);
        init();
    }

    public GridLinesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public GridLinesView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public GridLinesView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    /* access modifiers changed from: package-private */
    public void obtainLines() {
        this.lines = new ArrayList();
        float width = (float) this.tviewSize.getWidth();
        float height = (float) this.tviewSize.getHeight();
        float f = (float) (((double) width) / 3.0d);
        float f2 = (float) (((double) height) / 3.0d);
        this.lines.add(new Line(0.0f, f2, width, f2));
        float f3 = f2 * 2.0f;
        this.lines.add(new Line(0.0f, f3, width, f3));
        this.lines.add(new Line(f, 0.0f, f, height));
        float f4 = f * 2.0f;
        this.lines.add(new Line(f4, 0.0f, f4, height));
    }

    private void init() {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setColor(-1);
        this.paint.setAlpha(125);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(5.0f);
        this.tviewSize = null;
    }

    public void trigger() {
        obtainLines();
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Line> list = this.lines;
        if (list != null) {
            for (Line next : list) {
                canvas.drawLine(next.startX, next.startY, next.stopX, next.stopY, this.paint);
            }
        }
    }
}
