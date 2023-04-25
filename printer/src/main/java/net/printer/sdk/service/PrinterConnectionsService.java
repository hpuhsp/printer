package net.printer.sdk.service;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import net.printer.sdk.common.BackgroundInit;
import net.printer.sdk.common.PrinterBinder;
import net.printer.sdk.common.ProcessData;
import net.printer.sdk.common.TaskCallback;
import net.printer.sdk.task.PosAsynncTask;
import net.printer.sdk.utils.PosPrinterDev;
import net.printer.sdk.utils.RoundQueue;
import net.printer.sdk.utils.PosPrinterDev.ErrorCode;
import net.printer.sdk.utils.PosPrinterDev.PortType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PrinterConnectionsService extends Service {
    private final String TAG = "PCS";
    private HashMap<String, PrinterConnectionsService.Printer> printers = new HashMap();
    private final int PORT = 9100;
    private IBinder myBinder = new PrinterConnectionsService.XPrinterBinder();

    public PrinterConnectionsService() {
    }

    public IBinder onBind(Intent intent) {
        return this.myBinder;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        Iterator var2 = this.printers.values().iterator();

        while (var2.hasNext()) {
            PrinterConnectionsService.Printer printer = (PrinterConnectionsService.Printer) var2.next();
            if (printer.xPrinterDev != null) {
                printer.xPrinterDev.Close();
            }
        }

        this.printers.clear();
    }

    public PrinterConnectionsService.Printer getPrinter(String ip) {
        return (PrinterConnectionsService.Printer) this.printers.get(ip);
    }

    public void removePrinter(String ip) {
        Log.d(TAG, "removePrinter");
        this.printers.remove(ip);
    }

    public class Printer {
        public PosPrinterDev xPrinterDev;
        public PosPrinterDev.ReturnMessage mMsg;
        public boolean isConnected = false;
        public RoundQueue<byte[]> que;
        public String ip;
        public String type;
        public String usbPathName;
        public String BtPathName;

        public Printer() {
        }

        private RoundQueue<byte[]> getinstaceRoundQueue() {
            if (this.que == null) {
                this.que = new RoundQueue(500);
            }

            return this.que;
        }
    }

    public class XPrinterBinder extends Binder implements PrinterBinder {
        public XPrinterBinder() {
        }

        public void connectBtPort(final String bluetoothID, TaskCallback callback) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(bluetoothID);
            if (printer != null) {
                PrinterConnectionsService.this.removePrinter(bluetoothID);
            }

            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.new Printer();
                    printer.que = printer.getinstaceRoundQueue();
                    printer.xPrinterDev = new PosPrinterDev(PosPrinterDev.PortType.Bluetooth, bluetoothID);
                    printer.BtPathName = bluetoothID;
                    printer.mMsg = printer.xPrinterDev.Open();
                    printer.type = PosPrinterDev.PortType.Bluetooth.name();
                    boolean flag;
                    if (printer.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                        printer.isConnected = true;
                        flag = true;
                    } else {
                        flag = false;
                    }

                    if (flag) {
                        PrinterConnectionsService.this.printers.put(bluetoothID, printer);
                    }

                    return flag;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void connectUsbPort(final Context context, final String usbPathName, TaskCallback callback) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(usbPathName);
            if (printer != null) {
                PrinterConnectionsService.this.removePrinter(usbPathName);
            }

            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.new Printer();
                    printer.ip = null;
                    printer.usbPathName = usbPathName;
                    printer.que = printer.getinstaceRoundQueue();
                    printer.xPrinterDev = new PosPrinterDev(PosPrinterDev.PortType.USB, context, usbPathName);
                    printer.mMsg = printer.xPrinterDev.Open();
                    printer.type = PosPrinterDev.PortType.USB.name();
                    boolean flag;
                    if (printer.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                        printer.isConnected = true;
                        flag = true;
                    } else {
                        printer.isConnected = false;
                        flag = false;
                    }

                    Log.d(TAG, "connectUsbPort flag:" + flag);
                    PrinterConnectionsService.this.printers.put(usbPathName, printer);
                    return flag;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void connectNetPort(final String ip, TaskCallback callback) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer != null) {
                PrinterConnectionsService.this.removePrinter(ip);
            }

            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.new Printer();
                    printer.ip = ip;
                    printer.que = printer.getinstaceRoundQueue();
                    printer.xPrinterDev = new PosPrinterDev(PortType.Ethernet, ip, 9100);
                    printer.mMsg = printer.xPrinterDev.Open();
                    printer.type = PortType.Ethernet.name();

                    boolean flag;
                    try {
                        if (printer.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                            printer.isConnected = true;
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } catch (Exception var4) {
                        var4.printStackTrace();
                        flag = false;
                    }

                    if (flag) {
                        PrinterConnectionsService.this.printers.put(ip, printer);
                    }

                    return flag;
                }
            });
            task.execute(new Void[0]);
        }

        public void disconnectCurrentPort(String ip, TaskCallback callback) {
            final PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "disconnectCurrentPort ip: " + ip + ", 打印机未添加");
                callback.OnFailed();
            } else {
                PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                    public boolean doinbackground() {
                        printer.mMsg = printer.xPrinterDev.Close();
                        boolean flag;
                        if (printer.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.ClosePortSuccess)) {
                            printer.isConnected = false;
                            if (printer.que != null) {
                                printer.que.clear();
                            }

                            flag = true;
                        } else {
                            flag = false;
                        }

                        return flag;
                    }
                });
                task.execute(new Void[0]);
            }
        }

        public void disconnectAll(TaskCallback callback) {
            int count = PrinterConnectionsService.this.printers.size();
            if (count == 0) {
                Log.d(TAG, "disconnectAll count: " + count);
                callback.OnSucceed();
            } else {
                PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                    public boolean doinbackground() {
                        Iterator var2 = PrinterConnectionsService.this.printers.values().iterator();

                        while (var2.hasNext()) {
                            PrinterConnectionsService.Printer printer = (PrinterConnectionsService.Printer) var2.next();
                            String ip = printer.ip;
                            PrinterConnectionsService.XPrinterBinder.this.disconnectCurrentPort(ip, new TaskCallback() {
                                public void OnSucceed() {
                                }

                                public void OnFailed() {
                                }
                            });
                        }

                        return true;
                    }
                });
                task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
            }
        }

        public void acceptdatafromprinter(String ip, TaskCallback callback) {
            final PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "acceptdatafromprinter ip: " + ip + ", 打印机未添加");
                callback.OnFailed();
            } else {
                new PosAsynncTask(callback, new BackgroundInit() {
                    public boolean doinbackground() {
                        printer.que = printer.getinstaceRoundQueue();
                        byte[] buffer = new byte[4];
                        printer.que.clear();
                        Log.i("TAG", printer.xPrinterDev.Read(buffer).GetErrorCode().toString());
                        printer.que.addLast(buffer);
                        Log.i("TAG", "开始读取" + Arrays.toString((byte[]) printer.que.getLast()));

                        while (printer.xPrinterDev.Read(buffer).GetErrorCode().equals(ErrorCode.ReadDataSuccess)) {
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException var3) {
                                var3.printStackTrace();
                                return false;
                            }
                        }

                        printer.isConnected = false;
                        return false;
                    }
                });
            }
        }

        public RoundQueue<byte[]> readBuffer(String ip) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "readBuffer ip: " + ip + ", 打印机未添加");
                return null;
            } else {
                new RoundQueue(500);
                RoundQueue<byte[]> queue = printer.que;
                return queue;
            }
        }

        public void clearBuffer(String ip) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "clearBuffer ip: " + ip + ", 打印机未添加");
            } else {
                printer.que.clear();
            }
        }

        public void checkLinkedState(String ip, TaskCallback execute) {
            final PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "checkLinkedState ip: " + ip + ", 打印机未添加");
                execute.OnFailed();
            } else {
                PosAsynncTask task = new PosAsynncTask(execute, new BackgroundInit() {
                    public boolean doinbackground() {
                        if (printer.isConnected) {
                            printer.isConnected = printer.xPrinterDev.GetPortInfo().PortIsOpen();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                task.execute(new Void[0]);
            }
        }

        public void write(String ip, final byte[] data, TaskCallback callback) {
            final PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "write ip: " + ip + ", 打印机未添加");
                callback.OnFailed();
            } else {
                PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                    public boolean doinbackground() {
                        if (data != null) {
                            try {
                                printer.mMsg = printer.xPrinterDev.Write(data);
                                if (printer.mMsg.GetErrorCode().equals(ErrorCode.WriteDataSuccess)) {
                                    printer.isConnected = true;
                                    return true;
                                }
                            } catch (Exception var2) {
                                if (var2 != null) {
                                    var2.printStackTrace();
                                }
                            }

                            printer.isConnected = false;
                        }

                        return false;
                    }
                });
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }

        public void writeDataByYouself(String ip, TaskCallback callback, final ProcessData processData) {
            final PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "writeDataByYouself ip: " + ip + ", 打印机未添加");
                callback.OnFailed();
            } else {
                PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                    public boolean doinbackground() {
                        List<byte[]> list = processData.processDataBeforeSend();
                        if (list == null) {
                            return false;
                        } else {
                            for (int i = 0; i < list.size(); ++i) {
                                printer.mMsg = printer.xPrinterDev.Write((byte[]) list.get(i));
                            }

                            if (printer.mMsg.GetErrorCode().equals(ErrorCode.WriteDataSuccess)) {
                                printer.isConnected = true;
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                });
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }

        public boolean isConnect(String ip) {
            PrinterConnectionsService.Printer printer = PrinterConnectionsService.this.getPrinter(ip);
            if (printer == null) {
                Log.d(TAG, "isConnect ip: " + ip + ", 打印机未添加");
                return false;
            } else {
                try {
                    printer.isConnected = printer.xPrinterDev.GetPortInfo().PortIsOpen();
                } catch (Throwable var4) {
                    var4.printStackTrace();
                }

                return printer.isConnected;
            }
        }
    }
}
