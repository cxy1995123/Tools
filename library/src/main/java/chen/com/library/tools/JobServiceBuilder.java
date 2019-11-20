package chen.com.library.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

// <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
@SuppressLint("MissingPermission")
@TargetApi(21)
public class JobServiceBuilder {
    private int jobId;
    private Class<?> serviceClass;
    private Context context;
    private PersistableBundle bundle;


    public JobServiceBuilder addExtras(PersistableBundle bundle) {
        if (bundle == null) return this;

        if (this.bundle == null) {
            this.bundle = new PersistableBundle();
        }
        this.bundle.putAll(bundle);
        return this;
    }

    public JobServiceBuilder addExtras(String key, Object value) {
        if (key == null || value == null) return this;
        if (bundle == null) {
            bundle = new PersistableBundle();
        }

        if (value instanceof CharSequence) {
            bundle.putString(key, value.toString());
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Float) {
            bundle.putDouble(key, (Float) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof String[]) {
            bundle.putStringArray(key, (String[]) value);
        } else if (value instanceof long[]) {
            bundle.putLongArray(key, (long[]) value);
        } else if (value instanceof int[]) {
            bundle.putIntArray(key, (int[]) value);
        }
        return this;
    }

    /**
     * 是否是循环任务
     */
    private boolean isPeriodic = false;

    /**
     * 任务循环间隔,最小15分钟
     */
    private long periodicTime = 900_000;

    /**
     * 任务最大延时时间
     */
    private long overrideDeadline = 1000;

    /**
     * 任务最小延时时间
     */
    private long minimumLatency = 1000;

    /**
     * 任务需要满足的网络状态
     */
    private int requiredNetworkType = JobInfo.NETWORK_TYPE_NONE;

    /**
     * 是否需要满足充电状态
     */
    private boolean requiresCharging = false;

    /**
     * 设备是否要处于 使用状态
     */
    private boolean requiresDeviceIdle = false;

    /**
     * 设备重启后是否执行
     */
    private boolean isPersisted = true;

    public static JobServiceBuilder build(Context context) {
        return new JobServiceBuilder(context);
    }

    private JobServiceBuilder(Context context) {
        this.context = context;
    }

    public JobServiceBuilder setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    public JobServiceBuilder setMinimumLatency(long minimumLatency) {
        this.minimumLatency = minimumLatency;
        return this;
    }

    public JobServiceBuilder setOverrideDeadline(long overrideDeadline) {
        this.overrideDeadline = overrideDeadline;
        return this;
    }

    public JobServiceBuilder setRequiredNetworkType(int requiredNetworkType) {
        this.requiredNetworkType = requiredNetworkType;
        return this;
    }

    public JobServiceBuilder setPersisted(boolean persisted) {
        isPersisted = persisted;
        return this;
    }

    public JobServiceBuilder setJobId(int jobId) {
        this.jobId = jobId;
        return this;
    }

    public JobServiceBuilder setRequiresDeviceIdle(boolean requiresDeviceIdle) {
        this.requiresDeviceIdle = requiresDeviceIdle;
        return this;
    }

    public JobServiceBuilder setRequiresCharging(boolean requiresCharging) {
        this.requiresCharging = requiresCharging;
        return this;
    }

    public JobServiceBuilder setPeriodic(boolean periodic, long periodicTime) {
        isPeriodic = periodic;
        if (periodicTime > 900_000) {
            this.periodicTime = periodicTime;
        }
        return this;
    }

    public void create() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, serviceClass);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName);
        if (isPeriodic) {
            //不能和 setMinimumLatency setOverrideDeadline 同时使用
            builder.setPeriodic(periodicTime); ////循环执行，循环时长为一天（最小为15分钟）
        } else {
            builder.setMinimumLatency(minimumLatency);// 任务最少延迟时间
            builder.setOverrideDeadline(overrideDeadline);// 任务最大延时时间
        }
        builder.setRequiredNetworkType(requiredNetworkType);//需要满足网络条件
        builder.setRequiresCharging(requiresCharging);// 需要满足充电状态
        builder.setRequiresDeviceIdle(requiresDeviceIdle);// 设备处于Idle(Doze)
        builder.setPersisted(isPersisted); //设备重启后是否继续执行
        builder.setBackoffCriteria(1000*15, JobInfo.BACKOFF_POLICY_LINEAR);
        if (bundle != null) {
            builder.setExtras(bundle);
        }
        jobScheduler.schedule(builder.build());
    }

    public static void cancel(Context context, int jobId) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }

    public static void cancelAll(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

    public static JobScheduler getJobScheduler(Context context) {
        return (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

}
