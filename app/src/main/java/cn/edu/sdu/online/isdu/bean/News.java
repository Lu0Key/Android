package cn.edu.sdu.online.isdu.bean;

import java.util.List;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/15
 *
 * 新闻实体类
 ****************************************************
 */

public class News {

    private String title; // 新闻标题
    private String briefContent; // 新闻内容摘要
    private String newsContent; // 新闻完整内容
    private String date; // 新闻日期
    private String source; // 新闻来源或类别
    private String section; // 新闻所属板块
    private String url; // 新闻链接地址
    private List<String> extras; // 附件名称
    private List<String> extraUrl; // 附件下载地址

    public News() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefContent() {
        return briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    public List<String> getExtraUrl() {
        return extraUrl;
    }

    public void setExtraUrl(List<String> extraUrl) {
        this.extraUrl = extraUrl;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public static News loadFromString(String json) {
        News news = new News();


        return news;
    }
}
