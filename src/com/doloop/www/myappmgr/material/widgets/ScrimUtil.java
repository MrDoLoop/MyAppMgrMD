package com.doloop.www.myappmgr.material.widgets;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.FloatMath;
import android.view.Gravity;

/**
 * Utility methods for creating prettier gradient scrims.
 */
public class ScrimUtil {
    private ScrimUtil() {
    }

    /**http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1128/2076.html
     * Creates an approximated cubic gradient using a multi-stop linear gradient. See <a
     * href="https://plus.google.com/+RomanNurik/posts/2QvHVFWrHZf">this post</a> for more details.
     */
    public static Drawable makeCubicGradientScrimDrawable(int baseColor, int numStops, int gravity) {
        numStops = Math.max(numStops, 2);
        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());
        final int[] stopColors = new int[numStops];
        int red = Color.red(baseColor);
        int green = Color.green(baseColor);
        int blue = Color.blue(baseColor);
        int alpha = Color.alpha(baseColor);
        for (int i = 0; i < numStops; i++) {
            float x = i * 1f / (numStops - 1);
            //float opacity = MathUtil.constrain(0, 1, FloatMath.pow(x, 3));
            //https://github.com/romannurik/muzei/blob/2d33da034b8f5044633420cbc97bda8cf1e96ede/android-client-common/src/main/java/com/google/android/apps/muzei/util/MathUtil.java
            float opacity = (float) Math.max(0, Math.min(1, Math.pow(x, 3)));
            
            stopColors[i] = Color.argb((int) (alpha * opacity), red, green, blue);
        }
        final float x0, x1, y0, y1;
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                x0 = 1;
                x1 = 0;
                break;
            case Gravity.RIGHT:
                x0 = 0;
                x1 = 1;
                break;
            default:
                x0 = 0;
                x1 = 0;
                break;
        }
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                y0 = 1;
                y1 = 0;
                break;
            case Gravity.BOTTOM:
                y0 = 0;
                y1 = 1;
                break;
            default:
                y0 = 0;
                y1 = 0;
                break;
        }
        paintDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient =
                        new LinearGradient(width * x0, height * y0, width * x1, height * y1, stopColors, null,
                                Shader.TileMode.CLAMP);
                return linearGradient;
            }
        });
        return paintDrawable;
    }
}