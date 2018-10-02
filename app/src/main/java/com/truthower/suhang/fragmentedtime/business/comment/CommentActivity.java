//package com.truthower.suhang.fragmentedtime.business.comment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.avos.avoscloud.AVException;
//import com.avos.avoscloud.AVObject;
//import com.avos.avoscloud.AVQuery;
//import com.avos.avoscloud.FindCallback;
//import com.avos.avoscloud.SaveCallback;
//import com.truthower.suhang.fragmentedtime.R;
//import com.truthower.suhang.fragmentedtime.adapter.CommentAdapter;
//import com.truthower.suhang.fragmentedtime.base.BaseActivity;
//import com.truthower.suhang.fragmentedtime.bean.CommentBean;
//import com.truthower.suhang.fragmentedtime.bean.LoginBean;
//import com.truthower.suhang.fragmentedtime.business.user.UserCenterActivity;
//import com.truthower.suhang.fragmentedtime.config.ShareKeys;
//import com.truthower.suhang.fragmentedtime.listener.OnCommenttemClickListener;
//import com.truthower.suhang.fragmentedtime.sort.CommentComparatorByOO;
//import com.truthower.suhang.fragmentedtime.utils.LeanCloundUtil;
//import com.truthower.suhang.fragmentedtime.utils.SharedPreferencesUtils;
//import com.truthower.suhang.fragmentedtime.widget.dialog.SingleLoadBarUtil;
//import com.truthower.suhang.fragmentedtime.widget.recyclerview.LinearLayoutMangerWithoutBug;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by Administrator on 2017/7/29.
// */
//
//public class CommentActivity extends BaseActivity implements View.OnClickListener {
//    private ArrayList<CommentBean> commentList = new ArrayList<>();
//    private CommentAdapter adapter;
//    private RecyclerView commentRcv;
//    private String mangaName;
//    private EditText commentEt;
//    private TextView sentCommentTv;
//    private String replyUser = "";
//    private String mangaUrl;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        mangaName = intent.getStringExtra("mangaName");
//        mangaUrl = intent.getStringExtra("mangaUrl");
//        initUI();
//        doGetData();
//    }
//
//    private void initUI() {
//        commentRcv = (RecyclerView) findViewById(R.id.only_rcv);
//        commentRcv.setLayoutManager
//                (new LinearLayoutMangerWithoutBug
//                        (this, LinearLayoutManager.VERTICAL, false));
//        commentRcv.setFocusableInTouchMode(false);
//        commentRcv.setFocusable(false);
//        commentRcv.setHasFixedSize(true);
//        commentEt = (EditText) findViewById(R.id.comment_et);
//        sentCommentTv = (TextView) findViewById(R.id.sent_comment_tv);
//
//        sentCommentTv.setOnClickListener(this);
//        baseTopBar.setTitle(mangaName + "的评论");
//
//        closeKeyBroad();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_comment;
//    }
//
//    private void doGetData() {
//        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
//            this.finish();
//            return;
//        }
//        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);
//        AVQuery<AVObject> query = new AVQuery<>("Comment");
//        query.whereEqualTo("mangaName", mangaName);
//        query.limit(999);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                SingleLoadBarUtil.getInstance().dismissLoadBar();
//                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
//                    commentList = new ArrayList<>();
//                    if (null != list && list.size() > 0) {
//                        CommentBean item;
//                        for (int i = 0; i < list.size(); i++) {
//                            item = new CommentBean();
//                            item.setCreate_at(list.get(i).getCreatedAt());
//                            item.setMangaName(list.get(i).getString("mangaName"));
//                            item.setMangaUrl(list.get(i).getString("mangaUrl"));
//                            item.setOo_number(list.get(i).getInt("oo_number"));
//                            item.setXx_number(list.get(i).getInt("xx_number"));
//                            item.setReply_user(list.get(i).getString("reply_user"));
//                            item.setOwner(list.get(i).getString("owner"));
//                            item.setComment_content(list.get(i).getString("comment_content"));
//                            item.setObjectId(list.get(i).getObjectId());
//                            commentList.add(item);
//                        }
//                    }
//                    commentList = sortComment(commentList);
//                    initListView();
//                }
//            }
//        });
//    }
//
//    private ArrayList<CommentBean> sortComment(ArrayList<CommentBean> list) {
//        ArrayList<CommentBean> tempList = list;
//        ArrayList<CommentBean> resList = new ArrayList<>();
//
//        CommentComparatorByOO comparator1 = new CommentComparatorByOO();
//        Collections.sort(tempList, comparator1);
//        if (tempList.size() <= 5) {
//            return tempList;
//        } else {
//            for (int i = 0; i < 5; i++) {
//                if (tempList.get(0).getOo_number() > 0) {
//                    CommentBean item = tempList.get(0);
//                    item.setHot(true);
//                    resList.add(item);
//                    tempList.remove(0);
//                }
//            }
//            resList.addAll(tempList);
//            return resList;
//        }
//    }
//
//    private void initListView() {
//        try {
//            if (null == adapter) {
//                adapter = new CommentAdapter(this, commentList);
//                adapter.setOnCommenttemClickListener(new OnCommenttemClickListener() {
//                    @Override
//                    public void onOOClick(int position) {
//                        doOOXX(commentList.get(position).getObjectId(),"oo_number");
//                    }
//
//                    @Override
//                    public void onXXClick(int position) {
//                        doOOXX(commentList.get(position).getObjectId(),"xx_number");
//                    }
//
//                    @Override
//                    public void onUserNameClick(int position) {
//                        Intent intent = new Intent(CommentActivity.this, UserCenterActivity.class);
//                        intent.putExtra("owner", commentList.get(position).getOwner());
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onReplyClick(int position) {
//                        replyUser = commentList.get(position).getOwner();
//                        commentEt.setText("**回复  " + replyUser + "**\n");
//                        commentEt.setSelection(commentEt.getText().toString().length());
//                        commentEt.requestFocus();
//                        showKeyBroad();
//                    }
//                });
//                commentRcv.setAdapter(adapter);
//            } else {
//                adapter.setDatas(commentList);
//                adapter.notifyDataSetChanged();
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    private void doComment() {
//        String userName = LoginBean.getInstance().getUserName(this);
//        if (TextUtils.isEmpty(userName)) {
//            return;
//        }
//        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);
//
//        AVObject object = new AVObject("Comment");
//        object.put("owner", userName);
//        object.put("reply_user", replyUser);
//        object.put("mangaUrl", mangaUrl);
//        object.put("mangaName", mangaName);
//        object.put("comment_content", commentEt.getText().toString().trim());
//        object.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                SingleLoadBarUtil.getInstance().dismissLoadBar();
//                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
//                    commentEt.setText("");
//                    closeKeyBroad();
//                    doGetData();
//                }
//            }
//        });
//    }
//
//    private void doOOXX(String objId, final String type) {
//        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
//                ShareKeys.COMMENT_OOXX_KEY + objId, false)) {
//            baseToast.showToast("你已经点过一次了...");
//            return;
//        }
//        String userName = LoginBean.getInstance().getUserName(this);
//        if (TextUtils.isEmpty(userName)) {
//            return;
//        }
//        SharedPreferencesUtils.setSharedPreferencesData
//                (this, ShareKeys.COMMENT_OOXX_KEY + objId, true);
//        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);
//
//        AVQuery query = new AVQuery("Comment");
//        query.whereEqualTo("objectId", objId);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                SingleLoadBarUtil.getInstance().dismissLoadBar();
//                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
//                    if (null != list && list.size() > 0) {
//                        AVObject item = list.get(0);
//                        // 也可以使用 incrementKey:byAmount: 来给 Number 类型字段累加一个特定数值。
//                        item.increment(type, 1);
////                        list.get(0).increment("oo_number");
////                        list.get(0).setFetchWhenSave(true);
//                        item.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
//                                    doGetData();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//    }
//
//    public void showKeyBroad() {
//        // 自动弹出键盘
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////        imm.showSoftInput(translateET, InputMethodManager.RESULT_SHOWN);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
//                InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }
//
//    private void closeKeyBroad() {
//        // 隐藏键盘
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(commentEt, InputMethodManager.SHOW_FORCED);
//        imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);
//    }
//
//    private boolean checkData() {
//        if (TextUtils.isEmpty(commentEt.getText().toString().trim())) {
//            baseToast.showToast("不能为空!");
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sent_comment_tv:
//                if (checkData()) {
//                    doComment();
//                }
//                break;
//        }
//    }
//}
