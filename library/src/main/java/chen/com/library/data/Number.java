package chen.com.library.data;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 字符转数字以及大数字类型
 */

public class Number {

    private static final String TAG = Number.class.getName();

    private String numString;

    private static final BigDecimal INT_MAX_VALUE = new BigDecimal(Integer.MAX_VALUE);
    private static final BigDecimal INT_MIN_VALUE = new BigDecimal(Integer.MIN_VALUE);
    private static final BigDecimal LONG_MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
    private static final BigDecimal LONG_MIN_VALUE = new BigDecimal(Long.MIN_VALUE);

    private BigDecimal bigDecimal;

    private boolean isNumber;
    private boolean isInt;
    private boolean isFloat;

    public boolean matcherNumber() {
        return isNumber;
    }

    public Number(double numString) {
        this(Double.toString(numString));
    }

    public Number(float numString) {
        this(Float.toString(numString));
    }

    public Number(int numString) {
        this(Integer.toString(numString));
    }

    public Number(long numString) {
        this(Long.toString(numString));
    }

    public Number(String numString) {
        this.numString = numString;
        if (isNumber = matcherNumber(numString)) {
            bigDecimal = new BigDecimal(numString);
            isInt = matcherInteger();
            isFloat = matcherFloat();
        }
    }


    public boolean compareTo(String str) {
        if (bigDecimal != null) {
            return bigDecimal.compareTo(new BigDecimal(str)) > 0;
        }
        return false;
    }

    private boolean checkNumber(String numString) {
        if (!matcherNumber(this.numString)) {
            systemErrorPrint();
            return false;
        }

        if (!matcherNumber(numString)) {
            systemErrorPrint(numString);
            return false;
        }
        return true;
    }

    public String add(String numString) {
        if (!checkNumber(numString)) {
            return numString;
        }
        bigDecimal = bigDecimal.add(new BigDecimal(numString));
        numString = bigDecimal.stripTrailingZeros().toString();
        return numString;
    }

    public String subtract(String numString) {
        if (!checkNumber(numString)) {
            return numString;
        }
        bigDecimal = bigDecimal.subtract(new BigDecimal(numString));
        numString = bigDecimal.stripTrailingZeros().toString();
        return numString;
    }

    public String multiply(String numString) {
        if (!checkNumber(numString)) {
            return numString;
        }
        bigDecimal = bigDecimal.multiply(new BigDecimal(numString));
        numString = bigDecimal.stripTrailingZeros().toString();
        return numString;
    }

    public String divide(String numString) {
        if (!checkNumber(numString)) {
            return numString;
        }
        BigDecimal divideNumber = new BigDecimal(numString);
        if (numString.contains("0")) {
            return numString;
        }
        bigDecimal = bigDecimal.divide(divideNumber);
        numString = bigDecimal.stripTrailingZeros().toString();
        return numString;
    }

    private void systemErrorPrint() {
        Log.e(TAG, "source " + numString + " Not numbers");
    }

    private void systemErrorPrint(String numString) {
        Log.e(TAG, "target " + numString + " Not numbers");
    }

    public boolean isInt() {
        if (!matcherNumber(numString)) {
            return false;
        }

        if (bigDecimal.compareTo(INT_MAX_VALUE) < 0 && bigDecimal.compareTo(INT_MIN_VALUE) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLong() {
        if (!matcherNumber(numString)) {
            return false;
        }

        if (bigDecimal.compareTo(LONG_MAX_VALUE) < 0 && bigDecimal.compareTo(LONG_MIN_VALUE) > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 纯数字或者浮点数
     */
    private boolean matcherNumber(String numString) {
        if (TextUtils.isEmpty(numString)) return false;
        return Pattern.compile("[0-9]{1,}||[0-9]{1,}.[0-9]{1,}").matcher(numString).matches();
    }

    /**
     * 纯数字
     */
    private boolean matcherInteger() {
        if (TextUtils.isEmpty(numString)) return false;
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        return pattern.matcher(numString).matches();
    }

    /**
     * 浮点数
     */
    private boolean matcherFloat() {
        if (TextUtils.isEmpty(numString)) return false;
        Pattern pattern = Pattern.compile("[0-9]{1,}.[0-9]{1,}");
        return pattern.matcher(numString).matches();
    }


}
