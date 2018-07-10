package cn.edu.sdu.online.isdu.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.net.AccountOp;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/10
 *
 * 用户信息类
 *
 * 加入学院和专业信息
 ****************************************************
 */

public class User extends LitePalSupport {
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_SECRET = 2;

    private String nickName; // 昵称
    private int gender; // 性别
    private String studentNumber; // 学号
    private String name; // 姓名
    private String avatarString; // 头像字符串
    private String selfIntroduce; // 个人介绍
    private String passwordMD5; // MD5加密的教务密码
    private String major; // 专业
    private String depart; // 学院
    private int uid; // ID号，非学号

    public static User staticUser; // 全局用户实例

    public User() {}

    /**
     * 加载本地用户信息
     * @return Local user information
     */
    public static User load() {
        Context context = MyApplication.getContext();
        // 获取登录学号
        SharedPreferences sp = context.getSharedPreferences("login_cache", Context.MODE_PRIVATE);
        String studentNumber = sp.getString("student_number", "");

        return load(studentNumber);
    }

    public static User load(String studentNumber) {
        User user;

        if (!studentNumber.equals("")) {
            List<User> users = LitePal
                    .where("studentNumber = ?", studentNumber)
                    .find(User.class);

            if (!users.isEmpty())
                user = users.get(0);
            else
                user = new User();
        } else
            user = new User();

        return user;
    }

    public static void logout(Context context) {
        AccountOp.logout(context);
        staticUser.studentNumber = null;
        staticUser.passwordMD5 = null;
    }

    /**
     * 保存信息至SharedPreference
     */
    public void save(Context context) {
        User user = LitePal.find(User.class, 1);
        if (user == null) user = new User();
        user.setUid(uid);
        user.setDepart(depart);
        user.setMajor(major);
        user.setSelfIntroduce(selfIntroduce);
        user.setGender(gender);
        user.setAvatarString(avatarString);
        user.setName(name);
        user.setNickName(nickName);
        user.setPasswordMD5(passwordMD5);
        user.setStudentNumber(studentNumber);
        user.save(); // LitePal Save
        SharedPreferences sp = context.getSharedPreferences("login_cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("student_number", studentNumber);
        editor.apply();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarString() {
        return avatarString;
    }

    public void setAvatarString(String avatarString) {
        this.avatarString = avatarString;
    }

    public String getSelfIntroduce() {
        return selfIntroduce;
    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public String getPasswordMD5() {
        return passwordMD5;
    }

    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
