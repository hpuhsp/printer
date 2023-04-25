package net.printer.sdk.common;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:39
 * @UpdateRemark: 更新说明：
 */

import java.util.List;

public interface ProcessData {
    List<byte[]> processDataBeforeSend();
}
