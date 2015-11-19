package cm.android.applications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.util.DisplayMetrics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cm.java.codec.HashUtil;
import cm.java.util.ObjectUtil;
import cm.java.util.Utils;

public class AppUtil {

    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);

    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter() {
        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationInfo info) {
            if (isSystemUpdateApp(info)) {
                return true;
            } else if (!isSystemApp(info)) {
                return true;
            }
            return false;
        }
    };

    /**
     * 用户应用
     */
    public static final int APP_USER = 0;

    ;

    /**
     * 系统应用
     */
    public static final int APP_SYSTEM = 1;

    /**
     * 所有应用（用户+系统）
     */
    public static final int APP_ALL = 2;

    /**
     * 获取已安装的应用
     *
     * @param flag {@link #APP_USER},{@link #APP_SYSTEM},{@link #APP_ALL}
     * @return PackageInfo列表
     */
    public static List<PackageInfo> getInstalledPackages(PackageManager pm,
            int flag) {
        List<PackageInfo> packages = getInstalledPackages(pm);

        List<PackageInfo> appList = new ArrayList<PackageInfo>();

        if (flag == APP_USER) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (THIRD_PARTY_FILTER.filterApp(packageInfo.applicationInfo)) {
                    appList.add(packageInfo);
                }
            }
            return appList;
        } else if (flag == APP_SYSTEM) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (isSystemApp(packageInfo.applicationInfo)) {
                    appList.add(packageInfo);
                }
            }
            return appList;
        } else {
            return packages;
        }
    }

    public static List<PackageInfo> getInstalledPackages(PackageManager pm) {
        int retrieveFlags = PackageManager.GET_UNINSTALLED_PACKAGES
                | PackageManager.GET_DISABLED_COMPONENTS;
        List<PackageInfo> packages = pm.getInstalledPackages(retrieveFlags);
        return packages;
    }

    public static List<ApplicationInfo> getInstalledApplications(
            PackageManager pm) {
        int retrieveFlags = PackageManager.GET_UNINSTALLED_PACKAGES
                | PackageManager.GET_DISABLED_COMPONENTS;
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(retrieveFlags);
        return packages;
    }

    /**
     * 获取已安装的应用
     *
     * @param flag {@link #APP_USER},{@link #APP_SYSTEM},{@link #APP_ALL}
     * @return ApplicationInfo列表
     */
    public static List<ApplicationInfo> getInstalledApps(PackageManager pm,
            int flag) {
        List<ApplicationInfo> apps = getInstalledApplications(pm);
        List<ApplicationInfo> appList = ObjectUtil.newArrayList();

        if (flag == APP_USER) {
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo applicationInfo = apps.get(i);
                if (THIRD_PARTY_FILTER.filterApp(applicationInfo)) {
                    appList.add(applicationInfo);
                }
            }
            return appList;
        } else if (flag == APP_SYSTEM) {
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo applicationInfo = apps.get(i);
                if (isSystemApp(applicationInfo)) {
                    appList.add(applicationInfo);
                }
            }
            return appList;
        } else {
            return apps;
        }
    }

    /**
     * 获取已安装的应用
     *
     * @param flag {@link #APP_USER},{@link #APP_SYSTEM},{@link #APP_ALL}
     */
    public static Map<String, ApplicationInfo> getInstalledAppsMap(
            PackageManager pm, int flag) {
        List<ApplicationInfo> apps = getInstalledApplications(pm);
        Map<String, ApplicationInfo> appMap = ObjectUtil.newHashMap();

        if (flag == APP_USER) {
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo applicationInfo = apps.get(i);
                if (THIRD_PARTY_FILTER.filterApp(applicationInfo)) {
                    appMap.put(applicationInfo.packageName, applicationInfo);
                }
            }
            return appMap;
        } else if (flag == APP_SYSTEM) {
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo applicationInfo = apps.get(i);
                if (isSystemApp(applicationInfo)) {
                    appMap.put(applicationInfo.packageName, applicationInfo);
                }
            }
            return appMap;
        } else {
            for (ApplicationInfo applicationInfo : apps) {
                appMap.put(applicationInfo.packageName, applicationInfo);
            }
            return appMap;
        }
    }

    /**
     * 获取已安装的应用
     *
     * @param flag {@link #APP_USER},{@link #APP_SYSTEM},{@link #APP_ALL}
     */
    public static Map<String, PackageInfo> getInstalledPackagesMap(
            PackageManager pm, int flag) {
        List<PackageInfo> packages = getInstalledPackages(pm);
        Map<String, PackageInfo> appMap = ObjectUtil.newHashMap();

        if (flag == APP_USER) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (THIRD_PARTY_FILTER.filterApp(packageInfo.applicationInfo)) {
                    appMap.put(packageInfo.packageName, packageInfo);
                }
            }
            return appMap;
        } else if (flag == APP_SYSTEM) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (isSystemApp(packageInfo.applicationInfo)) {
                    appMap.put(packageInfo.packageName, packageInfo);
                }
            }
            return appMap;
        } else {
            for (PackageInfo packageInfo : packages) {
                appMap.put(packageInfo.packageName, packageInfo);
            }
            return appMap;
        }
    }

    /**
     * 同时获取用户应用和系统应用
     */
    public static void getPackages(PackageManager pm,
            List<PackageInfo> userAppList, List<PackageInfo> sysAppList) {
        List<PackageInfo> packages = getInstalledPackages(pm);

        if (null != userAppList) {
            userAppList.clear();
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (THIRD_PARTY_FILTER.filterApp(packageInfo.applicationInfo)) {
                    userAppList.add(packageInfo);
                }
            }
        }

        if (null != sysAppList) {
            sysAppList.clear();
            // APP_SYSTEM
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (isSystemApp(packageInfo.applicationInfo)) {
                    sysAppList.add(packageInfo);
                }
            }
        }
    }

    /**
     * 获取未安装的APK信息
     *
     * @param archiveFilePath APK文件的路径。如：/sdcard /download/XX.apk
     */
    public static PackageInfo getArchiveInfo(PackageManager pm,
            String archiveFilePath) {
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_ACTIVITIES);
        return info;
    }

    /**
     * 获取拥有应用入口的应用列表
     */
    // 获得所有启动Activity的信息，类似于Launch界面
    public static List<ResolveInfo> getLunchAppInfo(PackageManager pm) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, 0);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,
                new ResolveInfo.DisplayNameComparator(pm));
        return resolveInfos;

        // if (mlistAppInfo != null) {
        // mlistAppInfo.clear();
        // for (ResolveInfo reInfo : resolveInfos) {
        // String activityName = reInfo.activityInfo.name; //
        // // 获得该应用程序的启动Activity的name
        // String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
        // String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
        // Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
        // // 为应用程序的启动Activity 准备Intent
        // Intent launchIntent = new Intent();
        // launchIntent.setComponent(new ComponentName(pkgName,
        // activityName));
        // // 创建一个AppInfo对象，并赋值
        // AppInfo appInfo = new AppInfo();
        // appInfo.setAppLabel(appLabel);
        // appInfo.setPkgName(pkgName);
        // appInfo.setAppIcon(icon);
        // appInfo.setIntent(launchIntent);
        // mlistAppInfo.add(appInfo); // 添加至列表中
        // }
        // }
    }

    /**
     * 获取应用PackageInfo信息
     */
    public static PackageInfo getPackageInfo(PackageManager pm, String packageName) {
        PackageInfo packageInfo = getPackageInfo(pm, packageName, PackageManager.GET_ACTIVITIES |
                PackageManager.GET_GIDS |
                PackageManager.GET_CONFIGURATIONS |
                PackageManager.GET_INSTRUMENTATION |
                PackageManager.GET_PERMISSIONS |
                PackageManager.GET_PROVIDERS |
                PackageManager.GET_RECEIVERS |
                PackageManager.GET_SERVICES |
                PackageManager.GET_SIGNATURES |
                PackageManager.GET_UNINSTALLED_PACKAGES |
                PackageManager.GET_URI_PERMISSION_PATTERNS |
                PackageManager.GET_SHARED_LIBRARY_FILES);
        return packageInfo;
    }

    public static PackageInfo getPackageInfo(PackageManager pm, String packageName, int flags) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
            return packageInfo;
        } catch (NameNotFoundException e) {
//            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取应用签名
     */
    public static android.content.pm.Signature[] getSignature(
            PackageManager pm, String packageName) {
        try {
            android.content.pm.Signature[] sigs = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES).signatures;
            return sigs;
        } catch (NameNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static byte[] getPublicKey(Signature signature) {
        if (signature == null) {
            return null;
        }

        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
            byte[] pubKey = cert.getPublicKey().getEncoded();
            return pubKey;
        } catch (CertificateException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取签名hash
     */
    public static int getSignatureHashCode(PackageManager pm, String packageName) {
        int sig = 0;
        Signature[] s = getSignature(pm, packageName);
        if (s != null) {
            sig = s[0].hashCode();
        }
        return sig;
    }

    public static byte[] getFingerprint(Context context, String tag) {
        return getFingerprint(context, tag, context.getPackageName());
    }

    public static byte[] getFingerprint(Context context, String tag, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(packageName);

        android.content.pm.Signature[] signatures = getSignature(context.getPackageManager(),
                packageName);
        if (signatures != null) {
            sb.append(signatures[0].toCharsString());
        }

        byte[] fingerprint = HashUtil.getHmac(tag.getBytes(), sb.toString().getBytes());
        return fingerprint;
    }

    /**
     * 获取未安装应用签名
     */
    public static String showUninstallAPKSignatures(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            // apk包的文件路径
            // 这是一个Package 解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            // PackageParser packageParser = new PackageParser(apkPath);
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            // PackageParser.Package mPkgInfo = packageParser.parsePackage(new
            // File(apkPath), apkPath,
            // metrics, 0);
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                    "parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                    valueArgs);

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;
            Method pkgParser_collectCertificatesMtd = pkgParserCls
                    .getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toCharsString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断应用是否为系统应用
     */
    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return false;
        }

        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
            return true;
        }
        return false;
    }

    public static boolean isSystemUpdateApp(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return false;
        }
        return ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static boolean isSystemApp(PackageManager packageManager, String packageName) {
        if (Utils.isEmpty(packageName)) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return AppUtil.isSystemApp(app);
        } catch (NameNotFoundException e) {
            logger.error("packageName = " + packageName, e);
        }
        return false;
    }

    public static boolean isAppInstalled(PackageManager pm, String packageName) {
        return getPackageInfo(pm, packageName, PackageManager.GET_UNINSTALLED_PACKAGES) != null;
    }

    public static interface AppFilter {

        public void init();

        public boolean filterApp(ApplicationInfo info);
    }

    public static class AppFilterExcludeSelf implements AppFilter {

        private String packageName;

        public AppFilterExcludeSelf(String packageName) {
            this.packageName = packageName;
        }

        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationInfo info) {
            if (info.packageName.equals(packageName)) {
                return false;
            }

            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return true;
            } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return true;
            }
            return false;
        }
    }
}
