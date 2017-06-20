package com.example.mylibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by fj on 2017/6/20.
 * app提示信息、状态
 */

public class AppHintUtils {

    private static AppHintUtils netManager = new AppHintUtils();

    private AppHintUtils() {

    }

    public static AppHintUtils getHintInstance() {
        return netManager;
    }
    /**
     * 加载的进度条
     *
     * @param ev
     * @return
     */
    protected ProgressDialog mAlertDialog;

    /**
     * 四个重载的显示进度条的方法
     *
     * @return
     */
    public ProgressDialog showProgressDialog(Context context) {
        return showProgressDialog(context, "", "请稍后", null);
    }

    public ProgressDialog showProgressDialog(Context context, String pTitle, String pMessage) {
        return showProgressDialog(context, pTitle, pMessage, null);
    }

    public ProgressDialog showProgressDialog(Context context, String pTitle) {
        return showProgressDialog(context, pTitle, "请稍后", null);
    }

    public synchronized ProgressDialog showProgressDialog(Context context, String pTitle, String pMessage,
                                                          DialogInterface.OnCancelListener pCancelClickListener) {
        if (mAlertDialog != null) {
            mAlertDialog.setTitle(pTitle);
            mAlertDialog.setMessage(pMessage);
            return mAlertDialog;
        }
        mAlertDialog = ProgressDialog.show(context, pTitle, pMessage, true, true);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setOnCancelListener(pCancelClickListener);

        return mAlertDialog;
    }

    /**
     * 取消进度条
     */
    public synchronized void cancelProgressDialog() {
        if (mAlertDialog != null) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.cancel();
            }
            mAlertDialog = null;
        }
    }

    private static Toast toast;

    /**
     * 显示toast对话框
     *
     * @param msg
     */
    public void showToast(Context context,String msg) {
        if (msg == null) {
            return;
        }
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
//            toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
