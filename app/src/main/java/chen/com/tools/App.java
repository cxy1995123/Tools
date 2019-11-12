package chen.com.tools;

import android.app.Application;
import android.content.pm.ApplicationInfo;

public class App extends Application {

    public static App app;

    public static App getInstance() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
