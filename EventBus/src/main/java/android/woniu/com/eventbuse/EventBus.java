package android.woniu.com.eventbuse;

import android.text.TextUtils;
import android.util.Log;
import android.woniu.com.eventbuse.SubscriberMethod;
import android.woniu.com.eventbuse.ThreadPool;
import android.woniu.com.eventbuse.annotation.SubscriptionMethod;
import android.woniu.com.eventbuse.annotation.ThreadMode;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventBus implements LifecycleObserver {

    public static String TAG = "event";

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(LifecycleOwner owner) {
//        getInstance().bind(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        Log.i("EventBus", "onDestroy: " + owner);
        getInstance().unBind(owner);
        owner.getLifecycle().removeObserver(EventBus.getInstance());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onLifecycleChanged(LifecycleOwner owner, Lifecycle.Event event) {

    }

    private ThreadPool pool = new ThreadPool();

    public static EventBus getInstance() {
        return Single.event;
    }

    private HashMap<Object, SubscriberMethod> array = new HashMap<>();

    private EventBus() {
    }



    private boolean isAdd(Method method) {
        return method.getAnnotation(SubscriptionMethod.class) != null;
    }

    public  static void Bind(Object object) {
        if (object instanceof LifecycleOwner) {
            ((LifecycleOwner) object).getLifecycle().addObserver(getInstance());
        }
        getInstance().bind(object);
    }

    private synchronized void bind(Object view) {
        Class parentClass = view.getClass();
        Method[] methods = parentClass.getDeclaredMethods();
        SubscriberMethod subscriberMethod = new SubscriberMethod(view);
        for (final Method method : methods) {
            if (isAdd(method)) {
                subscriberMethod.add(method);
            }
        }

        if (!subscriberMethod.isEmpty()) {
            array.put(view, subscriberMethod);
        }
    }

    public void post(Object obj) {
        synchronized (this){
            Set<Map.Entry<Object, SubscriberMethod>> entries = array.entrySet();
            for (Map.Entry<Object, SubscriberMethod> entry : entries) {
                final SubscriberMethod value = entry.getValue();
                Iterator<Method> iterator = value.getList().iterator();
                for (; iterator.hasNext(); ) {
                    Method method = iterator.next();
                    FilterMethod(value, method, obj);
                }
            }
        }
    }

    private static void FilterMethod(final SubscriberMethod subscriberMethod, final Method method, final Object obj) {
        Class<?>[] types = method.getParameterTypes();
        if (compareParameterType(types, obj.getClass())) {
            SubscriptionMethod annotation = method.getAnnotation(SubscriptionMethod.class);
            if (annotation != null) {
                int mode = annotation.threadMode();
                CallMethod(subscriberMethod.getObject(), method, mode, obj);
            }
        }
    }

    private static void CallMethod(final Object obj, final Method method, int mode, final Object parameter) {
        switch (mode) {
            case ThreadMode.BackGround:
                getInstance().pool.run(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(obj, parameter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case ThreadMode.MinThread:
                try {
                    method.invoke(obj, parameter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static boolean compareParameterType(Class<?>[] types, Class<?> aClass) {
        if (types == null || types.length <= 0 || aClass == null) return false;
        Class<?> type = types[0];
        if (type == aClass) return true;
        return TextUtils.equals("int", type.getSimpleName()) &&
                TextUtils.equals(aClass.getSimpleName(), Integer.class.getSimpleName());
    }

    public synchronized void  unBind(Object view) {
        if (view == null) return;
        array.remove(view);
    }

    private static class Single {
        private static EventBus event = new EventBus();
    }

}
