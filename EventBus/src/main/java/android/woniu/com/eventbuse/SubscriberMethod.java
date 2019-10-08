package android.woniu.com.eventbuse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SubscriberMethod {

    private List<Method> list;
    private Object object;

    public SubscriberMethod(Object o) {
        this.object = o;
        list = new ArrayList<>();
    }

    public void add(Method method) {
        list.add(method);
    }

    public List<Method> getList() {
        return list;
    }

    public Object getObject() {
        return object;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }
}
