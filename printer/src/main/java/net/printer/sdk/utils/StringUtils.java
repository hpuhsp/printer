package net.printer.sdk.utils;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 15:18
 * @UpdateRemark: 更新说明：
 */

import java.io.UnsupportedEncodingException;

public class StringUtils {
    public StringUtils() {
    }

    public static byte[] strTobytes(String str) {
        byte[] b = null;
        byte[] data = null;

        try {
            b = str.getBytes("utf-8");
            data = (new String(b, "utf-8")).getBytes("gbk");
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return data;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] strTobytes(String str, String charset) {
        byte[] b = null;
        byte[] data = null;

        try {
            b = str.getBytes("utf-8");
            data = (new String(b, "utf-8")).getBytes(charset);
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        return data;
    }
}
