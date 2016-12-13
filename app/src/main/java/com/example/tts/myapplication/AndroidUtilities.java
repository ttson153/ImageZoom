package com.example.tts.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by tts on 11/28/16.
 */

public class AndroidUtilities {
    public static float density = 1;
    public static boolean usingHardwareInput;
    public static int statusBarHeight = 24;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    public static Point displaySizePixel = new Point();

    static {
        checkDisplaySize(MyApplication.getAppContext(), null);
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = MyApplication.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = MyApplication.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            density = context.getResources().getDisplayMetrics().density;
            displaySizePixel.x = context.getResources().getDisplayMetrics().widthPixels;
            displaySizePixel.y = context.getResources().getDisplayMetrics().heightPixels;
            statusBarHeight = getStatusBarHeight();

            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
            Log.d("MyLog", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, Matrix.ScaleToFit align) {
        float tx, sx;
        float ty, sy;
        if (rotation == 90 || rotation == 270) {
            sx = dst.height() / src.width();
            sy = dst.width() / src.height();
        } else {
            sx = dst.width() / src.width();
            sy = dst.height() / src.height();
        }
        if (align != Matrix.ScaleToFit.FILL) {
            if (sx > sy) {
                sx = sy;
            } else {
                sy = sx;
            }
        }
        tx = -src.left * sx;
        ty = -src.top * sy;

        matrix.setTranslate(dst.left, dst.top);
        if (rotation == 90) {
            matrix.preRotate(90);
            matrix.preTranslate(0, -dst.width());
        } else if (rotation == 180) {
            matrix.preRotate(180);
            matrix.preTranslate(-dst.width(), -dst.height());
        } else if (rotation == 270) {
            matrix.preRotate(270);
            matrix.preTranslate(-dst.height(), 0);
        }

        matrix.preScale(sx, sy);
        matrix.preTranslate(tx, ty);
    }

}
