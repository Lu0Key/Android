package cn.edu.sdu.online.isdu.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.util.Logger;

public class Message {
    private String type;
    private String senderId;
    private boolean isRead = false;
    private static List<OnMessageListener> listeners = new ArrayList<>();

    public static List<Message> msgList = new ArrayList<>();

    public Message() {
        super();
    }

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

    public void setRead(boolean read) {
        isRead = read;
        saveMsgList();
    }

    public static void loadMsgList() {
        msgList.clear();
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("msg", Context.MODE_PRIVATE);
        String str = sp.getString("json", "[]");
        try {
//            JSONArray jsonArray = new JSONArray(str);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Message msg = new Message();
//                msg.setType(jsonObject.getString("type"));
//                msg.setSenderId(jsonObject.getString("senderId"));
//                msg.setRead(jsonObject.getBoolean("read"));
//                msgList.add(msg);
//            }
            msgList = JSON.parseArray(str, Message.class);
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public static void addOnMessageListener(OnMessageListener listener) {
        listeners.add(listener);
    }

    public static void saveMsgList() {
        SharedPreferences.Editor editor =
                MyApplication.getContext().getSharedPreferences("msg", Context.MODE_PRIVATE).edit();
        editor.putString("json", JSON.toJSONString(msgList));
        editor.apply();
        for (OnMessageListener listener : listeners) {
            if (listener != null)
                listener.onMessage();
        }
    }

    public interface OnMessageListener {
        void onMessage();
    }
}
