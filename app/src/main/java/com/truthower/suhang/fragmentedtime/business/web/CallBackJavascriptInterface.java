package com.truthower.suhang.fragmentedtime.business.web;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

/**
 * This javascript interface allows the page to communicate that text has been
 * selected by the user.
 * <p/>
 * 这个类是java和JavaScript的桥梁,JavaScript中可以调用这个类中的方法 达到两种语言通讯的效果
 *
 * @author btate
 */
public class CallBackJavascriptInterface {

    /**
     * The TAG for logging.
     */
    private static final String TAG = "CallBackJavascriptInterface";

    /**
     * The javascript interface name for adding to web view.
     */
    private final String interfaceName = "myObj";


    /**
     * The context.
     */
    private Context mContext;
    private JavaScriptCallBackListener javaScriptCallBackListener;
    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    /**
     * Constructor accepting context.
     *
     * @param c
     */
    public CallBackJavascriptInterface(Context c) {
        this.mContext = c;
    }

    public CallBackJavascriptInterface(Context c, JavaScriptCallBackListener l) {
        this.mContext = c;
        this.javaScriptCallBackListener = l;
    }

    /**
     * Handles javascript errors.
     * <p/>
     * 如果您在编写HTML5应用，需要在JS代码中访问Java中的函数，则您会用到WebView的addJavascriptInterface()函数。
     * 因为安全问题，在Android4.2中(如果应用的android:targetSdkVersion数值为17+)JS只能访问带有
     *
     * @JavascriptInterface注解的Java函数。 之前，任何Public的函数都可以在JS代码中访问，而Java对象继承关系会导致很多Public的函数都可以在JS中访问，其中一个重要的函数就是
     * getClass()。然后JS可以通过反射来访问其他一些内容。通过引入 @JavascriptInterface注解，则在JS中只能访问
     * @JavascriptInterface注解的函数。这样就可以增强安全性。 如果您的应用android:targetSdkVersion数值为17或者大于17记得添加 @JavascriptInterface 注解。
     */
    @JavascriptInterface
    public void toPage(final String activityName) {
        if (null != javaScriptCallBackListener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    javaScriptCallBackListener.toPage(activityName);
                }
            });
        }
    }

    @JavascriptInterface
    public void toPage(final String activityName, final String params) {
        if (null != javaScriptCallBackListener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    javaScriptCallBackListener.toPage(activityName,params);
                }
            });
        }
    }


    @JavascriptInterface
    public void goToLogin(final String string) {
        if (null != javaScriptCallBackListener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    javaScriptCallBackListener.goToLogin(string);
                }
            });
        }
    }

    @JavascriptInterface
    public void closeHtml() {
        if (null != javaScriptCallBackListener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    javaScriptCallBackListener.closeHtml();
                }
            });
        }
    }

    @JavascriptInterface
    public void closePullRefresh() {
        if (null != javaScriptCallBackListener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    javaScriptCallBackListener.closePullRefresh();
                }
            });
        }
    }

    /**
     * Gets the interface name
     *
     * @return
     */
    @JavascriptInterface
    public String getInterfaceName() {
        return this.interfaceName;
    }
}
