package man.animalize.ngdaypic;

import androidx.fragment.app.Fragment;


public class MainListActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainListFragment();
    }

}
