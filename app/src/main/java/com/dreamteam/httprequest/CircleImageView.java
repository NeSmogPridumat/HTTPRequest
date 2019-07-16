package com.dreamteam.httprequest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
    public CircleImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        //создаем круг
        final float halfWidth = canvas.getWidth()/2;
        final float halfHeight = canvas.getHeight()/2;
        final float radius = Math.max(halfWidth, halfHeight);
        final Path path = new Path();
        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW);

        //обрезаем
        canvas.clipPath(path);

        super.onDraw(canvas);
    }
}
