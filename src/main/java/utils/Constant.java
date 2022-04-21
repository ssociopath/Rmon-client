package utils;

/**
 * @author bobo
 * @date 2021/6/25
 */

public class Constant {
    public final static String WINDOWS = "Windows";
    public final static String LINUX = "Linux";

    /**
     * 消息类型，即请求内容类型
     */
    public static final byte HEART = 1;
    public static final byte LOGIN = 2;
    public static final byte LOGOUT = 3;
    public static final byte IMAGE = 4;
    public static final byte TASK = 5;
    public static final byte DATA_UPDATE = 6;
    public static final byte DATA_DELETE = 7;
    public static final byte RES_UPDATE = 8;
    public static final byte CMD = 9;

    public static final byte DF = -1;
    public static final byte MF = -2;

    public static final byte RESPONSE_SUCCEED = 20;
    public static final byte RESPONSE_FAIL = 21;

    public static final int MAX_PKG_BYTE_LENGTH = 8*1024;

    public static final int DEFAULT_SIZE = 1080;
}
