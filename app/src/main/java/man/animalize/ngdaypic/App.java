package man.animalize.ngdaypic;


import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by anima on 17-2-15.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
    }
}
