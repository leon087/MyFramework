package cm.android.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.format.Formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 格式化工具
 */
public class MyFormatter {

//    private static char ff[] = {'A', 'B', 'C', 'D', 'E', 'F'};

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "yyyyMMddHHmmss";

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long fileLength) {
        int sub_index = 0;
        String show = "";
        if (fileLength >= 1073741824) {
            sub_index = (String.valueOf((float) fileLength
                    / (1024 * 1024 * 1024))).indexOf(".");
            show = ((float) fileLength / (1024 * 1024 * 1024) + "000")
                    .substring(0, sub_index + 3) + "GB";
        } else if (fileLength >= 1048576) {
            sub_index = (String.valueOf((float) fileLength / (1024 * 1024)))
                    .indexOf(".");
            show = ((float) fileLength / (1024 * 1024) + "000").substring(0,
                    sub_index + 3) + "MB";
        } else if (fileLength >= 1024) {
            sub_index = (String.valueOf((float) fileLength / 1024))
                    .indexOf(".");
            show = ((float) fileLength / 1024 + "000").substring(0,
                    sub_index + 3) + "KB";
        } else if (fileLength < 1024) {
            show = String.valueOf(fileLength) + "B";
        }
        return show;
    }

    /**
     * 系统函数，字符串转换 long -String (kb)
     */
    @TargetApi(3)
    public static String formateFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    private static SimpleDateFormat getFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    /**
     * 格式化日期成"yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDate(long time) {
        // 2009-10-09 10:15:55
        SimpleDateFormat dateFormat = getFormat(FORMAT);
        return dateFormat.format(new Date(time));
    }

    /**
     * 格式化日期成"yyyyMMddHHmmss"
     */
    public static String formatDate2(long time) {
        SimpleDateFormat dateFormat = getFormat(FORMAT_2);
        return dateFormat.format(new Date(time));
    }

    /**
     * 按自定义格式格式化时间
     */
    public static String formatDate(String formatStr, long time) {
        SimpleDateFormat dateFormat = getFormat(formatStr);
        return dateFormat.format(new Date(time));
    }
}
