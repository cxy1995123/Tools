package chen.com.library.wifi.handler;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import chen.com.library.wifi.entity.Message;
import chen.com.library.wifi.entity.StringMessage;
import chen.com.library.wifi.retention.MessageType;

public abstract class MessageHandler implements MessageHandlerImpl {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Timer timer = new Timer();
    private byte[] buff = new byte[1024 * 12];
    private byte[] typeBuff = new byte[4];

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            readMessage();
        }
    };

    /**
     * 开启消息监听线程
     */
    @Override
    public void start(Socket socket) {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            timer.schedule(timerTask, 1000, 500);
        } catch (IOException e) {
            onError(e);
        }
    }

    @Override
    public void writeMessage(Message message) {
        switch (message.getType()) {
            case MessageType.FILE:
                writeFileMessage(message);
                break;
            case MessageType.STRING:
                writeStringMessage((StringMessage) message);
                break;
        }
    }

    private void writeFileMessage(Message message) {
//        ThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int type = message.getType();
//                    File file = (File) message.getData();
//                    byte[] bytes = int2ByteArray(type);
//                    outputStream.write(bytes);
//                    FileInputStream stream = new FileInputStream(file);
//                    int len = 0;
//                    while ((len = stream.read(buff)) != -1) {
//                        outputStream.write(buff, 0, len);
//                    }
//                    outputStream.flush();
//                } catch (Exception e) {
//                    onError(e);
//                }
//            }
//        });
    }

    private void writeStringMessage(StringMessage message) {
        try {
            int type = message.getType();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", message.getId());
            jsonObject.put("content", message.getData());

            byte[] bytes = int2ByteArray(type);
            outputStream.write(bytes);
            outputStream.write(jsonObject.toString().getBytes(Charset.defaultCharset()));
            outputStream.flush();
            sendMessage(message);
        } catch (Exception e) {
            onError(e);
        }
    }

    private void writeExitMessage() {
        if (isConnect()) {
            try {
                outputStream.write(int2ByteArray(MessageType.EXIT));
                outputStream.flush();
            } catch (Exception e) {
                onError(e);
            }
        }
    }

    private void readMessage() {
        if (!isConnect()) return;
        try {
            int read = inputStream.read(typeBuff);
            if (read > -1) {
                int type = bytes2Int(typeBuff);
                switch (type) {
                    case MessageType.FILE:
                        break;
                    case MessageType.EXIT:
                        onDisconnect();
                        break;
                    case MessageType.STRING:
                        readStringMessage();
                        break;
                }
            }
        } catch (Exception e) {
            onError(e);
        }
    }

    private void readStringMessage() {
        try {
            int len = inputStream.read(buff);
            String json = new String(buff, 0, len, Charset.defaultCharset());
            JSONObject object = new JSONObject(json);
            String id = object.getString("id");
            String content = object.getString("content");
            StringMessage message = new StringMessage(id, content);
            receiveMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onDisconnect();

    protected abstract void onConnected();

    public void onDestroy() {
        try {

            writeExitMessage();

            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {

        }
    }

    protected Timer getTimer() {
        return timer;
    }

    protected TimerTask getTimerTask() {
        return timerTask;
    }

    protected InputStream getInputStream() {
        return inputStream;
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected abstract void sendMessage(Message message);

    protected abstract void receiveMessage(Message message);

    protected abstract void onError(Exception e);

    public static byte[] int2ByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static int bytes2Int(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF0000);
        return num;
    }

}
