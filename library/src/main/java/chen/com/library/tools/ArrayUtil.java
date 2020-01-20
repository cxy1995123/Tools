package chen.com.library.tools;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;


public class ArrayUtil {

    public static Collection<String> getArrayValue(Context context, @StringRes int res) {
        String str = context.getResources().getString(res);
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(str, LinkedHashMap.class);
        Collection<String> values = linkedHashMap.values();
        return values;
    }

    public static LinkedHashMap<String, String> getMapValue(Context context, int res) {
        String str = context.getResources().getString(res);
        if (str == null) return null;
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(str, LinkedHashMap.class);
        return linkedHashMap;
    }

    public static LinkedHashMap<String, String> getMapValue(String json) {
        if (json == null) return null;
        Type type = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }


    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean notEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    /**
     * 返回一个固定大小的集合,长度不可改变
     */
    public static <T> Collection<T> toList(T[] items) {
        if (items == null) {
            return new ArrayList<T>();
        }
        return Arrays.asList(items);
    }
}
