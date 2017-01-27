package man.animalize.ngdaypic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.MyDBHelper;

public class ItemPagerActivity extends FragmentActivity {
    private MyDBHelper.ItemCursor mCurrentCursor;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mCurrentCursor
        mCurrentCursor = MyDBHelper.getCurrentCursor();

        // ViewPager
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentCursor.moveToPosition(position);
                DayPicItem item = mCurrentCursor.getItem();
                setTitle(item.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setContentView(mViewPager);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                mCurrentCursor.moveToPosition(position);
                DayPicItem item = mCurrentCursor.getItem();

                return DayPicItemFragment.newInstance(item);
            }

            @Override
            public int getCount() {
                return mCurrentCursor.getCount();
            }
        });

        int posi = mCurrentCursor.getPosition();
        DayPicItem item = mCurrentCursor.getItem();
        Log.d("ddddddd", "" + posi);
        mViewPager.setCurrentItem(posi);
        setTitle(item.getTitle());
    }

}
