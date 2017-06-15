package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ilgiz on 05.06.2017.
 */

public class Toolbar extends android.support.v7.widget.Toolbar {

    private Integer top = null;

    public Toolbar(Context context) {
        super(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        if (top == null)
            top = ((View) getParent()).getHeight() - offset - getHeight();
    }

    @Override
    public int getTitleMarginTop() {
        return top == null ? super.getTitleMarginTop() : -top + super.getTitleMarginTop();
    }

    @Override
    public int getTitleMarginBottom() {
        return top == null ? super.getTitleMarginBottom() : top + super.getTitleMarginBottom();
    }

}