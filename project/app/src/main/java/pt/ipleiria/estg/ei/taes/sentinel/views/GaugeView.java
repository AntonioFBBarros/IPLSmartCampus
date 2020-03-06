package pt.ipleiria.estg.ei.taes.sentinel.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import pt.ipleiria.estg.ei.taes.sentinel.R;

public class GaugeView extends View {

    private Paint arcPaint;
    private int mSquareColor;
    private float mStrokeWidth;
    private float mPadding;
    private RectF arcBounds;

    public GaugeView(Context context) {
        super(context);
    }

    public GaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public GaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(@Nullable AttributeSet set) {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);

        if(set == null){
            return;
        }

        TypedArray gaugeView = getContext().obtainStyledAttributes(set, R.styleable.GaugeView);
        mSquareColor = gaugeView.getColor(R.styleable.GaugeView_gauge_color, Color.GRAY);
        gaugeView.recycle();

        arcPaint.setColor(mSquareColor);
    }

    public void paint(int color){
        arcPaint.setColor(color);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        mStrokeWidth = (float)((height - (Math.sqrt((height*height) - ((4*width*height)/(5*Math.PI)))))/2);
        mPadding = mStrokeWidth / 2;
        arcPaint.setStrokeWidth(mStrokeWidth);

        arcBounds = new RectF(mPadding, mPadding, width - mPadding, height - mPadding);

        canvas.drawArc(arcBounds, 130f, 280f, false, arcPaint);
    }

}
