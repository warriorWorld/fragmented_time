package com.truthower.suhang.fragmentedtime.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.config.Configure;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class MangaImgDialog extends Dialog {
    private Context context;
    private ImageView crossIv, imgIv;
    private OnImgDialogImgClickListener onImgDialogImgClickListener;

    public MangaImgDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_manga);
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
        crossIv = (ImageView) findViewById(R.id.cross_iv);
        crossIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        imgIv = (ImageView) findViewById(R.id.image_view);
        imgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=onImgDialogImgClickListener){
                    dismiss();
                    onImgDialogImgClickListener.onClick();
                }
            }
        });
    }

    public void setImgRes(String uri) {
        ImageLoader.getInstance().displayImage(uri, imgIv, Configure.smallImageOptions);
//        ImageLoader.getInstance().displayImage(uri, imgIv);
    }

    public void setOnImgDialogImgClickListener(OnImgDialogImgClickListener onImgDialogImgClickListener) {
        this.onImgDialogImgClickListener = onImgDialogImgClickListener;
    }

    public interface OnImgDialogImgClickListener {
        void onClick();
    }
}
