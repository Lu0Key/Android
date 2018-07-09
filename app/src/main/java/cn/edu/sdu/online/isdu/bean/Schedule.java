package cn.edu.sdu.online.isdu.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.sdu.online.isdu.util.ScheduleTime;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/7
 *
 * 日程的Java Bean
 ****************************************************
 */

public class Schedule implements Parcelable {

    public static final int[] defaultScheduleColor = new int[] {
        0xFF7F66, 0xFFCC66, 0x66E6FF, 0x7F66FF, 0xFF2970, 0x00EB9C
    }; // 默认提供的日程背景颜色

    public static final int defaultScheduleTextColor = 0xFFFFFF;

    private String scheduleName; // 事件名称
    private String scheduleLocation; // 事件地点
    private ScheduleTime startTime; // 事件开始时间
    private ScheduleTime endTime; // 事件结束时间
    private RepeatType repeatType; // 重复类型
    private int scheduleColor = 0xFF717DEB; // 日程背景颜色
    private int scheduleTextColor = 0xFFFFFFFF; // 日程文字颜色
    private List<Integer> repeatWeeks = new ArrayList<>();

    public Schedule() {}

    public Schedule(String scheduleName, String scheduleLocation, ScheduleTime startTime, ScheduleTime endTime, RepeatType repeatType) {
        this.scheduleName = scheduleName;
        this.scheduleLocation = scheduleLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatType = repeatType;
    }

    protected Schedule(Parcel in) {
        scheduleName = in.readString();
        scheduleLocation = in.readString();
        scheduleColor = in.readInt();
        scheduleTextColor = in.readInt();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getScheduleLocation() {
        return scheduleLocation;
    }

    public void setScheduleLocation(String scheduleLocation) {
        this.scheduleLocation = scheduleLocation;
    }

    public ScheduleTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ScheduleTime startTime) {
        this.startTime = startTime;
    }

    public ScheduleTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ScheduleTime endTime) {
        this.endTime = endTime;
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public int getScheduleColor() {
        return scheduleColor;
    }

    public void setScheduleColor(int scheduleColor) {
        this.scheduleColor = scheduleColor;
    }

    public int getScheduleTextColor() {
        return scheduleTextColor;
    }

    public void setScheduleTextColor(int scheduleTextColor) {
        this.scheduleTextColor = scheduleTextColor;
    }

    public List<Integer> getRepeatWeeks() {
        return repeatWeeks;
    }

    public void setRepeatWeeks(List<Integer> repeatWeeks) {
        this.repeatWeeks = repeatWeeks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(scheduleName);
        dest.writeString(scheduleLocation);
        dest.writeInt(scheduleColor);
        dest.writeInt(scheduleTextColor);
    }

    /**
     * 日程重复类型
     */
    public enum RepeatType implements Serializable {
        ONCE, DAILY, WEEKLY
    }

}
