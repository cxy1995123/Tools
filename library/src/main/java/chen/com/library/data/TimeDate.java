package chen.com.library.data;


import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间转换工具类
 *
 * @author chenXingYu
 */
public class TimeDate {
    public static final long DAY_OF_MILLISECOND = 86400000;

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DEFAULT_PATTERN, Locale.CHINA);
    private String dateStr;
    private Date date;
    private int year;
    private int month = 1;
    private int day = 1;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private int millisecond = 0;

    public static TimeDate getInstance() {
        return new TimeDate();
    }

    public static TimeDate getInstance(Long time) {
        return new TimeDate(time);
    }

    public static TimeDate getInstance(String time) {
        return new TimeDate(time);
    }

    public static TimeDate getInstance(Date time) {
        return new TimeDate(time, null);
    }

    private TimeDate() {
        this(null, null);
    }

    public TimeDate(String time) {
        this(null, time);
    }

    public TimeDate(Long time) {
        this(new Date(time), null);
    }


    private TimeDate(@Nullable Date date, @Nullable String str) {
        if (!TextUtils.isEmpty(str)) {
            setValue(str);
        } else if (date != null) {
            initDate(date);
        } else {
            initDate(null);
        }

    }

    private void initDate(@Nullable Date date) {
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                divisionStringTime(dateStr);
                String string = getTimeString();
                this.date = SIMPLE_DATE_FORMAT.parse(string);
            } catch (ParseException e) {
                this.date = new Date();
                dateStr = SIMPLE_DATE_FORMAT.format(date);
                divisionStringTime(dateStr);
            }
        } else if (date != null) {
            this.date = date;
            dateStr = SIMPLE_DATE_FORMAT.format(date);
            divisionStringTime(dateStr);
        } else {
            this.date = new Date();
            dateStr = SIMPLE_DATE_FORMAT.format(this.date);
            divisionStringTime(dateStr);
        }

    }

    /**
     * 分解字符串时间
     **/
    private void divisionStringTime(String date) {
        Matcher matcher = Pattern.compile("[0-9]+").matcher(date);
        for (int i = 0; matcher.find(); i++) {
            String var = matcher.group();
            switch (i) {
                case 0:
                    year = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1970;
                    break;
                case 1:
                    month = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 2:
                    day = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 3:
                    hour = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 4:
                    minute = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 5:
                    second = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 6:
                    millisecond = TextUtils.isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
            }

        }
    }

    /**
     * 自定义时间组合成标准时间格式
     **/
    private String getTimeString() {
        return (year == 0 ? String.valueOf(1970) : String.valueOf(year)) + "-" +
                valueOf(month) + "-" + valueOf(day) + " " +
                valueOf(hour) + ":" + valueOf(minute) + ":" +
                valueOf(second);
    }


    /**
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 返回自定义格式时间字符串
     **/
    public String getTime(String pattern) {
        if (date == null) {
            Log.e("TimeDate", "getTime: " + "data is null");
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return dateFormat.format(date);
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return getTime(DEFAULT_PATTERN);
    }

    private String valueOf(int value) {
        return value < 10 ? "0" + String.valueOf(value) : String.valueOf(value);
    }

    public void setValue(String var) {
        this.dateStr = var;
        initDate(null);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    /**
     * 是否大于某个时间
     */
    public boolean greaterThan(TimeDate date) {
        return compareTo(date) == 1;
    }


    /**
     * 距离该日期过了多少天
     */
    public long daysBetween(@Nullable TimeDate date) {
        if (date == null) {
            date = new TimeDate();
        }
        long daysBetween;

        if (this.date == null || date.date == null) {
            daysBetween = 0;
        } else {
            long aThisdateTime = this.date.getTime();
            long aDatedateTime = date.date.getTime();
            if (aThisdateTime > aDatedateTime) {
                daysBetween = (aThisdateTime - aDatedateTime) / (1000L * 3600L * 24L);
            } else {
                daysBetween = (aDatedateTime - aThisdateTime) / (1000L * 3600L * 24L);
            }
        }
        return daysBetween;
    }

    /**
     * 距离当前日期过了多少天
     */
    public long daysBetween() {
        return daysBetween(null);
    }


    /**
     * @param day 需要减去的天数
     */
    public void subtract(int day) {
        if (date == null || day == 0) return;
        date = new Date(date.getTime() - (day * DAY_OF_MILLISECOND));
        dateStr = SIMPLE_DATE_FORMAT.format(date);
        divisionStringTime(dateStr);

    }


    /**
     * 比较两个时间的大小
     *
     * @return 大于  1 等于0 小于-1
     */
    public int compareTo(TimeDate date) {
        if (date == null) return 1;
        if (date.getDate() == null) return 1;
        if (this.date == null) return -1;
        long offset = this.date.getTime() - date.getDate().getTime();
        if (offset > 0) {
            return 1;
        }
        if (offset < 0) {
            return -1;
        }
        return 0;
    }


    /**
     * 是否是同一年
     */
    public boolean isSameYear(TimeDate date) {
        if (date == null) return false;
        return date.getYear() == year;
    }

    /**
     * 是否是同月
     */
    public boolean isSameMonth(TimeDate date) {
        if (date == null) return false;
        return isSameYear(date) && date.getMonth() == month;
    }

    /**
     * 是否是同月
     */
    public boolean isSameDay(TimeDate date) {
        if (date == null) return false;
        return isSameMonth(date) && date.getDay() == day;
    }


    @Override
    public String toString() {
        return getTime();
    }

    public long getLongTime() {
        if (date != null) {
            return date.getTime();
        }
        return new Date().getTime();
    }

    /**
     * 获取指定月份 有多少天
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getDaysOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
