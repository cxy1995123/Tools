package chen.com.tools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class LightManager implements SensorEventListener {

    private static final float MINIMUM_BRIGHTNESS = 60f;

    private float lastTimeValue = 0;

    private SensorManager senserManager;

    private LightManager(Context context) {
        senserManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor defaultSensor = senserManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        senserManager.registerListener(this, defaultSensor, SENSOR_DELAY_NORMAL);
    }

    public void destory() {
        senserManager.unregisterListener(this);
    }

    public static LightManager create(Context context) {
        return new LightManager(context);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values != null && event.values.length > 0) {
            float value = event.values[0];
            if (value < MINIMUM_BRIGHTNESS) {
                if (lastTimeValue > MINIMUM_BRIGHTNESS) {
                    lastTimeValue = value;
                    if (listener != null) {
                        listener.OnSensorChanged(true, lastTimeValue);
                    }
                }
            } else {
                if (lastTimeValue < MINIMUM_BRIGHTNESS) {
                    lastTimeValue = value;
                    if (listener != null) {
                        listener.OnSensorChanged(false, lastTimeValue);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private OnSensorChangedListener listener;

    public void setListener(OnSensorChangedListener listener) {
        this.listener = listener;
    }

    public interface OnSensorChangedListener {
        void OnSensorChanged(boolean isBelowStandard, float value);
    }

}
