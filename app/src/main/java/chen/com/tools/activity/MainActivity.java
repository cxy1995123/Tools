package chen.com.tools.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.woniu.com.eventbuse.RxBus.RxBusBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.activity.result.ActivityResultProxy;
import chen.com.library.data.TimeDate;
import chen.com.library.systembar.StatusBarCompat;

import chen.com.tools.R;


public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        findViewById(R.id.blocking).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "onClick: ");
            }
        });


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }

    private String getAge(Date birthDat) {
        TimeDate now = TimeDate.getInstance();
        TimeDate date = TimeDate.getInstance(birthDat);
        int age = 0;
        String unit = "岁";
        if(now.moreThan(date)) {
            if(date.daysBetween() > 365) {
                age = now.getYear() - date.getYear();
                if(age > 1) {
                    now.setYear(now.getYear() - (age - 1));
                    now.refreshTime();
                    if(now.daysBetween(date) < date.getYearCountDays()) {
                        age--;
                    }
                }
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void openCameraActivity(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static String getAgeString(Date birthDay) {
        String unit = "岁";

        Calendar nowCalendar = Calendar.getInstance();

        Calendar birthDayCalendar = Calendar.getInstance();

        birthDayCalendar.setTime(birthDay);
        if(nowCalendar.before(birthDay)) {
            return "";
        }
        int yearNow = nowCalendar.get(Calendar.YEAR);
        int monthNow = nowCalendar.get(Calendar.MONTH);
        int dayOfMonthNow = nowCalendar.get(Calendar.DAY_OF_MONTH);

        int yearBirth = birthDayCalendar.get(Calendar.YEAR);
        int monthBirth = birthDayCalendar.get(Calendar.MONTH);
        int dayOfMonthBirth = birthDayCalendar.get(Calendar.DAY_OF_MONTH);

        int age = 0;

        long newTime = nowCalendar.getTime().getTime() / 1000;
        long birthTime = birthDayCalendar.getTime().getTime() / 1000;

        if(birthTime > newTime) {
            return "";
        }

        int thisMonthMaxDay = nowCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(newTime - birthTime < 86400 * thisMonthMaxDay) {
            unit = "天";
            //未满月
            if(monthNow == monthBirth && yearBirth == yearNow) {
                //出生日期是这一年这一月
                age = nowCalendar.get(Calendar.DAY_OF_MONTH) - birthDayCalendar.get(Calendar.DAY_OF_MONTH);
            } else if(yearNow == yearBirth) {
                //出生日期是这一年跨月
                int day1 = nowCalendar.get(Calendar.DAY_OF_MONTH);
                int day2 = birthDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDayCalendar.get(Calendar.DAY_OF_MONTH);
                age = day1 + day2;
            }
            return age + unit;
        }

        int thisYearMaxDay = nowCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        if(newTime - birthTime < 86400 * thisYearMaxDay) {
            //未满一年
            unit = "月";
            if(yearNow == yearBirth) {
                age = monthNow - monthBirth;
            } else {
                age = 11 - monthBirth + monthNow;
            }
            return age + unit;
        }
        unit = "岁";
        age = yearNow - yearBirth;
        if(monthNow <= monthBirth) {
            if(monthNow == monthBirth) {
                if(dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age + unit;
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(getAgeString(format.parse("2020-03-08")));
        System.out.println(getAgeString(format.parse("2020-02-08")));
    }


}
