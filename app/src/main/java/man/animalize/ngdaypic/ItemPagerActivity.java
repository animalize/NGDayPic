package man.animalize.ngdaypic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.MyDBHelper;

public class ItemPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;

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
                MyDBHelper db = MyDBHelper.getInstance(ItemPagerActivity.this);

                synchronized (db) {
                    db.getCurrentCursor().moveToPosition(position);
                    DayPicItem item = db.getCurrentCursor().getItem();
                    setTitle(item.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setContentView(mViewPager);

        // PagerAdapter
        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter pa = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                MyDBHelper db = MyDBHelper.getInstance(ItemPagerActivity.this);

                synchronized (db) {
                    db.getCurrentCursor().moveToPosition(position);
                    DayPicItem item = db.getCurrentCursor().getItem();
                    return DayPicItemFragment.newInstance(item);
                }

            }

            @Override
            public int getCount() {
                int posi;
                MyDBHelper db = MyDBHelper.getInstance(ItemPagerActivity.this);
                synchronized (db) {
                    return db.getCurrentCursor().getCount();
                }
            }
        };
        mViewPager.setAdapter(pa);
        MyDBHelper.addAdapter(pa);

        // show
        Intent i = getIntent();
        mViewPager.setCurrentItem(i.getIntExtra("posi", 0));
        setTitle(i.getStringExtra("title"));
    }

    @Override
    protected void onDestroy() {
        MyDBHelper.delAdapter(mViewPager.getAdapter());

        super.onDestroy();
    }
}
