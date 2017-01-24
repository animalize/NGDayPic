package man.animalize.ngdaypic;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

// 只有一个Fragment的Activity
public abstract class OneFragmentActivity extends Activity {

    // 生成Fragment对象
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}
