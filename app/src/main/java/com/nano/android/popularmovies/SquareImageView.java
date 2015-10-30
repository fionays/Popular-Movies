package com.nano.android.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This class extends ImageView and creates a ImageView with width equal to height.
 * Created by YANG on 9/17/2015.
 */
public class SquareImageView extends ImageView {

    // Four different constructors.
    public SquareImageView (Context context) {
        super(context);
    }

    public SquareImageView (Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @TargetApi(21)
    public SquareImageView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyle) {
        super(context, attrs, defStyleAttr, defStyle);
    }

    /**
     * Give the square ImageView.
     * @param widthMeasureSpec   The requirements of the width as passed by the parent.
     * @param heightMeasureSpec  The requirements of the height as passed by the parent.
     */
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

        // Get the width and height determined by the default onMeasure() method.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Retrieve the measured width of the view.
        int measuredWidth = getMeasuredWidth();

        // Make sure the measured width is no less than the minimum width of the view.
        if (measuredWidth <= getSuggestedMinimumWidth()) {
            measuredWidth = getSuggestedMinimumWidth();
        }

        // Make the measured height equal to the measured width.
        setMeasuredDimension(measuredWidth, measuredWidth);
    }
}
