package cn.com.javalib.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDate2 {

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


    public static TimeDate2 build() {
        return build(new Date());
    }

    public static TimeDate2 build(String time) {
        if (isEmpty(time)) {
            return build();
        }
        return new TimeDate2(time);
    }

    public static TimeDate2 build(Date time) {
        if (time == null) {
            time = new Date();
        }
        return new TimeDate2(time);
    }

    public static TimeDate2 build(Long time) {
        return build(new Date(time));
    }

    public static TimeDate2 build(String time, String pattern) {
        if (isEmpty(time) || isEmpty(pattern)) return build();
        return new TimeDate2(time, pattern);
    }

    public static TimeDate2 getInstance() {
        return build();
    }

    public static TimeDate2 getInstance(Long time) {
        return build(time);
    }

    public static TimeDate2 getInstance(String time) {
        return build(time);
    }

    public static TimeDate2 getInstance(Date time) {
        return build(time);
    }


    public TimeDate2() {
        this(new Date());
    }

    public TimeDate2(Long time) {
        this(new Date(time));
    }

    private TimeDate2(String time, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
            date = format.parse(time);
            init(date);
        } catch (ParseException e) {
            date = new Date();
            init(date);
        }
    }

    private TimeDate2(String time) {
        divisionStringTime(time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        date = calendar.getTime();
        dateStr = SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    private TimeDate2(Date time) {
        this.date = time;
        init(time);
    }

    private void init(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        millisecond = calendar.get(Calendar.MILLISECOND);
        dateStr = SIMPLE_DATE_FORMAT.format(date);
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

    private boolean isDigitsOnly(String str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public void setValue(String var) {
        if (isEmpty(var)) return;
        divisionStringTime(var);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        dateStr = SIMPLE_DATE_FORMAT.format(calendar.getTime());
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
        init(date);
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        date = calendar.getTime();
        dateStr = SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * 是否大于某个时间
     */
    public boolean moreThan(TimeDate2 date) {
        return compareTo(date) == 1;
    }

    /**
     * 比较两个时间的大小
     *
     * @return 大于  1 等于0 小于-1
     */
    public int compareTo(TimeDate2 date) {
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


    public Date getDate() {
        return date;
    }

    /**
     * 距离该日期过了多少天
     */
    public long daysBetween(TimeDate2 date) {
        if (date == null) {
            date = new TimeDate2();
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
        init(date);
    }

    public void plus(int day) {
        if (date == null || day == 0) return;
        date = new Date(date.getTime() + (day * DAY_OF_MILLISECOND));
        init(date);
    }

    /**
     * 是否是同一年
     */
    public boolean isSameYear(TimeDate2 date) {
        if (date == null) return false;
        return date.getYear() == year;
    }

    /**
     * 是否是同月
     */
    public boolean isSameMonth(TimeDate2 date) {
        if (date == null) return false;
        return isSameYear(date) && date.getMonth() == month;
    }

    /**
     * 是否是同月
     */
    public boolean isSameDay(TimeDate2 date) {
        if (date == null) return false;
        return isSameMonth(date) && date.getDay() == day;
    }

    /**
     * 获取指定月份 有多少天
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当月有多少天
     */
    public int getDaysOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public String getTime(String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return dateFormat.format(date);
    }

    public String getTime() {
        return getTime(DEFAULT_PATTERN);
    }

    public String getSimpleTime() {
        return getTime(SIMPLE_PATTERN);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (obj instanceof TimeDate2) {
            return compareTo((TimeDate2) obj) == 0;
        }

        return false;

    }

    public long getLongTime() {
        if (date != null) {
            return date.getTime();
        }
        return new Date().getTime();
    }

    public String getAge() {
        if (date == null) return "";
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(date);
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        // 按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。

        if (date == null || birthday.after(now)) {
            return "未知年龄";
        }

        if (day < 0) {
            month -= 1;
            now.add(Calendar.MONTH, -1);// 得到上一个月，用来得到上个月的天数。
            day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (month < 0) {
            month = (month + 12) % 12;
            year--;
        }

        if (year > 0) {
            return year + "岁";
        }

        if (month > 0) {
            return month + "月";
        }

        if (day > 0) {
            return day + "天";
        }

        if (year == 0 && month == 0 && day == 0) {
            return "0天";
        }
        return "";

    }

    @Override
    public int hashCode() {
        return Objects.hash(dateStr);
    }

    @Override
    public String toString() {
        return dateStr;
    }
}
