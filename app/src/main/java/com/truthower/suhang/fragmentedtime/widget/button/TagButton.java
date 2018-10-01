package com.truthower.suhang.fragmentedtime.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.truthower.suhang.fragmentedtime.R;

/**
 * Created by Administrator on 2017/8/1.
 */

public class TagButton extends ToggleButton {
    private Context context;
    private boolean mCheckEnable = true;

    public TagButton(Context paramContext) {
        super(paramContext);
        init();
    }

    public TagButton(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public TagButton(Context paramContext, AttributeSet paramAttributeSet,
                     int paramInt) {
        super(paramContext, paramAttributeSet, 0);
        init();
    }

    private void init() {
        setTextOn(null);
        setTextOff(null);
        setText("");
        setBackgroundResource(R.drawable.tag_toggle);
        setTextSize(12);
        setHeight(22);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void setCheckEnable(boolean paramBoolean) {
        this.mCheckEnable = paramBoolean;
        if (!this.mCheckEnable) {
            super.setChecked(false);
        }
    }

    public void setChecked(boolean paramBoolean) {
        if (this.mCheckEnable) {
            super.setChecked(paramBoolean);
        }
    }
}
