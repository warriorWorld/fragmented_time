package com.truthower.suhang.mangareader.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import java.util.ArrayList;


public class OnlineMangaFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    private PullToRefreshListView pullListView;
    private SpiderBase spider;
    private ListView mangaListLv;
    private View emptyView;
    private OnlineMangaListAdapter onlineMangaListAdapter;
    //总的漫画列表和一次请求获得的漫画列表
    private ArrayList<MangaBean> totalMangaList = new ArrayList<>(),
            currentMangaList = new ArrayList<>();

    private TopBar topBar;
    private int gradientMagicNum = 500;
    private String[] optionsList = {"切换站点", "搜索", "分类", "跳转"};
    private WheelSelectorDialog optionsSelector, typesSelector, webSelector;
    private MangaEditDialog searchDialog, toPageDialog;
    private boolean isHidden = true;
    private int nowPage = 1, startPage = 1;
    private String nowTypeName = "all", nowWeb = Configure.websList[0];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_online_manga_list, container, false);
        initUI(v);
        initPullListView();
        initSpider(Configure.websList[0]);

        doGetData();
        return v;
    }


    private void initUI(View v) {
        pullListView = (PullToRefreshListView) v.findViewById(R.id.home_ptf);
        mangaListLv = pullListView.getRefreshableView();
        emptyView = v.findViewById(R.id.empty_view);

        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                showOptionsSelector();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void initSpider(String spiderName) {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + spiderName + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (!isHidden) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden) {
        }
    }

    private void doGetData() {
        spider.getMangaList(nowTypeName, nowPage + "", new JsoupCallBack<MangaListBean>() {
            @Override
            public void loadSucceed(final MangaListBean result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentMangaList = result.getMangaList();
                        initListView();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {

            }
        });
    }

    private void initListView() {
        if (nowPage > startPage) {
            //如果不是首页 那就加上之后的
            totalMangaList.addAll(currentMangaList);
        } else {
            totalMangaList = currentMangaList;
        }


        if (null == onlineMangaListAdapter) {
            onlineMangaListAdapter = new OnlineMangaListAdapter(
                    getActivity(), totalMangaList);
            mangaListLv.setAdapter(onlineMangaListAdapter);
            mangaListLv.setFocusable(true);
            mangaListLv.setEmptyView(emptyView);
            mangaListLv.setFocusableInTouchMode(true);
            mangaListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                    intent.putExtra("mangaUrl", totalMangaList.get(position).getUrl());
                    startActivity(intent);
                }
            });
            //变色太难看了
//            mangaListLv.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    topBar.computeAndsetBackgroundAlpha(getScrollY(firstVisibleItem), gradientMagicNum);
//                }
//            });
        } else {
            onlineMangaListAdapter.setList(totalMangaList);
            onlineMangaListAdapter.notifyDataSetChanged();
        }
        pullListView.onPullDownRefreshComplete();// 动画结束方法
        pullListView.onPullUpRefreshComplete();
    }

    public int getScrollY(int firstVisibleItem) {
        View c = mangaListLv.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        return -top + firstVisibleItem * gradientMagicNum;
    }

    private void showOptionsSelector() {
        if (null == optionsList || optionsList.length == 0) {
            baseToast.showToast("没有筛选条件");
            return;
        }
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(getActivity());
            optionsSelector.setCancelable(true);
        }
        optionsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
                switch (position) {
                    case 0:
                        //切换站点
                        showWebsSelector();
                        break;
                    case 1:
                        //搜索
                        showSearchDialog("搜索漫画");
                        break;
                    case 2:
                        //分类
                        showTypesSelector();
                        break;
                    case 3:
                        //跳转
                        showToPageDialog("跳转");
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(optionsList);
    }

    private void showTypesSelector() {
        if (null == typesSelector) {
            typesSelector = new WheelSelectorDialog(getActivity());
            typesSelector.setCancelable(true);
        }
        typesSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                initToFirstPage();
                nowTypeName = selectedRes;
                topBar.setTitle(nowWeb + "(" + nowTypeName + ")");
                doGetData();
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        typesSelector.show();

        typesSelector.initOptionsData(spider.getMangaTypes());
    }

    private void showWebsSelector() {
        if (null == webSelector) {
            webSelector = new WheelSelectorDialog(getActivity());
            webSelector.setCancelable(true);
        }
        webSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                initSpider(selectedRes);
                initToFirstPage();
                nowTypeName = spider.getMangaTypes()[0];
                nowWeb = selectedRes;
                topBar.setTitle(nowWeb + "(" + nowTypeName + ")");
                doGetData();
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        webSelector.show();

        webSelector.initOptionsData(Configure.websList);
    }

    private void showSearchDialog(String title) {
        if (null == searchDialog) {
            searchDialog = new MangaEditDialog(getActivity());
            searchDialog.setOnPeanutEditDialogClickListener(new MangaEditDialog.OnPeanutEditDialogClickListener() {
                @Override
                public void onOkClick(String text) {
                    text = text.replaceAll(" ", "-");
                    text = spider.getWebUrl() + text;
                    if (!spider.isOneShot()) {
                        Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", text);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.setTitle(title);
        searchDialog.setHint("单词间空格分隔,如one piece");
        searchDialog.clearEdit();
    }

    private void showToPageDialog(String title) {
        if (null == toPageDialog) {
            toPageDialog = new MangaEditDialog(getActivity());
            toPageDialog.setOnPeanutEditDialogClickListener(new MangaEditDialog.OnPeanutEditDialogClickListener() {
                @Override
                public void onOkClick(String text) {
                    try {
                        nowPage = (Integer.valueOf(text) - 1) * spider.nextPageNeedAddCount();
                        startPage = nowPage;
                        int actualPage = (startPage / spider.nextPageNeedAddCount()) + 1;
                        topBar.setTitle(nowWeb + "(" + actualPage + ")");
                        doGetData();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            toPageDialog.setCancelable(true);
        }
        toPageDialog.show();
        toPageDialog.setTitle(title);
        toPageDialog.setHint("输入要跳转的页数");
        toPageDialog.setOnlyNumInput(true);
        toPageDialog.clearEdit();
    }

    private void initPullListView() {
        // 上拉加载更多
        pullListView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullListView.setScrollLoadEnabled(false);
        pullListView.setOnRefreshListener(this);

        mangaListLv.setCacheColorHint(0xFFCCCCCC);// 点击后颜色
        // // mListView.setScrollBarStyle(ScrollView.);
//        mangaListLv.setDivider(getResources().getDrawable(R.color.colorAccent));// 线的颜色
        mangaListLv.setDividerHeight(0);// 线的高度

//        pullListView.doPullRefreshing(true, 500);
    }

    private void initToFirstPage() {
        nowPage = 1;
        startPage = 1;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        initToFirstPage();
        doGetData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        nowPage += spider.nextPageNeedAddCount();
        doGetData();
    }
}
