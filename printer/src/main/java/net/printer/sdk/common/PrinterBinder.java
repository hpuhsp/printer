package net.printer.sdk.common;

import android.content.Context;

import net.printer.sdk.utils.RoundQueue;


/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:38
 * @UpdateRemark: 更新说明：
 */

public interface PrinterBinder {
    void connectBtPort(String paramString, TaskCallback paramTaskCallback);

    void connectUsbPort(Context paramContext, String paramString, TaskCallback paramTaskCallback);

    void connectNetPort(String paramString, TaskCallback paramTaskCallback);

    void disconnectCurrentPort(String paramString, TaskCallback paramTaskCallback);

    void disconnectAll(TaskCallback paramTaskCallback);

    void acceptdatafromprinter(String paramString, TaskCallback paramTaskCallback);

    RoundQueue<byte[]> readBuffer(String paramString);

    void clearBuffer(String paramString);

    void checkLinkedState(String paramString, TaskCallback paramTaskCallback);

    void write(String paramString, byte[] paramArrayOfbyte, TaskCallback paramTaskCallback);

    void writeDataByYouself(String paramString, TaskCallback paramTaskCallback, ProcessData paramProcessData);

    boolean isConnect(String paramString);
}
