package com.doloop.www.myappmgr.material;

import java.lang.Thread.UncaughtExceptionHandler;

import com.doloop.www.myappmgr.material.utils.L;

import android.content.Context;
import android.widget.Toast;
/**
 * 
 * @author zhaonan07
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler sINSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    /**
     * ��ȡʵ��
     * @return CrashHandlerʵ��
     */
    public static CrashHandler getInstance() {
        return sINSTANCE;
    }

    /**
     * ��ʼ��
     * @param ctx ����
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String errorMsg = BuildErrorMsg(ex);
        Toast.makeText(mContext, R.string.crash_msg, Toast.LENGTH_SHORT).show();
        L.getLogger().e(errorMsg, true);
        //System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());  
    }

    private String BuildErrorMsg(Throwable ex) {
        StringBuffer exceptionStr = new StringBuffer();
        if(ex != null)
        {
            exceptionStr.append(ex.getMessage());
            StackTraceElement[] elements = ex.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                exceptionStr.append(elements[i].toString());
            }
        }
        return exceptionStr.toString();
    }

    /**
     * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
     * 
     * @param ex
     * @return true:��������˸��쳣��Ϣ;���򷵻�false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        // new Handler(Looper.getMainLooper()).post(new Runnable() {
        // @Override
        // public void run() {
        // new AlertDialog.Builder(mContext).setTitle("��ʾ")
        // .setMessage("���������...").setNeutralButton("��֪����", null)
        // .create().show();
        // }
        // });

        return true;
    }
}