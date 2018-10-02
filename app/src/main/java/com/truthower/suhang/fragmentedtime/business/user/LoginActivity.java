//package com.truthower.suhang.fragmentedtime.business.user;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.avos.avoscloud.AVException;
//import com.avos.avoscloud.AVUser;
//import com.avos.avoscloud.LogInCallback;
//import com.avos.avoscloud.RequestPasswordResetCallback;
//import com.truthower.suhang.fragmentedtime.R;
//import com.truthower.suhang.fragmentedtime.base.BaseActivity;
//import com.truthower.suhang.fragmentedtime.bean.LoginBean;
//import com.truthower.suhang.fragmentedtime.config.ShareKeys;
//import com.truthower.suhang.fragmentedtime.listener.OnEditResultListener;
//import com.truthower.suhang.fragmentedtime.utils.LeanCloundUtil;
//import com.truthower.suhang.fragmentedtime.utils.SharedPreferencesUtils;
//import com.truthower.suhang.fragmentedtime.widget.dialog.MangaDialog;
//import com.truthower.suhang.fragmentedtime.widget.dialog.MangaEditDialog;
//import com.truthower.suhang.fragmentedtime.widget.dialog.SingleLoadBarUtil;
//
//
//public class LoginActivity extends BaseActivity implements View.OnClickListener {
//    private ImageView crossIv;
//    private EditText userIdEt;
//    private EditText userPsdEt;
//    private Button loginBtn;
//    private TextView forgotPsdTv;
//    private TextView registerNowTv;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initUI();
//        Intent intent = getIntent();
//        String toast = intent.getStringExtra("toast");
//        if (!TextUtils.isEmpty(toast)) {
//            baseToast.showToast(toast);
//        }
//        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
//            MangaDialog dialog = new MangaDialog(this);
//            dialog.show();
//            dialog.setTitle("教程");
//            dialog.setMessage("1,登录后就可以把漫画加入收藏和正在追更了,很方便的." +
//                    "\n2,没有账号可以点击下边的注册按钮" +
//                    "\n2,我希望你最好注册下,谢谢.");
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
//            userIdEt.setText(LoginBean.getInstance().getUserName());
//        }
//    }
//
//    private void initUI() {
//        crossIv = (ImageView) findViewById(R.id.cross_iv);
//        userIdEt = (EditText) findViewById(R.id.user_id_et);
//        userPsdEt = (EditText) findViewById(R.id.user_psd_et);
//        userPsdEt.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                //因为DOWN和UP都算回车 所以这样写 避免调用两次
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_ENTER:
//                            if (checkData()) {
//                                doLogin();
//                            }
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
//        loginBtn = (Button) findViewById(R.id.login_btn);
//        forgotPsdTv = (TextView) findViewById(R.id.forgot_psd_tv);
//        registerNowTv = (TextView) findViewById(R.id.register_now_tv);
//
//        crossIv.setOnClickListener(this);
//        loginBtn.setOnClickListener(this);
//        forgotPsdTv.setOnClickListener(this);
//        registerNowTv.setOnClickListener(this);
//
//        hideBaseTopBar();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_login;
//    }
//
//    private boolean checkData() {
//        if (TextUtils.isEmpty(userIdEt.getText().toString())) {
//            baseToast.showToast("请输入用户名!");
//            return false;
//        }
//        if (TextUtils.isEmpty(userPsdEt.getText().toString())) {
//            baseToast.showToast("请输入密码!");
//            return false;
//        }
//        return true;
//    }
//
//
//    private void doLogin() {
//        SingleLoadBarUtil.getInstance().showLoadBar(LoginActivity.this);
//        AVUser.logInInBackground(userIdEt.getText().toString(), userPsdEt.getText().toString(),
//                new LogInCallback<AVUser>() {
//                    @Override
//                    public void done(AVUser avUser, AVException e) {
//                        SingleLoadBarUtil.getInstance().dismissLoadBar();
//                        if (e == null) {
//                            LoginBean.getInstance().setEmail(LoginActivity.this, avUser.getEmail());
//                            LoginBean.getInstance().setUserName(LoginActivity.this, avUser.getUsername());
//                            LoginBean.getInstance().setMaster(LoginActivity.this, avUser.getBoolean("master"));
//                            LoginBean.getInstance().setCreator(LoginActivity.this, avUser.getBoolean("creator"));
//                            LoginActivity.this.finish();
////                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        } else {
//                            baseToast.showToast(e.getMessage());
//                        }
//                    }
//                });
//    }
//
//    private void showResetPsdDialog() {
//        MangaEditDialog editDialog = new MangaEditDialog(this);
//        editDialog.setOnEditResultListener(new OnEditResultListener() {
//            @Override
//            public void onResult(String text) {
//                AVUser.requestPasswordResetInBackground(text, new RequestPasswordResetCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (LeanCloundUtil.handleLeanResult(LoginActivity.this, e)) {
//                            baseToast.showToast("重置密码邮件已发送到指定邮箱");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelClick() {
//
//            }
//        });
//        editDialog.show();
//        editDialog.setOkText("确定");
//        editDialog.setCancelText("取消");
//        editDialog.setHint("请输入注册邮箱");
//        editDialog.setTitle("发送重置密码邮件");
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent = null;
//        switch (v.getId()) {
//            case R.id.login_btn:
//                if (checkData()) {
//                    doLogin();
//                }
//                break;
//            case R.id.cross_iv:
//                LoginActivity.this.finish();
//                break;
//            case R.id.register_now_tv:
//                intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                break;
//            case R.id.forgot_psd_tv:
//                showResetPsdDialog();
//                break;
//        }
//        if (null != intent) {
//            startActivity(intent);
//        }
//    }
//}
