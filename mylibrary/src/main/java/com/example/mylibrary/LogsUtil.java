package com.example.mylibrary;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

/**
 * Created by fj on 2017/4/24.
 *
 */

public class LogsUtil {
    /**
     * 是否打开打印开关
     */
    private static Boolean isOpen  =  true;
    /**
     * 应用包名
     */
    private static String packageName = "package not init";
    private static LogsUtil logsUtil = new LogsUtil();
    private LogsUtil(){
    }
    public static LogsUtil getInstance(Boolean flag){
        isOpen = flag;
        return logsUtil;
    }

    /**
     * 初始化应用包名（在application中进行,可不进行初始化）
     * @param context
     */
    public void initLogsUtil(Context context){
        packageName = getAppProcessName(context);
    }

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取activity类名
     * @param context
     * @return
     */
    private static String getClassName(Context context){
        return context.getClass().getSimpleName();
    }

    /**
     * 获取fragment名字
     * @param fragment
     * @return
     */
    private static String getFragmentName(Fragment fragment){
       return fragment.getClass().getSimpleName();
    }

    /**----------------------------在activity中调用   LogsUtil.d(this,"测试11");------------------------------**/
    public static void d(Context context,String msg){
        if (isOpen){
            Log.d(packageName,getClassName(context)+"----"+msg);
        }
    }

    public static void i(Context context,String msg){
        if (isOpen){
            Log.d(packageName,getClassName(context)+"----"+msg);
        }
    }
    public static void w(Context context,String msg){
        if (isOpen){
            Log.d(packageName,getClassName(context)+"----"+msg);
        }
    }
    public static void e(Context context,String msg){
        if (isOpen){
            Log.d(packageName,getClassName(context)+"----"+msg);
        }
    }
    /**----------------------------在fragment中调用  LogsUtil.d(WoFragment.this,"测试11");------------------------------**/
    public static void d(Fragment fragment,String msg){
        if (isOpen){
            Log.d(packageName,getFragmentName(fragment)+"----"+msg);
        }
    }
    public static void i(Fragment fragment,String msg){
        if (isOpen){
            Log.d(packageName,getFragmentName(fragment)+"----"+msg);
        }
    }
    public static void w(Fragment fragment,String msg){
        if (isOpen){
            Log.d(packageName,getFragmentName(fragment)+"----"+msg);
        }
    }
    public static void e(Fragment fragment,String msg){
        if (isOpen){
            Log.d(packageName,getFragmentName(fragment)+"----"+msg);
        }
    }
}
