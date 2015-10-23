package man.animalize.ngdaypic;

import android.app.Fragment;


public class MainListActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainListFragment();
    }

}
