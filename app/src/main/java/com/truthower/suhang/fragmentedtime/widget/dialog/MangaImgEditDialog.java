package com.truthower.suhang.fragmentedtime.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.config.Configure;
import com.truthower.suhang.fragmentedtime.listener.OnEditResultListener;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class MangaImgEditDialog extends Dialog {
    private Context context;
    private ImageView imgIv;
    private EditText editText;
    private OnEditResultListener onEditResultListener;

    public MangaImgEditDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_edit_manga);
        init();

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        lp.width = (int) (d.getWidth() * 0.9);
        window.setAttributes(lp);
    }


    private void init() {
        editText = (EditText) findViewById(R.id.edit_et);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //因为DOWN和UP都算回车 所以这样写 避免调用两次
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            inputEnd();
                            break;
                    }
                }
                return false;
            }
        });
        imgIv = (ImageView) findViewById(R.id.image_view);
        imgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void inputEnd() {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(context, "空的啊", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null != onEditResultListener) {
//            dismiss();
            onEditResultListener.onResult(editText.getText().toString().trim());
            clearEdit();
        }
    }

    @Override
    public void show() {
        super.show();
        showKeyBroad();
    }

    @Override
    public void dismiss() {
        closeKeyBroad();
        super.dismiss();
    }

    public void showKeyBroad() {
        // 自动弹出键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(translateET, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void clearEdit() {
        editText.setText("");
    }

    private void closeKeyBroad() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setImgRes(String uri) {
        ImageLoader.getInstance().displayImage(uri, imgIv, Configure.smallImageOptions);
//        ImageLoader.getInstance().displayImage(uri, imgIv);
    }

    public void setImgRes(Bitmap bitmap) {
        imgIv.setImageBitmap(bitmap);
    }

    public void setOnEditResultListener(OnEditResultListener onEditResultListener) {
        this.onEditResultListener = onEditResultListener;
    }
}
