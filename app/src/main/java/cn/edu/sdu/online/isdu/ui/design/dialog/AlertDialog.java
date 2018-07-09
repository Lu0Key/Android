package cn.edu.sdu.online.isdu.ui.design.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import cn.edu.sdu.online.isdu.R;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/5
 *
 * 提示对话框，拥有是否两个选项
 ****************************************************
 */

public class AlertDialog extends Dialog {

    private TextView txtTitle;
    private TextView txtMessage;
    private TextView btnPositive;
    private TextView btnNegative;
    private View blankView;

    private String title;
    private String message;
    private String pos;
    private String neg;

    private View.OnClickListener onPositiveButtonClickListener;
    private View.OnClickListener onNegativeButtonClickListener;

    public AlertDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    public AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_dialog_alert);

        initView();
        setCancelOnTouchOutside(true);
    }

    private void initView() {
        txtTitle = findViewById(R.id.txt_title);
        txtMessage = findViewById(R.id.txt_msg);
        btnPositive = findViewById(R.id.btn_positive);
        btnNegative = findViewById(R.id.btn_negative);
        blankView = findViewById(R.id.blank_view);

        txtTitle.setText(title);
        txtMessage.setText(message);
        btnPositive.setText(pos);
        btnPositive.setOnClickListener(onPositiveButtonClickListener);
        btnNegative.setText(neg);
        btnNegative.setOnClickListener(onNegativeButtonClickListener);

    }

    private void setCancelOnTouchOutside(boolean b) {
        if (b) {
            blankView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            blankView.setOnClickListener(null);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (txtTitle != null) {
            txtTitle.setText(title);
        }
    }

    public void setMessage(String message) {
        this.message = message;
        if (txtMessage != null) {
            txtMessage.setText(message);
        }
    }

    public void setPositiveButton(String text, View.OnClickListener listener) {
        pos = text;
        onPositiveButtonClickListener = listener;

        if (btnPositive != null) {
            btnPositive.setText(pos);
            btnPositive.setOnClickListener(onPositiveButtonClickListener);
        }
    }

    public void setNegativeButton(String text, View.OnClickListener listener) {
        neg = text;
        onNegativeButtonClickListener = listener;

        if (btnPositive != null) {
            btnNegative.setText(neg);
            btnNegative.setOnClickListener(onNegativeButtonClickListener);
        }
    }
}
