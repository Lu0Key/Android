package cn.edu.sdu.online.isdu.bean;

/**
 ****************************************************
 * @author Zsj
 * Last Modifier: Zsj
 * Last Modify Time: 2018/7/7
 *
 * 考试安排信息
 ****************************************************
 */

public class Exam {
    private String date;
    private String time;
    private String location;
    private String gradeRate;
    private String type;
    private String name;

    public Exam() {

    }

    public Exam(String date, String time, String location, String gradeRate, String type, String name) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.gradeRate = gradeRate;
        this.type = type;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGradeRate() {
        return gradeRate;
    }

    public void setGradeRate(String gradeRate) {
        this.gradeRate = gradeRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
