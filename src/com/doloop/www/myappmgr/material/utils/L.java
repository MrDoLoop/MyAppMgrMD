package com.doloop.www.myappmgr.material.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 打印log用的
 * 
 * @author zhaonan07
 *
 */

public class L {
    /**
     * log TAG
     */
    private static String TAG = "Logger";
    private static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static String FILE_NAME = "MyAppMgrCrash.log";

    private static String logFile = ROOT + FILE_NAME;

    private static int sLogLevel = Log.VERBOSE;

    /**
     * debug or not
     */
    // private static boolean debug = BuildConfig.DEBUG;

    private static boolean sWriteOnSd = false;

    private static L instance = new L();

    private L() {

    }

    public static String getLogFile(){
        return logFile;
    }
    
    /**
     * 获取logger实例
     * @return logger实例
     */
    public static L getLogger() {
        return instance;
    }
    /**
     * 设定新的logger
     * @param context 背景
     * @param fileName log文件的文件名
     * @return logger实例
     */
    public static L getLogger(Context context, String fileName) {
        TAG = context.getPackageName();
        FILE_NAME = fileName;
        logFile = ROOT + FILE_NAME;
        return instance;
    }

    /**
     * 全局log设定，是否写入到sdcard
     * 
     * @param flag 是否写入
     */
    public static void write2Sdcard(boolean flag) {
        sWriteOnSd = flag;
    }

    /**
     * 获取函数名称
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            TAG = st.getFileName();
            return "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + "): "
                    + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + "]";
        }

        return null;
    }

    private String createMessage(String msg) {
        String functionName = getFunctionName();
        String message = (functionName == null ? msg : (functionName + " - " + msg));
        return message;
    }
    /**
     * log.d
     * 
     * @param msg 消息
     */
    public static void d(String msg) {
        instance.debug(msg);
    }

    /**
     * log.e
     * 
     * @param msg 消息
     */
    public static void e(String msg) {
        instance.error(msg);
    }
    /**
     * log.e
     * 
     * @description log.e
     * @param e 消息
     */
    public static void e(Exception e) {
        instance.error(e);
    }

    /**
     * log.w
     * @param msg 消息
     */
    public static void w(String msg) {
        instance.warn(msg);
    }
    /**
     * log.w
     * @param e 消息
     */
    public static void w(Exception e) {
        instance.warn(e != null ? e.toString() : "null");
    }
    /**
     * log.i
     * @param msg 消息
     */
    public static void i(String msg) {
        instance.info(msg);
    }
    
    /**
     * log.v
     * @param msg 消息
     */
    public static void v(String msg) {
        instance.verbose(msg);
    }
    

    /**
     * log.i
     * @param msg 消息
     */
    public void info(String msg) {

        if (sLogLevel <= Log.INFO) {
            String message = createMessage(msg);
            Log.i(TAG, message);
            if (sWriteOnSd) {
                instance.writeLog(message);
            }
        }
    }

    /**
     * 最好不要使用，如果非要使用，可以通过LogUtils.getLogger之后进行操作
     * 
     * @param msg logMsg
     * @param write2sd 忽略全局设定，强制执行这里的true or false
     */
    public void i(String msg, boolean write2sd) {

        String message = createMessage(msg);

        Log.i(TAG, message);

        if (write2sd) {
            instance.writeLog(message);
        }
    }

    /**
     * 最好不要使用，如果非要使用，可以通过LogUtils.getLogger之后进行操作
     * 
     * @param msg logMsg
     * @param write2sd 忽略全局设定，强制执行这里的true or false
     */
    public void d(String msg, boolean write2sd) {
        String message = createMessage(msg);
        Log.d(TAG, message);
        if (write2sd) {
            instance.writeLog(message);
        }
    }

    /**
     * 最好不要使用，如果非要使用，可以通过LogUtils.getLogger之后进行操作
     * 
     * @param msg logMsg
     * @param write2sd 忽略全局设定，强制执行这里的true or false
     */
    public void e(String msg, boolean write2sd) {
        String message = createMessage(msg);
        Log.e(TAG, message);
        if (write2sd) {
            instance.writeLog(message);
        }
    }

    /**
     * 最好不要使用，如果非要使用，可以通过LogUtils.getLogger之后进行操作
     * 
     * @param msg logMsg
     * @param write2sd 忽略全局设定，强制执行这里的true or false
     */
    public void w(String msg, boolean write2sd) {
        String message = createMessage(msg);
        Log.w(TAG, message);
        if (write2sd) {
            instance.writeLog(message);
        }
    }

    /**
     * Log.v
     * @param msg 消息
     */
    public void verbose(String msg) {
        if (sLogLevel <= Log.VERBOSE) {
            String message = createMessage(msg);
            Log.v(TAG, message);
            if (sWriteOnSd) {
                instance.writeLog(message);
            }
        }
    }

    /*
     * public void v(String msg) { if (debug) { instance.verbose(msg); } if (sWriteOnSd) { instance.writeLog(msg); } }
     */

    /*
     * public void v(Exception e) { if (debug) { instance.verbose(e != null ? e.toString() : "null"); } if (sWriteOnSd)
     * { instance.writeLog(e.toString()); } }
     */

    /**
     * Log.d
     * @param msg 消息
     */
    public void debug(String msg) {
        if (sLogLevel <= Log.DEBUG) {
            String message = createMessage(msg);
            Log.d(TAG, message);
            if (sWriteOnSd) {
                instance.writeLog(msg);
            }
        }

    }

  /**
   * Log.e
   * @param msg 消息
   */
    public void error(String msg) {
        if (sLogLevel <= Log.ERROR) {
            String message = createMessage(msg);
            Log.e(TAG, message);
            if (sWriteOnSd) {
                instance.writeLog(message);
            }
        }
    }

    /**
     * Log.e
     * @param e Exception
     */
    public void error(Exception e) {
        if (sLogLevel <= Log.ERROR) {
            StringBuffer sb = new StringBuffer();
            String name = getFunctionName();
            StackTraceElement[] sts = e.getStackTrace();

            if (name != null) {
                sb.append(name + " - " + e + "\r\n");
            } else {
                sb.append(e + "\r\n");
            }
            if (sts != null && sts.length > 0) {
                for (StackTraceElement st : sts) {
                    if (st != null) {
                        sb.append("[ " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName()
                                + " ]\r\n");
                    }
                }
            }
            Log.e(TAG, sb.toString());
            if (sWriteOnSd) {
                instance.writeLog(sb.toString());
            }
        }

    }

    /**
     * Log.w 
     * @param msg 消息
     */
    public void warn(String msg) {
        if (sLogLevel <= Log.WARN) {
            String message = createMessage(msg);
            Log.w(TAG, message);
            if (sWriteOnSd) {
                instance.writeLog(message);
            }
        }

    }

    /**
     * 删除log文件
     */
    public static void delLogFile() {
        File file = new File(logFile);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLog(String content) {
        try {
            File file = new File(logFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // DateFormat formate = SimpleDateFormat.getDateTimeInstance();
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            FileWriter write = new FileWriter(file, true);
            write.write(formate.format(new Date()) + "   " + content + "\r\n");
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
