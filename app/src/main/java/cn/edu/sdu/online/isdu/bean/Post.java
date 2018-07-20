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
    private String content; // 具体内容

    public Post() {}

    public Post(int type, String content) {
        this.type = type;
        this.content = content;
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
