package chen.com.library.view;

import android.content.Context;
import android.os.Parcel;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import chen.com.library.R;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class TagEditText extends AppCompatEditText implements View.OnKeyListener, InputFilter {

    public TagEditText(Context context) {
        this(context, null);
    }

    public TagEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public TagEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFilters(new InputFilter[]{this});
        setOnKeyListener(this);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals("@")) {
            if (listener != null) {
                TagSpan tagSpan = listener.onAddTagSpan(TagEditText.this);
                int start1 = 0;
                int end1 = tagSpan.msg.length();
                SpannableString spannableString = new SpannableString(tagSpan.msg);
                spannableString.setSpan(tagSpan, start1, end1, SPAN_EXCLUSIVE_EXCLUSIVE);
                append(spannableString);
                return "";
            } else {
                return "@";
            }
        }
        return source;
    }

    public interface OnAddTagListener {
        TagSpan onAddTagSpan(EditText editText);
    }

    private OnAddTagListener listener;

    public void setListener(OnAddTagListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            int start = getSelectionStart();
            Editable editable = getText();
            if (editable == null) {
                return false;
            }
            TagSpan[] spans = editable.getSpans(start, start, TagSpan.class);
            if (spans != null && spans.length > 0) {
                TagSpan span = spans[0];
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                editable.delete(spanStart, spanEnd);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static class TagSpan extends ForegroundColorSpan {

        private String msg;

        public TagSpan(Context context, String msg) {
            super(ContextCompat.getColor(context, R.color.design_default_color_primary));
            this.msg = msg;
        }

        public TagSpan(@NonNull Parcel src) {
            super(src);
            msg = src.readString();
        }
    }

}
