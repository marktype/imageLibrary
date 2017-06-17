package com.example.mylibrary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by fj on 2017/6/16.
 * 网络权限管理
 */

public class NetManager {

    private static NetManager netManager = new NetManager();

    private NetManager() {

    }

    public static NetManager getNetManagerInstance() {
        return netManager;
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查网络是否连接（没有判断是什么类型的网络）
     *
     * @param context
     */
    public void checkNetConnection(Context context) {
        if (!isConnected(context)) {
            showToast(context, "请连接网络！");
        } else {
            showToast(context, "已经连接网络！");
        }
    }

    /**
     * 判断wifi是否可用
     *
     * @param context
     * @return
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            //获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空 并且类型是否为MOBILE
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return networkInfo.isAvailable();
        }
        return false;
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

    private Toast toast;

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
//            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 网络未连接时，调用设置方法
     */
    public void setNetwork(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    /**
     * 检查网络是否连接（区别是wifi还是移动网络）
     *
     * @param context
     */
    public void checkNetworkConnection(Context context) {
        boolean wifiConnected = false;
        boolean mobileConnected = false;
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Log.e("tag", "get Wifi connection");
            } else if (mobileConnected) {
                Log.e("tag", "get Mobile connection");
            }
        } else {
            Log.e("tag", "no Mobile 0r no Wifi connection");
        }
        // END_INCLUDE(connect)
    }

    /**
     * 获取手机ip地址
     * 加上网络请求权限（否则使用移动网络的时候无法获取手机ip，wifi则不受影响）
     * <uses-permission android:name="android.permission.INTERNET"/>
     * @param context
     * @return
     */
    public String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            showToast(context,"当前无网络，请检查网络");
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
