package man.animalize.ngdaypic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

// 只有一个Fragment的Activity
public abstract class OneFragmentActivity extends FragmentActivity {

    // 生成Fragment对象
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}
