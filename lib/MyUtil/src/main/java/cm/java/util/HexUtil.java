package cm.java.util;

public class HexUtil {

    public static byte[] decode(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    public static String encode(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    //    private final static String HEX = "0123456789ABCDEF";
    private final static String HEX = "0123456789abcdef";

    private static void appendHex(StringBuilder sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
