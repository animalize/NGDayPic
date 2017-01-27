package man.animalize.ngdaypic;

import android.support.v4.app.Fragment;


public class MainListActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainListFragment();
    }

}
