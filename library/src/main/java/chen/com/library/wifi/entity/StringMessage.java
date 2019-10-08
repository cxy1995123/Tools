package chen.com.library.wifi.entity;
import java.util.UUID;

import chen.com.library.wifi.retention.MessageType;

public class StringMessage extends Message<String> {

    public StringMessage(String s) {
        super(s);
        setType(MessageType.STRING);
        setId(UUID.randomUUID().toString());
    }

    public StringMessage(String id,String s) {
        super(s);
        setType(MessageType.STRING);
        setId(id);
    }

    @Override
    public String getData() {
        return super.getData();
    }
}
