package chen.com.library.wifi.entity;


import chen.com.library.wifi.retention.MessageStatus;
import chen.com.library.wifi.retention.MessageType;

public class Message<T> {

    private String id;

    @MessageType
    private int type;

    @MessageStatus
    private int status;

    private T data;

    public Message(T t) {
        this.data = t;
    }

    public void setType(@MessageType int type) {
        this.type = type;
    }

    @MessageType
    public int getType() {
        return type;
    }

    public T getData() {
        return data;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @MessageStatus
    public int getStatus() {
        return status;
    }


}
