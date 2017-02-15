package man.animalize.ngdaypic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.MyDBHelper;

public class ItemPagerActivity extends FragmentActivity {
    private myPagerAdapter pa;
    private ViewPager mViewPager;

    // 广播接收器。 当数据库改变时，用doQuery()刷新显示列表
    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doQuery();
        }
    };

    private void doQuery() {
        AsyncTask<Void, Integer, ArrayList<DayPicItem>> at = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                return MyDBHelper.getInstance(ItemPagerActivity.this)
                        .getArrayList();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<DayPicItem> al = (ArrayList<DayPicItem>) o;
                ItemPagerActivity.this.pa.setArrayList(al);
            }
        };
        at.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewPager
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        //mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DayPicItem item = pa.getDayPicItem(position);
                setTitle(item.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setContentView(mViewPager);

        // PagerAdapter
        FragmentManager fm = getSupportFragmentManager();
        pa = new myPagerAdapter(fm);
        pa.setArrayList(MyDBHelper.getInstance(this).getArrayList());
        mViewPager.setAdapter(pa);

        // show
        Intent i = getIntent();
        mViewPager.setCurrentItem(i.getIntExtra("posi", 0));
        setTitle(i.getStringExtra("title"));

        // 注册广播接收器
        // 接到此广播时，会刷新显示列表
        IntentFilter itf = new IntentFilter(BackService.FILTER);
        registerReceiver(mReciver, itf);
    }

    @Override
    protected void onDestroy() {
        // 注销广播接收器
        unregisterReceiver(mReciver);

        super.onDestroy();
    }

    public static class myPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<DayPicItem> al;

        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setArrayList(ArrayList<DayPicItem> al) {
            this.al = al;
            notifyDataSetChanged();
        }

        public DayPicItem getDayPicItem(int position) {
            return al.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            DayPicItem item = al.get(position);
            return DayPicItemFragment.newInstance(item);
        }

        @Override
        public int getCount() {
            if (al == null) {
                return 0;
            } else {
                return al.size();
            }
        }
    }
}
