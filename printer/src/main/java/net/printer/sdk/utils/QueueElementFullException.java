package net.printer.sdk.utils;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 15:18
 * @UpdateRemark: 更新说明：
 */
public class QueueElementFullException extends RuntimeException {
    private static final long serialVersionUID = -1242599979055870217L;

    public QueueElementFullException() {
    }

    public QueueElementFullException(String message) {
        super(message);
    }

    public QueueElementFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueElementFullException(Throwable cause) {
        super(cause);
    }
}
