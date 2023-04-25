package net.printer.sdk.utils;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 15:17
 * @UpdateRemark: 更新说明：
 */

import java.io.UnsupportedEncodingException;

public class PosSetting {
    public PosSetting() {
    }

    public static byte[] sendHex(String text) {
        int l = text.length() / 2;
        int a = 0;
        byte[] data = new byte[l];
        String[] temps = new String[l];

        for (int i = 0; i < l; ++i) {
            String s = text.substring(2 * i, 2 * i + 2);
            temps[i] = "0x" + s;

            try {
                a = Integer.decode(temps[i]);
                data[i] = (byte) a;
            } catch (NumberFormatException var8) {
                var8.printStackTrace();
                data = null;
            }
        }

        return data;
    }

    public static byte[] sendSetip(String text) {
        byte[] data = new byte[]{31, 27, 31, 34};
        String[] temps = text.split("\\.");
        byte[] bs = new byte[temps.length];

        for (int i = 0; i < temps.length; ++i) {
            bs[i] = (byte) Integer.parseInt(temps[i]);
        }

        data = byteMerger(data, bs);
        return data;
    }

    public static byte[] sendSetym(String text) {
        byte[] data = new byte[]{31, 27, 31, -80};
        String[] temps = text.split("\\.");
        byte[] bs = new byte[temps.length];

        for (int i = 0; i < temps.length; ++i) {
            bs[i] = (byte) Integer.parseInt(temps[i]);
        }

        data = byteMerger(data, bs);
        return data;
    }

    public static byte[] sendSetwg(String text) {
        byte[] data = new byte[]{31, 27, 31, -79};
        String[] temps = text.split("\\.");
        byte[] bs = new byte[temps.length];

        for (int i = 0; i < temps.length; ++i) {
            bs[i] = (byte) Integer.parseInt(temps[i]);
        }

        data = byteMerger(data, bs);
        return data;
    }

    public static byte[] sendSetNet(String text1, String text2, String text3) {
        byte[] data = new byte[]{31, 27, 31, -78};
        String[] temps1 = text1.split("\\.");
        String[] temps2 = text2.split("\\.");
        String[] temps3 = text3.split("\\.");
        byte[] bs1 = new byte[temps1.length];
        byte[] bs2 = new byte[temps2.length];
        byte[] bs3 = new byte[temps3.length];

        int i;
        for (i = 0; i < temps1.length; ++i) {
            bs1[i] = (byte) Integer.parseInt(temps1[i]);
        }

        for (i = 0; i < temps2.length; ++i) {
            bs2[i] = (byte) Integer.parseInt(temps2[i]);
        }

        for (i = 0; i < temps3.length; ++i) {
            bs3[i] = (byte) Integer.parseInt(temps3[i]);
        }

        data = byteMerger(byteMerger(byteMerger(data, bs1), bs2), bs3);
        return data;
    }

    public static byte[] sendSetNetall(int pos, String text1, String text2, String text3, String text4, String text5) {
        byte[] data = new byte[]{31, 27, 31, -76};
        byte[] data2 = new byte[1];
        byte[] data3 = new byte[]{(byte) pos};
        String[] temps1 = text1.split("\\.");
        String[] temps2 = text2.split("\\.");
        String[] temps3 = text3.split("\\.");
        byte[] bs1 = new byte[temps1.length];
        byte[] bs2 = new byte[temps2.length];
        byte[] bs3 = new byte[temps3.length];

        int i;
        for (i = 0; i < temps1.length; ++i) {
            bs1[i] = (byte) Integer.parseInt(temps1[i]);
        }

        for (i = 0; i < temps2.length; ++i) {
            bs2[i] = (byte) Integer.parseInt(temps2[i]);
        }

        for (i = 0; i < temps3.length; ++i) {
            bs3[i] = (byte) Integer.parseInt(temps3[i]);
        }

        data = byteMerger(byteMerger(byteMerger(data, bs1), bs2), bs3);
        byte[] wifes1 = strTobytes(text4);
        byte[] wifes2 = strTobytes(text5);
        data = byteMerger(byteMerger(byteMerger(byteMerger(byteMerger(data, data3), wifes1), data2), wifes2), data2);
        return data;
    }

    public static byte[] sendSetWifi(int pos, String text1, String text2) {
        byte[] bs1 = new byte[]{31, 27, 31, -77, (byte) pos};
        byte[] bs2 = new byte[1];
        byte[] bs3 = new byte[1];
        byte[] data1 = strTobytes(text1);
        byte[] data2 = strTobytes(text2);
        byte[] data = byteMerger(byteMerger(byteMerger(byteMerger(bs1, data1), bs2), data2), bs3);
        return data;
    }

    public static byte[] sendUSBset(int pos) {
        byte[] data = null;
        byte[] bs1 = new byte[]{31, 27, 31, -72, 19, 20, 0, 0};
        byte[] bs2 = new byte[]{31, 27, 31, -72, 19, 20, 1, 0};
        switch (pos) {
            case 0:
                data = bs1;
                break;
            case 1:
                data = bs2;
        }

        return data;
    }

    public static byte[] sendSetBt(String BtName, String Btpsd) {
        byte[] bs1 = new byte[]{31, 27, 31, 98, 116, 115, 13, 10, 65, 84, 43, 78, 65, 77, 69};
        byte[] bs2 = new byte[]{13, 10, 65, 84, 43, 80, 73, 78};
        byte[] bs3 = new byte[]{13, 10, 13, 10, 0, 0};
        byte[] data1 = strTobytesUTF(BtName);
        byte[] data2 = strTobytesUTF(Btpsd);
        byte[] data = byteMerger(byteMerger(byteMerger(byteMerger(bs1, data1), bs2), data2), bs3);
        return data;
    }

    public static byte[] printDensity(int n) {
        byte[] data1 = new byte[]{31, 27, 31, 19, 20, (byte) n};
        return data1;
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

    public static byte[] strTobytesUTF(String str) {
        byte[] b = null;
        byte[] data = null;

        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return b;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
