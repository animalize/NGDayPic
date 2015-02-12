package man.animalize.ngdaypic;

import android.app.Fragment;

public class DayPicItemActivity extends OneFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DayPicItemFragment();
    }
}
