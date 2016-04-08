package cm.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import cm.java.util.IoUtil;

/**
 * 桌面快捷方式有关的工具类
 */
public class ShortcutUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShortcutUtil.class);
    //private static final String HTC = "HTC";

    /**
     * 快捷方式添加的action
     */
    private final static String SHORTCUT_ADD_ACTION
            = "com.android.launcher.action.INSTALL_SHORTCUT";

    /**
     * 快捷方式删除的action
     */
    private final static String SHORTCUT_DEL_ACTION
            = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    /**
     * 读取数据库需要的权限
     */
    private final static String READ_SETTINGS_PERMISSION
            = "com.android.launcher.permission.READ_SETTINGS";


    /**
     * 添加快捷方式到桌面，添加快捷方式需要添加用户权限
     * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
     */
    public static void addShortCut(Context context, String shortCutName, int resourceId,
                                   Class<?> cls) {
        Intent shortCutIntent = new Intent(SHORTCUT_ADD_ACTION);
        //添加快捷方式的名字
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        //不允许重复添加
        shortCutIntent.putExtra("duplicate", false);

        //指定当前的Activity为快捷方式启动的对象
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setClass(context, cls));

        //添加快捷方式的图标
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(context, resourceId);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        context.sendBroadcast(shortCutIntent);
    }

    @TargetApi(4)
    public static void addAppShortcut(Context context, Class<?> cls) {
        String label = context.getApplicationInfo().loadLabel(context.getPackageManager())
                .toString();
        int iconRes = context.getApplicationInfo().icon;
        addShortCut(context.getApplicationContext(), label, iconRes, cls);
    }

    /**
     * 删除桌面上的快捷方式，需要添加权限
     * <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
     */
    @TargetApi(3)
    public static void delShortcut(Context context) {
        Intent shortcut = new Intent(SHORTCUT_DEL_ACTION);
        // 获取当前应用名称
        String appName = null;
        try {
            appName = obtatinAppName(context);
        } catch (NameNotFoundException e) {
            logger.error("", e);
        }
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        //Intent shortcutIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .setClass(context, context.getClass());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断桌面上是否有快捷方式，调用此方法需要添加权限
     * <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
     */
    public static boolean hasShortcut(Context context) {

        String PERMISSIONS = "com.android.launcher.permission.READ_SETTINGS";
        //String HTC_PERMISSIONS = "com.htc.launcher.permission.READ_SETTINGS";
        String AUTHORITY = "com.android.launcher2.settings";
        if (!EnvironmentUtil.SdkUtil.hasFroyo()) {
            AUTHORITY = "com.android.launcher.settings";
        }

        /*
        String mobileVersion = Build.MODEL;
        if (mobileVersion.toUpperCase().contains(HTC)) {
            AUTHORITY = getAuthorityFromPermission(context, HTC_PERMISSIONS);
        } else {
            AUTHORITY = getAuthorityFromPermission(context, PERMISSIONS);
        }
        */
        AUTHORITY = getAuthorityFromPermission(context, PERMISSIONS);
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        String appName = null;
        try {
            appName = obtatinAppName(context);
        } catch (NameNotFoundException e) {
            logger.error("", e);
        }
        Cursor c = null;
        try {
            c = context.getContentResolver()
                    .query(CONTENT_URI, new String[]{"title"}, "title=?", new String[]{appName},
                            null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
            return false;
        } finally {
            IoUtil.closeQuietly(c);
        }
    }


    private static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager()
                .getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission
                            .equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取应用的名称
     */
    @TargetApi(4)
    private static String obtatinAppName(Context context) throws NameNotFoundException {
//        PackageManager packageManager = context.getPackageManager();
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
//        return packageManager.getApplicationLabel(packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)).toString();
    }
}