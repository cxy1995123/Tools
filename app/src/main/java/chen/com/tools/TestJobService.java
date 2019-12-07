package chen.com.tools;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import chen.com.library.data.TimeDate;
import chen.com.library.tools.JobServiceBuilder;

public class TestJobService extends JobService {
    public static final int ID = 999;

    @Override
    public boolean onStartJob(JobParameters params) {
//        File file = new File(Environment.getExternalStorageDirectory(), "ATest/" + TimeDate.getInstance().getTime() + ".txt");
//        if (!file.getParentFile().exists()){
//            file.getParentFile().mkdirs();
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Log.i("TestJobService", "onStartJob: " + TimeDate.getInstance());
        createJob(params);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("TestJobService", TimeDate.getInstance().getTime());
        return false;
    }

    void createJob(JobParameters params) {
        JobServiceBuilder.build(this)
                .addExtras(params.getExtras())
                .setServiceClass(TestJobService.class)
                .setJobId(TestJobService.ID)
                .setMinimumLatency(1000 * 5)
                .setOverrideDeadline(1000 * 5)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .create();
        jobFinished(params, false);
    }
}
