package com.truthower.suhang.fragmentedtime.business.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Toast;

import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseActivity;
import com.truthower.suhang.fragmentedtime.base.BaseFragment;
import com.truthower.suhang.fragmentedtime.utils.ActivityPoor;
import com.truthower.suhang.fragmentedtime.utils.DisplayUtil;
import com.truthower.suhang.fragmentedtime.widget.viewgroup.MainNavigationView;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private BaseFragment mFragment, mFragment1;
    private DrawerLayout drawer;
    private MainNavigationView navigationView;
    private View appBarMain;
    private TabLayout tabLayout;
    private ViewPager vp;
    private MyFragmentPagerAdapter adapter;
    private int navWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFragment = new BaseFragment();
        mFragment1 = new BaseFragment();
        navWidth = DisplayUtil.dip2px(this, 218);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        appBarMain = findViewById(R.id.app_bar_main);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        vp = (ViewPager) findViewById(R.id.view_pager);
        vp.setAdapter(adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager()));
        vp.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(vp);


        navigationView = (MainNavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //slideOffset是个从0-1的值
                appBarMain.setTranslationX(slideOffset * navWidth);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        hideBaseTopBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private long mExitTime;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {

                Toast.makeText(this, "再按一次退出" + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                ActivityPoor.finishAllActivity();
                System.exit(0);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    /*
 setOffscreenPageLimit对此无用,全都在内存里
 FragmentPagerAdapter 继承自 PagerAdapter。相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment
  的情况。如文档所述，<b>该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页</b>，数量也比
  较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用
  FragmentStatePagerAdapter。FragmentPagerAdapter 重载实现了几个必须的函数，因此来自 PagerAdapter
  的函数，我们只需要实现 getCount()，即可。且，由于 FragmentPagerAdapter.instantiateItem() 的实现中，
  调用了一个新增的虚函数 getItem()，因此，我们还至少需要实现一个 getItem()。因此，总体上来说，相对于继承自
  PagerAdapter，更方便一些。*/
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private String[] titleList = {"测试1", "测试2"};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mFragment;
                case 1:
                    return mFragment1;
                default:
                    return mFragment;
            }
        }


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList[position];
        }
    }
}
