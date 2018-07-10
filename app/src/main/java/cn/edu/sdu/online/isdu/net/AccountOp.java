package cn.edu.sdu.online.isdu.net;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.bean.User;
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/10
 *
 * 用户信息访问
 ****************************************************
 */

public class AccountOp {

    public static final String ACTION_SYNC_USER_INFO =
            "cn.edu.sdu.online.isdu.SYNC_USER_INFO_SUCCESS";
    public static final String ACTION_USER_LOG_OUT =
            "cn.edu.sdu.online.isdu.USER_LOG_OUT";

    public static LocalBroadcastManager localBroadcastManager =
            LocalBroadcastManager.getInstance(MyApplication.getContext());

    /**
     * 从服务器同步用户信息
     */
    public static void syncUserInformation() {
        /////////Network
        String stuNum = User.staticUser.getStudentNumber();
        String stuPwd = User.staticUser.getPasswordMD5();

        if (stuNum != null && stuPwd != null && !stuNum.trim().equals("") && !stuPwd.trim().equals(""))
            NetworkAccess.buildRequest(ServerInfo.url + "signIn?j_username=" + stuNum + "&j_password=" + stuPwd, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {

                        String jsonString = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
//                            if (!"failed".equals(jsonObject.getString("result"))) {
//
//                                User.staticUser.setStudentNumber(jsonObject.getString("studentNumber"));
//                                User.staticUser.setPasswordMD5(jsonObject.getString("j_password"));
//                                User.staticUser.setNickName(jsonObject.getString("nickname"));
//                                User.staticUser.setName(jsonObject.getString("name"));
//                                User.staticUser.setAvatarString(jsonObject.getString("avatar_string"));
//
//                                String genderString = jsonObject.getString("gender");
//                                if (genderString.equals("男")) {
//                                    User.staticUser.setGender(User.GENDER_MALE);
//                                } else if (genderString.equals("女")) {
//                                    User.staticUser.setGender(User.GENDER_FEMALE);
//                                } else {
//                                    User.staticUser.setGender(User.GENDER_SECRET);
//                                }
//
//                                User.staticUser.setSelfIntroduce(jsonObject.getString("self_introduce"));
//                                User.staticUser.setMajor(jsonObject.getString("major"));
//                                User.staticUser.setDepart(jsonObject.getString("depart"));
//                            }

                            if (jsonObject.isNull("status") || !jsonObject.getString("status").equals("failed")) {
                                AccountOp.syncUserInformation(jsonObject); // 同步用户信息
                                final Intent intent = new Intent(ACTION_SYNC_USER_INFO);
                                intent.putExtra("result", "success");
                                localBroadcastManager.sendBroadcast(intent);
                            } else {
                                final Intent intent = new Intent(ACTION_SYNC_USER_INFO);
                                intent.putExtra("result", jsonObject.getString("failed"));
                                localBroadcastManager.sendBroadcast(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

    }

    /**
     * 从JSON同步用户信息
     *
     * @param jsonObject 包含用户信息的JSON对象
     */
    public static void syncUserInformation(JSONObject jsonObject) {
        try {
            if (jsonObject.isNull("status") || !jsonObject.getString("status").equals("failed")) {

                User.staticUser.setStudentNumber(jsonObject.getString("studentNumber"));
                User.staticUser.setPasswordMD5(jsonObject.getString("j_password"));
                User.staticUser.setNickName(jsonObject.getString("nickname"));
                User.staticUser.setName(jsonObject.getString("name"));
                User.staticUser.setAvatarString(jsonObject.getString("avatar"));

                String genderString = jsonObject.getString("gender");
                if (genderString.equals("男")) {
                    User.staticUser.setGender(User.GENDER_MALE);
                } else if (genderString.equals("女")) {
                    User.staticUser.setGender(User.GENDER_FEMALE);
                } else {
                    User.staticUser.setGender(User.GENDER_SECRET);
                }

                User.staticUser.setSelfIntroduce(jsonObject.getString("sign"));
                User.staticUser.setMajor(jsonObject.getString("major"));
                User.staticUser.setDepart(jsonObject.getString("depart"));
                User.staticUser.setUid(jsonObject.getInt("id"));

                User.staticUser.save();
                User.staticUser.save(MyApplication.getContext());
            }

            final Intent intent = new Intent(ACTION_SYNC_USER_INFO);
            intent.putExtra("result", jsonObject.getString("status"));
            localBroadcastManager.sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences("login_cache", Context.MODE_PRIVATE).edit();
        editor.remove("student_number");
        editor.apply();
        final Intent intent = new Intent(ACTION_USER_LOG_OUT);
        localBroadcastManager.sendBroadcast(intent);
    }
}
