package cn.edu.sdu.online.isdu.net;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/15
 *
 * 服务器信息
 ****************************************************
 */

public class ServerInfo {

    public static final String ipAddr = "202.194.15.132";
    public static final int port = 8384;
    public static final String url = "http://" + ipAddr + ":" + port + "/";
    public static final String envVarUrl = "http://" + ipAddr + ":8380/env_variables.html";

    public static final String getUrlLogin(String num, String pwd) {
        return url + "/user/signIn?j_username=" + num + "&j_password=" + pwd;
    }

    public static String getUserInfo(String id, String key) {
        return url + "/user/getInformation?id=" + id + "&key=" + key;
    }

    public static final String urlUpdate = url + "/user/update";
}
