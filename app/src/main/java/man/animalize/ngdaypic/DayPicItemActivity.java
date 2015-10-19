package man.animalize.ngdaypic;

import android.app.Fragment;

import man.animalize.ngdaypic.Base.DayPicItem;

public class DayPicItemActivity extends OneFragmentActivity {
    @Override
    protected Fragment createFragment() {

        DayPicItem item = (DayPicItem) getIntent().getSerializableExtra("item");
        return DayPicItemFragment.newInstance(item);
    }
}
