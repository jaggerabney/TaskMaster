package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

@SuppressLint("AppCompatCustomView")
public class TextlessCheckBox extends CheckBox {
    private Drawable buttonDrawable;

    public TextlessCheckBox(Context context) {
        super(context);
    }

    public TextlessCheckBox(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getCompoundPaddingLeft() + getCompoundPaddingRight();
        } else {
            return buttonDrawable == null ? 0 : buttonDrawable.getIntrinsicWidth();
        }
    }
}
