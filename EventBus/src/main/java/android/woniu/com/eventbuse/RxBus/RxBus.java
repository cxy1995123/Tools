package android.woniu.com.eventbuse.RxBus;

import android.util.Log;
import android.util.LongSparseArray;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus implements LifecycleObserver {

    private static final RxBus client;
    private Subject<Object> subject;
    private LongSparseArray<CompositeDisposable> sparseArray;

    private RxBus() {
        subject = PublishSubject.create().toSerialized();
        sparseArray = new LongSparseArray<>();
    }

    static {
        client = new RxBus();
    }

    public static RxBus getInstance() {
        return client;
    }

    public void subscribe(Object o, Class<?> ofType, Consumer consumer) {
        Disposable disposable = subject.observeOn(AndroidSchedulers.mainThread()).ofType(ofType).subscribe(consumer, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

        int hashcode = o.hashCode();

        int key = sparseArray.indexOfKey(hashcode);
        if (key > 0) {
            CompositeDisposable compositeDisposable = sparseArray.get(key);
            compositeDisposable.add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            sparseArray.put(o.hashCode(), compositeDisposable);

            if (o instanceof LifecycleOwner) {
                ((LifecycleOwner) o).getLifecycle().addObserver(getInstance());
            }

        }
    }

    public RxBusBuilder bind(Object o) {
        return RxBusBuilder.builder(o);
    }

    Subject<Object> getSubject() {
        return subject;
    }

    LongSparseArray<CompositeDisposable> getSparseArray() {
        return sparseArray;
    }

    public static void post(Object object) {
        getInstance().getSubject().onNext(object);
    }

    public static synchronized void unSubscribe(Object object) {
        int index = getInstance().sparseArray.indexOfKey(object.hashCode());
        if (index > 0) {
            CompositeDisposable disposable = getInstance().getSparseArray().valueAt(index);
            if (disposable != null) {
                disposable.dispose();
                getInstance().getSparseArray().removeAt(index);
            }
        }
    }

    public static synchronized void unSubscribeAll() {
        int count = getInstance().sparseArray.size();
        for (int i = 0; i < count; i++) {
            CompositeDisposable disposable = getInstance().getSparseArray().valueAt(i);
            if (disposable != null) {
                disposable.dispose();
            }
        }
        getInstance().sparseArray.clear();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        Log.i("RxBus", "onDestroy: " + owner);
        unSubscribe(owner);
        owner.getLifecycle().removeObserver(RxBus.getInstance());
    }


}
