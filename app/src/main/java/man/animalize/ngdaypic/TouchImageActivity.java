package man.animalize.ngdaypic;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class TouchImageActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        String jpgfn = i.getStringExtra("jpgfilename");

        return TouchImageFragment.newInstance(jpgfn);
    }
}
