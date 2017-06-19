package com.tts.libimagezoomanim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by tholh on 8/2/16.
 */
public class InsetsLayout extends FrameLayout {
    private Object lastInsets;
    private boolean inLayout = false;
    private boolean allowApplyInsets = true;

    public InsetsLayout(Context context) {
        this(context, null);
    }

    public InsetsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setFocusableInTouchMode(true);

        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                @SuppressLint("NewApi")
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    final InsetsLayout insetsLayout = (InsetsLayout) v;
                    lastInsets = insets;
                    //drawerLayout.setWillNotDraw(insets.getSystemWindowInsetTop() <= 0 && getBackground() == null);
                    insetsLayout.requestLayout();
                    return insets.consumeSystemWindowInsets();
                }
            });
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    @SuppressLint("NewApi")
    private void dispatchChildInsets(View child, Object insets, int drawerGravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    @SuppressLint("NewApi")
    private void applyMarginInsets(MarginLayoutParams lp, Object insets, int drawerGravity, boolean topOnly) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        lp.topMargin = topOnly ? 0 : wi.getSystemWindowInsetTop();
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    public void setAllowApplyInsets(boolean allowApplyInsets) {
        this.allowApplyInsets = allowApplyInsets;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);

        final boolean applyInsets = lastInsets != null && Build.VERSION.SDK_INT >= 21 && allowApplyInsets;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (applyInsets) {
                if (child.getFitsSystemWindows()) {
                    dispatchChildInsets(child, lastInsets, lp.gravity);
                } else if (child.getTag() == null) {
                    applyMarginInsets(lp, lastInsets, lp.gravity, Build.VERSION.SDK_INT >= 21);
                }
            }

            //child.setPadding(0, 0, 0, 0);
            final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height);
            child.measure(drawerWidthSpec, drawerHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        inLayout = true;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            try {
                child.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        inLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!inLayout) {
            super.requestLayout();
        }
    }
}
