package com.example.ivansv.weatherforecast;

/**
 * Created by ivansv on 22.02.2016.
 */

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class EmptySpaceCroppingTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int y = source.getHeight() / 5;
        int x = 0;
        Bitmap result = Bitmap.createBitmap(source, x, y, source.getWidth(), source.getHeight() * 3 / 5);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "square()";
    }
}
