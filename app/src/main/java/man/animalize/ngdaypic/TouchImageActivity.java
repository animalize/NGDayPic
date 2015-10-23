package man.animalize.ngdaypic;

import android.app.Fragment;
import android.content.Intent;

public class TouchImageActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        String jpgfn = i.getStringExtra("jpgfilename");

        return TouchImageFragment.newInstance(jpgfn);
    }
}
