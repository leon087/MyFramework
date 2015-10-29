package cm.java.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类
 */
public final class Utils {

    private static final Logger logger = LoggerFactory.getLogger("util");

    private Utils() {
    }

    /**
     * @return 随机数
     */
    public static int getRandom(int randomint) {
        Random random = new Random();
        return random.nextInt(randomint);
    }

    /**
     * 检查字符串是否为null或空字符串
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        return false;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <T> boolean isEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * 判断文件MimeType的方法
     *
     * @param isOpen 目的打开方式为true
     */
    public static String getMIMEType(File f, boolean isOpen) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        if (isOpen) {
            /* 依附档名的类型决定MimeType */
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                    || end.equals("xmf") || end.equals("ogg")
                    || end.equals("wav")) {
                type = "audio";
            } else if (end.equals("3gp") || end.equals("mp4")) {
                type = "video";
            } else if (end.equals("jpg") || end.equals("gif")
                    || end.equals("png") || end.equals("jpeg")
                    || end.equals("bmp")) {
                type = "image";
            } else {
                /* 如果无法直接打开，就跳出软件列表给用户选择 */
                type = "*";
            }
            type += "/*";
        } else {
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                    || end.equals("xmf") || end.equals("ogg")
                    || end.equals("wav")) {
                type = "audio";
            } else if (end.equals("3gp") || end.equals("mp4")) {
                type = "video";
            } else if (end.equals("jpg") || end.equals("gif")
                    || end.equals("png") || end.equals("jpeg")
                    || end.equals("bmp")) {
                type = "image";
            } else if (end.equals("apk")) {
                type = "apk";
            }
        }
        return type;
    }

    /**
     * 网络序 整形值转换，比如：10.0.0.172 转换为：0xAC00000A
     *
     * @param ipAddress 网络地址
     * @return 转换后的整形值
     */
    public static int lookupHost(String ipAddress) {
        if (ipAddress == null) {
            return -1;
        }

        String[] addrArray = ipAddress.split("\\.");
        int size = addrArray.length;
        if (size != 4) {
            return -1;
        }

        int[] addrBytes = new int[size];
        try {
            for (int i = 0; i < size; i++) {
                addrBytes[i] = Integer.parseInt(addrArray[i]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }

        return ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8) | (addrBytes[0] & 0xff);
    }

    /**
     * check the http response data
     */
    public static boolean checkResponseEntity(HttpResponse response) {
        if (response == null) {
            return false;
        }
        if (response.getEntity().getContentLength() == 0
                || (response.getEntity().getContentType() != null && response
                .getEntity().getContentType().getValue()
                .contains("wml"))) {
            return false;
        }
        return true;
    }

    public static String getHeader(Header[] headers, String headerName) {
        if (headers == null) {
            return "";
        }

        for (Header header : headers) {
            if (headerName.equals(header.getName())) {
                return header.getValue();
            }
        }

        return "";
    }

    /**
     * 从登陆的HttpResponse结果中获取JSESSIONID
     */
    public static String getSessionID(HttpResponse response) {
        String sessionID = "";
        if (response == null) {
            return sessionID;
        }

        Header[] headers = response.getHeaders("Set-Cookie");
        if (headers == null) {
            return sessionID;
        }
        for (Header header : headers) {
            if (header.getName() != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(header.getName() + " = " + header.getValue());
                }
                // Set-Cookie = JSESSIONID=5C50DF19F1DE7BA88B6A30CACEA3A2B6;
                // Path=/market
                sessionID = header.getValue().split(";")[0];
                break;
            }
        }
        return sessionID;
    }

    /**
     * @param text
     * @return
     */
    public static int getLineCount(String text) {
        if (text == null) {
            return 0;
        }
        int lineCount = 0;
        String[] br = text.split("<br>");
        System.out.println("br.length = " + br.length);
        for (int i = 0; i < br.length; i++) {
            System.out.println(i + "br[i].getBytes().length = "
                    + br[i].getBytes().length);

            int line = (int) Math.ceil(((double) br[i].getBytes().length) / 46);
            if (line == 0) {
                line = 1;
            }
            lineCount += line;
            System.out.println("lineCount = " + lineCount);
        }

        return lineCount;
    }

    public static String encodeURL(String str, String defaultValue) {
        try {
            return URLEncoder.encode(str, HTTP.UTF_8);
        } catch (Exception e) {
            logger.error("str = " + str, e);
            return defaultValue;
        }
    }

    public static String decodeURL(String str, String defaultValue) {
        try {
            return URLDecoder.decode(str, HTTP.UTF_8);
        } catch (Exception e) {
            logger.error("str = " + str, e);
            return defaultValue;
        }
    }

    public static String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

    public static String buildShortClassTag(Object cls) {
        if (cls == null) {
            return "null";
        }

        Class<?> clazz = null;
        if (cls instanceof Class<?>) {
            clazz = (Class<?>) cls;
        } else {
            clazz = cls.getClass();
        }
        String simpleName = clazz.getSimpleName();
        if (simpleName == null || simpleName.length() <= 0) {
            simpleName = clazz.getName();
            int end = simpleName.lastIndexOf('.');
            if (end > 0) {
                simpleName = simpleName.substring(end + 1);
            }
        }
        return simpleName;
    }

    public static Properties loadProperties(File file) {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            properties.load(is);
            return properties;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return properties;
        } finally {
            IoUtil.closeQuietly(is);
        }
    }

    public static int convertToInt(String str) throws NumberFormatException {
        int s, e;
        for (s = 0; s < str.length(); s++) {
            if (Character.isDigit(str.charAt(s))) {
                break;
            }
        }
        for (e = str.length(); e > 0; e--) {
            if (Character.isDigit(str.charAt(e - 1))) {
                break;
            }
        }
        if (e > s) {
            try {
                return Integer.parseInt(str.substring(s, e));
            } catch (NumberFormatException ex) {
                logger.error("convertToInt", ex);
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public static <T> T[] concat(T[] A, T[] B) {
        final Class<?> typeofA = A.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        T[] C = (T[]) Array.newInstance(typeofA, A.length + B.length);
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);

        return C;
    }

    public static String getString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? "" : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding response into string failed", e);
            return "";
        }
    }

    public static org.apache.http.Header[] genHeader(Map<String, String> map) {
        Set<Entry<String, String>> set = map.entrySet();
        List<Header> headerList = ObjectUtil.newArrayList();
        for (Entry<String, String> entry : set) {
            Header header = new BasicHeader(entry.getKey(), entry.getValue());
            headerList.add(header);
        }
        return headerList.toArray(new Header[headerList.size()]);
    }

    public static Map<String, String> genHeaderMap(org.apache.http.Header[] headers) {
        Map<String, String> headMap = ObjectUtil.newHashMap();
        for (Header header : headers) {
            headMap.put(header.getName(), header.getValue());
        }
        return headMap;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (!Utils.isEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String getString(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str;
    }

    public static Object cloneObject(Object obj) {
        if (obj == null) {
            return null;
        }

        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            Object o = in.readObject();
            return o;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            IoUtil.closeQuietly(out);
            IoUtil.closeQuietly(in);
        }
    }

    public static String encodeBase64(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static String trimString(String str) {
        if (isEmpty(str)) {
            return "";
        }

        return str.trim();
    }
}
