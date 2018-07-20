package cn.edu.sdu.online.isdu.bean;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/5/26
 *
 * 论坛帖子的Java Bean
 ****************************************************
 */

public class Post {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;

    private int type; // 论坛内容类型
    private String title_flag;//
    private String title;
    private String userName;
    private int comments_numbers;
    private Long time;
    private String content; // 具体内容

    public Post() {}

    public Post(int type, String title_flag, String title, String userName, int comments_numbers, Long time, String content) {
        this.type = type;
        this.title_flag = title_flag;
        this.title = title;
        this.userName = userName;
        this.comments_numbers = comments_numbers;
        this.time = time;
        this.content = content;
    }

    public String getTitle_flag() {
        return title_flag;
    }

    public void setTitle_flag(String title_flag) {
        this.title_flag = title_flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getComments_numbers() {
        return comments_numbers;
    }

    public void setComments_numbers(int comments_numbers) {
        this.comments_numbers = comments_numbers;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
