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
    public static String searchUser(String studentNumber){
        return url + "user/findBySN?studentNumber="+studentNumber;
    }
    public static String searchUserbyNickName(String NickName){
        return url + "user/findByNN?nickname="+NickName;
    }
    public static String getUserInfo(String id, String key) {
        return url + "/user/getInformation?id=" + id + "&key=" + key;
    }
    public static String getLibraryInfoUrl(String id){
        return url + "library/info?id=" + id;
    }
    public static final String getBindUrl(String uid,String cardNumber, String pwd) {
        return url + "library/bind?id=" + uid + "&cardNumber=" + cardNumber + "&password=" + pwd;
    }
    public static final String getBookListUrl(String uid) {
        return url + "library/borrowed?id=" + uid ;
    }
    public static final String searchBookUrl(String bookName){
        return url + "library/search?name=" + bookName ;
    }
    public static final String renewBookUrl(int uid){
        return url + "library/renew?id=" + uid ;
    }
    public static final String renewOneBookUrl(int uid,String bookId,String checkCode){
        return url + "library/renewOne?id=" + uid + "&bookId="+ bookId + "&verifyId=" + checkCode;
    }
    public static final String urlUpdate = url + "/user/update";

    public static String getNewsUrl(int index) {
        String site;
        switch (index) {
            case 0:
                site = "sduOnline";
                break;
            case 1:
                site = "underGraduate";
                break;
            case 2:
                site = "sduYouth";
                break;
            case 3: default:
                site = "sduView";
                break;
        }
        return "https://sduonline.cn/isdu/news/api/index.php?site=" + site;
    }

    public static final String uploadPostUrl = "http://202.194.15.133:8384/post/upload";

    public static String postCommentUrl = "http://202.194.15.133:8384/comment/add";

    /**
     *
     * @param index 版块ID
     * @param id 所处位置的ID
     * @return
     */
    public static String getNewsUrl(int index, int id) {
        String site;
        switch (index) {
            case 0:
                site = "sduOnline";
                break;
            case 1:
                site = "underGraduate";
                break;
            case 2:
                site = "sduYouth";
                break;
            case 3: default:
                site = "sduView";
                break;
        }
        return "https://sduonline.cn/isdu/news/api/index.php?site=" + site + "&content&id=" + id;
    }


    public static String getGradeUrl(String id){
        return url+"/academic/curTerm?id="+id;
    }

    public static String getPastGradeUrl(String id,String term) {
        return url + "/academic/termScore?id=" + id + "&termId=" + term;
    }


    public static String getExamUrl(int id) {
        return url + "/academic/schedule?id=" + id;
    }

    public static String getScheduleUrl(int id) {
        return url + "/academic/table?id=" + id;

    }

    public static String getSchoolBusUrl(String from, String to, int isWeekend) {
        return "https://sduonline.cn/isdu/schoolbus/api/?start=" + from + "&end=" + to + "&isWeekend=" + isWeekend;
    }

    public static String getStudyRooms(String campus) {
        return "http://sduonline.cn/isdu/studyroom/api/?campus=" + campus;
    }

    public static String getLibrarySearUrl(String loc) {
        return "https://sduonline.cn/isdu/library/api/?room=" + loc;
    }

    public static String getStudyRooms(String campus, String building, String date) {
        return "http://sduonline.cn/isdu/studyroom/api/?campus=" + campus + "&building=" + building + "&date=" + date;
    }

    public static String getPostList(int uid, int startId) {
        return "http://202.194.15.133:8384/post/getPostList10?startId=" + startId + "&userId=" + uid;
    }

    public static String getPost(int id) {
        return "http://202.194.15.133:8384/post/detail/?id=" + id;
    }

    public static String getLibraryInfoUrl(String id){
        return url + "library/info?id=" + id;
    }
    public static final String getBindUrl(String uid,String cardNumber, String pwd) {
        return url + "library/bind?id=" + uid + "&cardNumber=" + cardNumber + "&password=" + pwd;
    }
    public static final String getBookListUrl(String uid) {
        return url + "library/borrowed?id=" + uid ;
    }
    public static final String searchBookUrl(String bookName){
        return url + "library/search?name=" + bookName ;
    }
    public static final String renewBookUrl(int uid){
        return url + "library/renew?id=" + uid ;
    }
    public static final String renewOneBookUrl(int uid,String bookId,String checkCode){
        return url + "library/renewOne?id=" + uid + "&bookId="+ bookId + "&verifyId=" + checkCode;
    }
}
