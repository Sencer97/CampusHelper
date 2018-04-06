package com.test.campushelper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class ShowPicGridView extends GridView {
    public ShowPicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ShowPicGridView(Context context) {
        super(context);
    }
    public ShowPicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
