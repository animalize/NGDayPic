package man.animalize.ngdaypic;


import android.app.Application;
import android.content.Context;

import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by anima on 17-2-15.
 */

public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
        context = getApplicationContext();
    }
}
