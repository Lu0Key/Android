package cn.edu.sdu.online.isdu.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.sdu.online.isdu.net.ServerInfo;
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess;
import cn.edu.sdu.online.isdu.util.Logger;
import cn.edu.sdu.online.isdu.util.ScheduleTime;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    public static List<List<List<Schedule>>> localScheduleList;

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

    /**
     * 本地读取日程
     */
    public static List<List<List<Schedule>>> load(Context context) {
        SharedPreferences sp = context.getSharedPreferences("schedule_list", Context.MODE_PRIVATE);
        String jsonString = sp.getString("schedules", "");
        if (jsonString.equals("")) {
            loadFromNet(context);
            return new ArrayList<>();
        }
        return load(jsonString);
    }

    public static List<List<List<Schedule>>> load(String jsonString) {
        List<List<List<Schedule>>> sList = new ArrayList<>();
        try {
            JSONArray weeksArray = new JSONArray(jsonString); // startWeek到endWeek
            for (int i = 0; i < weeksArray.length(); i++) {
                List<List<Schedule>> iList = new ArrayList<>();

                JSONArray daysArray = weeksArray.getJSONArray(i); // 周一到周日

                for (int j = 0; j < daysArray.length(); j++) {
                    List<Schedule> jList = new ArrayList<>();


                    JSONArray dayArray = daysArray.getJSONArray(j); // 某一天
                    for (int k = 0; k < dayArray.length(); k++) {
                        Schedule schedule = new Schedule();
                        JSONObject jsonObject = dayArray.getJSONObject(k);

                        schedule.setScheduleName(jsonObject.getString("schedule_name"));
                        schedule.setScheduleColor(jsonObject.getInt("schedule_color"));
                        schedule.setScheduleLocation(jsonObject.getString("schedule_location"));
                        schedule.setScheduleTextColor(jsonObject.getInt("schedule_text_color"));
                        schedule.setStartTime(new ScheduleTime(
                                jsonObject.getInt("start_time_hour"),
                                jsonObject.getInt("start_time_minute")
                        ));
                        schedule.setEndTime(new ScheduleTime(
                                jsonObject.getInt("end_time_hour"),
                                jsonObject.getInt("end_time_minute")
                        ));
                        int rtype = jsonObject.getInt("repeat_type");
                        schedule.setRepeatType(rtype == 0 ? RepeatType.ONCE :
                                (rtype == 1 ? RepeatType.DAILY : RepeatType.WEEKLY));

                        String repeatWeeks = jsonObject.getString("repeat_weeks");
                        String[] weeks = repeatWeeks.split(",");
                        List<Integer> wl = new ArrayList<>();
                        for (String num : weeks) {
                            wl.add(Integer.parseInt(num));
                        }
                        schedule.setRepeatWeeks(wl);

                        jList.add(schedule);
                    }


                    iList.add(jList);
                }

                sList.add(iList);
            }

            return sList;
        } catch (Exception e) {
            Logger.log(e);
            return sList;
        }
    }

    /**
     * 从网络读取日程
     */
    private static void loadFromNet(final Context context) {
        NetworkAccess.buildRequest(ServerInfo.url,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Logger.log(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        localScheduleList = load(response.body().string());
                        save(context);
                    }
                });
    }

    public static String parse() {
        try {
            JSONArray iArray = new JSONArray();
            for (int i = 0; i < localScheduleList.size(); i++) {
                JSONArray jArray = new JSONArray();
                for (int j = 0; j < localScheduleList.get(i).size(); j++) {
                    JSONArray kArray = new JSONArray();
                    for (int k = 0; k < localScheduleList.get(i).get(j).size(); k++) {
                        Schedule schedule = localScheduleList.get(i).get(j).get(k);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("schedule_name", schedule.scheduleName);
                        jsonObject.put("schedule_color", schedule.scheduleColor);
                        jsonObject.put("schedule_location", schedule.scheduleLocation);
                        jsonObject.put("schedule_text_color", schedule.scheduleTextColor);
                        jsonObject.put("start_time_hour", schedule.startTime.getHour());
                        jsonObject.put("start_time_minute", schedule.startTime.getMinute());
                        jsonObject.put("end_time_hour", schedule.endTime.getHour());
                        jsonObject.put("end_time_minute", schedule.endTime.getMinute());
                        jsonObject.put("repeat_type",
                                schedule.repeatType == RepeatType.ONCE ? 0 :
                                        (schedule.repeatType == RepeatType.DAILY ? 1 : 2));

                        StringBuilder sb = new StringBuilder("");
                        for (Integer week : schedule.repeatWeeks) sb.append(week + ",");
                        String s = sb.toString();
                        jsonObject.put("repeat_weeks", s.substring(0, s.length() - 1));

                        kArray.put(jsonObject);
                    }
                    jArray.put(kArray);
                }
                iArray.put(jArray);
            }

            return iArray.toString();

        } catch (Exception e) {
            Logger.log(e);
        }
        return "";
    }

    public static void save(Context context) {
        if (localScheduleList == null) return;
        SharedPreferences.Editor editor =
                context.getSharedPreferences("schedule_list", Context.MODE_PRIVATE).edit();
        editor.putString("schedules", parse());
        editor.apply();
    }

}
