package chen.com.tools;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public interface IMChatView {

    Activity getActivity();

    void onKeyBoardChange(boolean isShow, int keyBoardHealth);

    View getEmojView();

    void showEmojView();

    void hideEmojView();

    void hideKeyBoard();

    void updateSoftInputMethod(int softInputMode);

    EditText getInputEditText();

    int getLayoutRes();

}
