package chen.com.library.data;

import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间转换工具类
 *
 * @author chenXingYu
 */
@SuppressWarnings("WeakerAccess")
public class TimeDate{
    private static final long DAY_OF_MILLISECOND = 86400000;

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String SIMPLE_PATTERN = "yyyy-MM-dd";

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
        if(!isEmpty(str)) {
            setValue(str);
        } else if(date != null) {
            initDate(date);
        } else {
            initDate(null);
        }
    }

    private void initDate(@Nullable Date date) {
        if(!isEmpty(dateStr)) {
            try {
                divisionStringTime(dateStr);
                String string = getTimeString();
                this.date = SIMPLE_DATE_FORMAT.parse(string);
            } catch (ParseException e) {
                this.date = new Date();
                dateStr = SIMPLE_DATE_FORMAT.format(date);
                divisionStringTime(dateStr);
            }
        } else if(date != null) {
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
                    year = isDigitsOnly(var) ? Integer.valueOf(var) : 1970;
                    break;
                case 1:
                    month = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 2:
                    day = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 3:
                    hour = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 4:
                    minute = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 5:
                    second = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
                case 6:
                    millisecond = isDigitsOnly(var) ? Integer.valueOf(var) : 1;
                    break;
            }

        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static boolean isDigitsOnly(String str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if(!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 自定义时间组合成标准时间格式
     **/
    private String getTimeString() {
        return (year == 0 ? String.valueOf(1970) : String.valueOf(year)) + "-" + valueOf(month) + "-" + valueOf(day) + " " + valueOf(hour) + ":" + valueOf(minute) + ":" + valueOf(second);
    }


    /**
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 返回自定义格式时间字符串
     **/
    @SuppressWarnings("WeakerAccess")
    public String getTime(String pattern) {
        if(date == null) {
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

    public String getSimpleTime() {
        return getTime(SIMPLE_PATTERN);
    }

    private String valueOf(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
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

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void refreshTime() {
        StringBuilder builder = new StringBuilder();
        String monthStr;
        if(this.month < 10) {
            monthStr = "0" + this.month;
        } else {
            monthStr = String.valueOf(month);
        }

        String dayStr;
        if(this.day < 10) {
            dayStr = "0" + this.day;
        } else {
            dayStr = String.valueOf(day);
        }


        String hourStr;
        if(hour < 10) {
            hourStr = "0" + this.hour;
        } else {
            hourStr = String.valueOf(hour);
        }

        String minuteStr;
        if(minute < 10) {
            minuteStr = "0" + this.minute;
        } else {
            minuteStr = String.valueOf(minute);
        }

        String secondStr;
        if(second < 10) {
            secondStr = "0" + this.second;
        } else {
            secondStr = String.valueOf(second);
        }
        //yyyy-MM-dd HH:mm:ss
        builder.append(year).append("-").append(monthStr).append("-").append(dayStr);
        builder.append(" ").append(hourStr).append(":").append(minuteStr).append(":").append(secondStr);
        dateStr = builder.toString();
        initDate(null);
    }

    /**
     * 是否大于某个时间
     */
    public boolean moreThan(TimeDate date) {
        return compareTo(date) == 1;
    }


    /**
     * 距离该日期过了多少天
     */
    public long daysBetween(@Nullable TimeDate date) {
        if(date == null) {
            date = new TimeDate();
        }
        long daysBetween;

        if(this.date == null || date.date == null) {
            daysBetween = 0;
        } else {
            long aThisdateTime = this.date.getTime();
            long aDatedateTime = date.date.getTime();
            if(aThisdateTime > aDatedateTime) {
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
        if(date == null || day == 0) return;
        date = new Date(date.getTime() - (day * DAY_OF_MILLISECOND));
        dateStr = SIMPLE_DATE_FORMAT.format(date);
        divisionStringTime(dateStr);

    }

    private String getAge() {
        TimeDate now = TimeDate.getInstance();
        TimeDate date = this;
        int age = 0;
        String unit = "岁";
        if(now.moreThan(date)) {
            if(date.daysBetween() > 365) {
                age = now.getYear() - date.getYear();
                if(age < 1) {
                    //闰年366
                    age = 12;
                    unit = "月";
                }
            } else if(date.daysBetween() > 31) {
                //不足一岁 但是 大于 1月
                unit = "月";
                if(date.isSameYear(now)) {
                    //今年出生的
                    age = now.getMonth() - date.getMonth();
                } else {
                    age = 12 - date.getMonth() + now.getMonth();
                }
            } else {
                //不足一月的
                unit = "天";
                age = (int) date.daysBetween();
            }
        }
        return age + unit;
    }


    /**
     * 比较两个时间的大小
     *
     * @return 大于  1 等于0 小于-1
     */
    public int compareTo(TimeDate date) {
        if(date == null) return 1;
        if(date.getDate() == null) return 1;
        if(this.date == null) return -1;
        long offset = this.date.getTime() - date.getDate().getTime();
        if(offset > 0) {
            return 1;
        }
        if(offset < 0) {
            return -1;
        }
        return 0;
    }


    /**
     * 是否是同一年
     */
    public boolean isSameYear(TimeDate date) {
        if(date == null) return false;
        return date.getYear() == year;
    }

    /**
     * 是否是同月
     */
    public boolean isSameMonth(TimeDate date) {
        if(date == null) return false;
        return isSameYear(date) && date.getMonth() == month;
    }

    /**
     * 是否是同月
     */
    public boolean isSameDay(TimeDate date) {
        if(date == null) return false;
        return isSameMonth(date) && date.getDay() == day;
    }


    @Override
    public String toString() {
        return getTime();
    }

    public long getLongTime() {
        if(date != null) {
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

    public int getYearCountDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    public int getDaysOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(obj instanceof TimeDate) {
            return compareTo((TimeDate) obj) == 0;
        }

        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(dateStr);
    }
}
