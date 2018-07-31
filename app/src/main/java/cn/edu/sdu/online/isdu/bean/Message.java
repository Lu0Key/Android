package cn.edu.sdu.online.isdu.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.util.Logger;

public class Message {
    private String type;
    private String senderId;
    private String senderNickname;
    private String senderAvatar;
    private String time;
    private String content;
    private boolean isRead = false;
    private static List<OnMessageListener> listeners = new ArrayList<>();

    public static List<Message> msgList = new ArrayList<>();

    public Message() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read, Context context) {
        isRead = read;
        saveMsgList(context);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static void loadMsgList(Context context) {
        msgList.clear();
        SharedPreferences sp = context.getSharedPreferences("msg", Context.MODE_PRIVATE);
        String str = sp.getString("json", "[]");
        try {
            msgList = JSON.parseArray(str, Message.class);
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public static void addOnMessageListener(OnMessageListener listener) {
        listeners.add(listener);
    }

    public static void saveMsgList(Context context) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences("msg", Context.MODE_PRIVATE).edit();
        editor.putString("json", JSON.toJSONString(msgList));
        editor.apply();
        for (OnMessageListener listener : listeners) {
            if (listener != null)
                listener.onMessage();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(type, message.type) &&
                Objects.equals(senderId, message.senderId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, senderId);
    }

    public interface OnMessageListener {
        void onMessage();
    }
}
