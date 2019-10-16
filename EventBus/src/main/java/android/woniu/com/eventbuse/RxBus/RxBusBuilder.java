package android.woniu.com.eventbuse.RxBus;
import android.util.Log;
import android.util.LongSparseArray;
import androidx.annotation.IntDef;
import androidx.lifecycle.LifecycleOwner;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import static android.woniu.com.eventbuse.RxBus.RxBusBuilder.threadType.BACK;
import static android.woniu.com.eventbuse.RxBus.RxBusBuilder.threadType.MAIN;


public class RxBusBuilder implements Consumer<Throwable> {
    private Object object;
    private Class<?> eventClass = Object.class;

    @threadType
    private int threadType = MAIN;

    public static RxBusBuilder builder(Object object) {
        RxBusBuilder busBuilder = new RxBusBuilder();
        busBuilder.object = object;
        return busBuilder;
    }

    public RxBusBuilder threadmode(@threadType int mode) {
        this.threadType = mode;
        return this;
    }

    public RxBusBuilder eventClass(Class<?> eventClass) {
        this.eventClass = eventClass;
        return this;
    }

    public void subscribe(Consumer consumer) {
        Scheduler scheduler;
        if (threadType == MAIN) {
            scheduler = Schedulers.io();
        } else {
            scheduler = AndroidSchedulers.mainThread();
        }
        Observable<?> observable = RxBus.getInstance()
                .getSubject()
                .ofType(eventClass)
                .subscribeOn(scheduler);

        Disposable disposable = observable.subscribe(consumer, this);
        LongSparseArray<CompositeDisposable> longSparseArray = RxBus.getInstance().getSparseArray();
        int hashcode = object.hashCode();
        int key = longSparseArray.indexOfKey(hashcode);
        if (key > 0) {
            CompositeDisposable compositeDisposable = longSparseArray.get(key);
            compositeDisposable.add(disposable);
            Log.i("RxBus", "subscribe: " + compositeDisposable.size());
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            longSparseArray.put(object.hashCode(), compositeDisposable);
            if (object instanceof LifecycleOwner) {
                ((LifecycleOwner) object).getLifecycle().addObserver(RxBus.getInstance());
            }
        }
        Log.i("RxBus", "subscribe: " + RxBus.getInstance().getSparseArray().size());
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.i("RxBusBuilder", "accept: " + throwable.getMessage());
    }


    @IntDef({MAIN, BACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface threadType {
        int MAIN = 0;
        int BACK = 1;
    }


}
