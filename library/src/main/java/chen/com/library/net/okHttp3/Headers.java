package chen.com.library.net.okHttp3;


import java.util.HashMap;

public class Headers extends HashMap<String, String> {

    public static Headers build() {
        return new Headers();
    }

    public void add(String key, String value) {
        super.put(key, value);
    }

}
