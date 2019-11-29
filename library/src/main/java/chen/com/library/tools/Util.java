package chen.com.library.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;
import java.lang.reflect.Field;

public class Util {


    public static boolean CheckPermission(String permission, Context context) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static void requestPermission(String[] permission, Activity activity, int code) {
        ActivityCompat.requestPermissions(activity, permission, code);
    }

    public static int randomColor() {
        int c = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        return c;
    }


    /**
     * @param array_resource 数组Id
     * @param index          数组角标
     * @param context        上下文
     * @see Util#getIntegerResources(int, int, Context)
     */
    public static int getIntegerResources(int index, int array_resource, Context context) {
        TypedArray array = context.getResources().obtainTypedArray(array_resource);
        int resourceId = array.getResourceId(index, 0);
        return resourceId;
    }

    /**
     * 读取bitmap
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = 2;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 缩放
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 合并
     */
    public static Bitmap mergrBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight() + bitmap2.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);

        return bitmap3;
    }

    /**
     * 获取view的图
     */
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 复制
     */
    private void createBitmap(Bitmap bitmap) {
        Bitmap bigBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas bigCanvas = new Canvas(bigBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        bigCanvas.drawPaint(paint);

        Matrix matrix = new Matrix();
        matrix.setScale(0.8f, 0.8f);
        //原图绘制
        bigCanvas.drawBitmap(bigBitmap, 0, 0, paint);
    }

    /**
     * 状态栏高度
     */
    @SuppressLint("PrivateApi")
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar =context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();

        }
        return sbar;
    }

    public Bitmap shotRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int item_hright;
            Paint paint = new Paint();
            Canvas bigCanvas = null;
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                Bitmap drawingCache = getViewBitmap(holder.itemView);
                item_hright = holder.itemView.getMeasuredHeight();
                if (bigBitmap == null) {
                    bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), item_hright * (size - 1), Bitmap.Config.RGB_565);
                }

                if (bigCanvas == null) {
                    bigCanvas = new Canvas(bigBitmap);
                }
                paint.setColor(Color.WHITE);
                bigCanvas.drawPaint(paint);
                bigCanvas.drawBitmap(drawingCache, 0, i * item_hright, paint);
                recycleBitmap(drawingCache);
            }
        }
        return bigBitmap;
    }

    private void recycleBitmap(Bitmap drawingCache) {
        if (!drawingCache.isRecycled()) {
            drawingCache.recycle();
            drawingCache = null;
        }
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dipValue, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, int value) {
        return ((int) (value * context.getResources().getDisplayMetrics().density));
    }


    public static String formatTime(int datal) {
        int hours = datal / 3600;
        int minute = (datal / 60) - (hours * 60);
        int second = datal - (hours * 60 * 60) - (minute * 60);
        String s_hours;
        String s_minute;
        String s_second;
        if (hours < 10) {
            s_hours = "0" + String.valueOf(hours);
        } else {
            s_hours = String.valueOf(hours);
        }

        if (minute < 10) {
            s_minute = "0" + String.valueOf(minute);
        } else {
            s_minute = String.valueOf(minute);
        }

        if (second < 10) {
            s_second = "0" + String.valueOf(second);
        } else {
            s_second = String.valueOf(second);
        }

        return s_hours + ":" + s_minute + ":" + s_second;
    }

    public static void hideKeyBord(Activity context) {
        try {
            ((InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(context.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static void showKeyBoard(EditText editText) {
        InputMethodManager methodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (methodManager != null) {
            editText.requestFocus();
            methodManager.showSoftInput(editText, 0);
        }
    }

    public static void hideKeyBord(EditText view) {

        try {
            InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null)
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }


    }

}
