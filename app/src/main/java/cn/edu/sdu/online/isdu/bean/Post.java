package cn.edu.sdu.online.isdu.bean;

import java.util.Objects;

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

    private int postId; // 帖子ID
    private int type; // 论坛内容类型
    private String titleFlag;
    private String title;
    private String uid;
    private int commentsNumbers;
    private Long time;
    private String content; // 具体内容
    private int likeNumber;
    private int collectNumber;

    public Post() {}

    public Post(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public Post(int postId, int type, String titleFlag, String title, String uid, int commentsNumbers, Long time, String content, int likeNumber, int collectNumber) {
        this.postId = postId;
        this.type = type;
        this.titleFlag = titleFlag;
        this.title = title;
        this.uid = uid;
        this.commentsNumbers = commentsNumbers;
        this.time = time;
        this.content = content;
        this.likeNumber = likeNumber;
        this.collectNumber = collectNumber;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public int getCollectNumber() {
        return collectNumber;
    }

    public void setCollectNumber(int collectNumber) {
        this.collectNumber = collectNumber;
    }

    public String getTitleFlag() {
        return titleFlag;
    }

    public void setTitleFlag(String titleFlag) {
        this.titleFlag = titleFlag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCommentsNumbers() {
        return commentsNumbers;
    }

    public void setCommentsNumbers(int commentsNumbers) {
        this.commentsNumbers = commentsNumbers;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postId == post.postId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId);
    }
}
