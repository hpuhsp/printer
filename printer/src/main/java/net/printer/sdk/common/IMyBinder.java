package net.printer.sdk.common;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:27
 * @UpdateRemark: 更新说明：
 */

import android.content.Context;

import net.printer.sdk.utils.PosPrinterDev;
import net.printer.sdk.utils.RoundQueue;

import java.util.List;

public interface IMyBinder {
    void ConnectNetPort(String paramString, int paramInt, TaskCallback paramTaskCallback);

    void ConnectBtPort(String paramString, TaskCallback paramTaskCallback);

    void ConnectUsbPort(Context paramContext, String paramString, TaskCallback paramTaskCallback);

    void DisconnectCurrentPort(TaskCallback paramTaskCallback);

    void Acceptdatafromprinter(TaskCallback paramTaskCallback, int paramInt);

    RoundQueue<byte[]> ReadBuffer();

    void ClearBuffer();

    void CheckLinkedState(TaskCallback paramTaskCallback);

    void Write(byte[] paramArrayOfbyte, TaskCallback paramTaskCallback);

    void WriteSendData(TaskCallback paramTaskCallback, ProcessData paramProcessData);

    void writeDataByUSB(TaskCallback paramTaskCallback, ProcessData paramProcessData);

    void DisconnetNetPort(TaskCallback paramTaskCallback);

    List<String> OnDiscovery(PosPrinterDev.PortType paramPortType, Context paramContext);

    List<String> getBtAvailableDevice();
}
