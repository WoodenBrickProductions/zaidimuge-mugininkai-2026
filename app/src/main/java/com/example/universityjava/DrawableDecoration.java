package com.example.universityjava;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawableDecoration extends View {
    private final Paint paint = new Paint();
    private int color = getResources().getColor(R.color.text);

    public DrawableDecoration(Context context) {
        super(context);
    }

    public DrawableDecoration(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    void setColor(int color){
        this.color = color;
        invalidate();
    }
    // Implement constructors
    @Override
    protected void onDraw(@NonNull Canvas canvas){
        super.onDraw(canvas);
        paint.setColor(color);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        int width = getWidth();
        int height = getHeight();
        canvas.drawCircle(width/2f,height/2f,Math.min(width,height)/2f,paint);
        canvas.drawRect(0,height,width,0, paint);
    }
}
