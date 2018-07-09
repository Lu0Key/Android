package cn.edu.sdu.online.isdu.bean;

/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/6/23
 *
 * 图书馆书籍信息
 ****************************************************
 */

public class Book {

    private String bookName; // 书名
    private String idNumber; // 书本编号
    private String bookPlace; // 借阅地点
    private String borrowDate; // 借阅日期
    private String backDate; // 应还日期
    private int remainDays; // 剩余天数
    private int borrowTimes; // 续借次数

    public Book(){}

    public void setBookName(String bookName) {
        this.bookName=bookName;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public void setBookPlace(String bookPlace) {
        this.bookPlace = bookPlace;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setBorrowTimes(int borrowTimes) {
        this.borrowTimes = borrowTimes;
    }

    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookPlace() {
        return bookPlace;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public int getBorrowTimes() {
        return borrowTimes;
    }

    public int getRemainDays() {
        return remainDays;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getBackDate() {
        return backDate;
    }

}
