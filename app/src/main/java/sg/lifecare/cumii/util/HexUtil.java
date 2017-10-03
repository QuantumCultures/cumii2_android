package sg.lifecare.cumii.util;

/**
 * Created by sweelai on 29/11/16.
 */

public class HexUtil {

    public static String getShortHexStringFromByte(int b) {
        StringBuilder builder = new StringBuilder();
        appendShortHexStringFromByte(b, builder);
        return builder.toString();
    }

    public static void appendShortHexStringFromByte(int b, StringBuilder builder) {
        String hexString = Integer.toHexString(b & 0xff);
        if (hexString.length() == 1) {
            builder.append("0");
        }
        builder.append(hexString);
    }

    public static String getHexStringFromByte(int b) {
        StringBuilder builder = new StringBuilder();
        appendHexStringFromByte(b, builder);
        return builder.toString();
    }

    public static void appendHexStringFromByte(int b, StringBuilder builder) {
        builder.append("0x");
        appendShortHexStringFromByte(b, builder);
    }

    public static String getHexStringFromByteArray(byte[] byteArray) {
        return getHexStringFromByteArray(byteArray, 0, byteArray.length);
    }

    public static String getHexStringFromByteArray(byte[] byteArray, int offset, int length) {
        StringBuilder builder = new StringBuilder();
        appendHexStringFromByteArray(builder, byteArray, offset, length);
        return builder.toString();
    }

    public static void appendHexStringFromByteArray(StringBuilder builder, byte[] byteArray, int offset, int length) {
        int l = 1;
        for (int i = offset; i < (offset + length); i++) {
            if ((l != 1) && ((l - 1) % 8 == 0)) {
                builder.append(' ');
            }
            if ((l != 1) && ((l - 1) % 16 == 0)) {
                builder.append('\n');
            }
            l++;
            appendHexStringFromByte(byteArray[i], builder);
            if (i != offset + length - 1) {
                builder.append(" ");
            }
        }
    }

    public static String getShortHexStringFromByteArray(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        appendShortHexStringFromByteArray(builder, byteArray, 0, byteArray.length);
        return builder.toString();
    }

    public static String getShortHexStringFromByteArray(byte[] byteArray, int offset, int length) {
        StringBuilder builder = new StringBuilder();
        appendShortHexStringFromByteArray(builder, byteArray, offset, length);
        return builder.toString();
    }

    public static void appendShortHexStringFromByteArray(StringBuilder builder, byte[] byteArray, int offset, int length) {
        for (int i = offset; i < (offset + length); i++) {
            appendShortHexStringFromByte(byteArray[i], builder);
        }
    }

    public static byte[] getByteArrayFromShortHexString(String s) throws NumberFormatException {

        if (s == null) {
            throw new IllegalArgumentException("string s may not be null");
        }

        int length = s.length();

        if ((length == 0) || ((length % 2) != 0)) {
            throw new NumberFormatException("string is not a legal hex string.");
        }

        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            int firstCharacter = Character.digit(s.charAt(i), 16);
            int secondCharacter = Character.digit(s.charAt(i + 1), 16);

            if (firstCharacter == -1 || secondCharacter == -1) {
                throw new NumberFormatException("string is not a legal hex string.");
            }

            data[i / 2] = (byte) ((firstCharacter << 4) + secondCharacter);
        }
        return data;
    }
}
