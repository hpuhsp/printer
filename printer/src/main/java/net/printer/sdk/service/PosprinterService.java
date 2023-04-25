package net.printer.sdk.service;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:40
 * @UpdateRemark: 更新说明：
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import net.printer.sdk.common.ProcessData;
import net.printer.sdk.task.PosAsynncTask;
import net.printer.sdk.common.BackgroundInit;
import net.printer.sdk.common.IMyBinder;
import net.printer.sdk.common.TaskCallback;
import net.printer.sdk.utils.PosPrinterDev;
import net.printer.sdk.utils.RoundQueue;
import net.printer.sdk.utils.PosPrinterDev.ErrorCode;
import net.printer.sdk.utils.PosPrinterDev.PortType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PosprinterService extends Service {
    private PosPrinterDev xPrinterDev;
    private PosPrinterDev.ReturnMessage mMsg;
    private boolean isConnected = false;
    private RoundQueue<byte[]> que;
    private IBinder myBinder = new PosprinterService.MyBinder();

    public PosprinterService() {
    }

    private RoundQueue<byte[]> getinstaceRoundQueue() {
        if (this.que == null) {
            this.que = new RoundQueue(500);
        }

        return this.que;
    }

    public void onCreate() {
        super.onCreate();
        this.que = this.getinstaceRoundQueue();
        Log.i("TAG", "onCreate");
    }

    public IBinder onBind(Intent intent) {
        Log.i("TAG", "onBind");
        return this.myBinder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.xPrinterDev != null) {
            this.xPrinterDev.Close();
        }

    }

    public class MyBinder extends Binder implements IMyBinder {
        private String USBName;
        private Context Context;
        private PosPrinterDev dev;
        private PosPrinterDev.PortType portTp;
        private List<String> mFound;
        private List<String> mBound;
        private BluetoothAdapter mbBluetoothAdapter;
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.bluetooth.device.action.FOUND")) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    PosprinterService.MyBinder.this.mFound.add(device.getName() + '\n' + device.getAddress());
                }

            }
        };

        public MyBinder() {
        }

        public void ConnectNetPort(final String ethernetIP, final int ethernetPort, TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.xPrinterDev = new PosPrinterDev(PosPrinterDev.PortType.Ethernet, ethernetIP, ethernetPort);
                    PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Open();
                    PosprinterService.MyBinder.this.portTp = PosPrinterDev.PortType.Ethernet;

                    try {
                        if (PosprinterService.this.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                            PosprinterService.this.isConnected = true;
                            Log.e("connectNetPort", "connect ok");
                            return true;
                        } else {
                            Log.e("connectNetPort", "conect fail");
                            PosprinterService.this.isConnected = false;
                            return false;
                        }
                    } catch (Exception var2) {
                        var2.printStackTrace();
                        Log.e("connectNetPort", "connect fail");
                        return false;
                    }
                }
            });
            Log.e("connectNetPort", "connect ok");
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void ConnectBtPort(final String bluetoothID, TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.xPrinterDev = new PosPrinterDev(PosPrinterDev.PortType.Bluetooth, bluetoothID);
                    PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Open();
                    PosprinterService.MyBinder.this.portTp = PosPrinterDev.PortType.Bluetooth;
                    if (PosprinterService.this.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                        PosprinterService.this.isConnected = true;
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void ConnectUsbPort(final Context context, final String usbPathName, TaskCallback callback) {
            this.USBName = usbPathName;
            this.Context = context;
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.xPrinterDev = new PosPrinterDev(PosPrinterDev.PortType.USB, context, usbPathName);
                    PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Open();
                    PosprinterService.MyBinder.this.portTp = PosPrinterDev.PortType.USB;
                    if (PosprinterService.this.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.OpenPortSuccess)) {
                        PosprinterService.this.isConnected = true;
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void DisconnectCurrentPort(TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Close();
                    if (PosprinterService.this.mMsg.GetErrorCode().equals(PosPrinterDev.ErrorCode.ClosePortSuccess)) {
                        PosprinterService.this.isConnected = false;
                        if (PosprinterService.this.que != null) {
                            PosprinterService.this.que.clear();
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void Write(final byte[] data, TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    if (data != null) {
                        PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Write(data);
                        if (PosprinterService.this.mMsg.GetErrorCode().equals(ErrorCode.WriteDataSuccess)) {
                            PosprinterService.this.isConnected = true;
                            return true;
                        }

                        PosprinterService.this.isConnected = false;
                    }

                    return false;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void WriteSendData(TaskCallback callback, final ProcessData processData) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    List<byte[]> list = processData.processDataBeforeSend();
                    if (list == null) {
                        return false;
                    } else {
                        for (int i = 0; i < list.size(); ++i) {
                            PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Write((byte[]) list.get(i));
                        }

                        if (PosprinterService.this.mMsg.GetErrorCode().equals(ErrorCode.WriteDataSuccess)) {
                            PosprinterService.this.isConnected = true;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void writeDataByUSB(TaskCallback callback, final ProcessData processData) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    List<byte[]> list = processData.processDataBeforeSend();
                    if (list != null) {
                        try {
                            for (int i = 0; i < list.size(); ++i) {
                                PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Write((byte[]) list.get(i));
                            }

                            if (PosprinterService.this.mMsg.GetErrorCode().equals(ErrorCode.WriteDataSuccess)) {
                                PosprinterService.this.isConnected = true;
                                return true;
                            }

                            PosprinterService.this.isConnected = false;
                        } catch (NullPointerException var3) {
                            var3.printStackTrace();
                            PosprinterService.this.isConnected = false;
                            return false;
                        }
                    }

                    return false;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void Acceptdatafromprinter(TaskCallback callback, int cout) {
            final byte[] buffer = new byte[cout];
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.que = PosprinterService.this.getinstaceRoundQueue();
                    PosprinterService.this.que.clear();
                    Log.i("TAG", PosprinterService.this.xPrinterDev.Read(buffer).GetErrorCode().toString());
                    PosprinterService.this.que.addLast(buffer);
                    Log.i("TAG", "开始读取" + Arrays.toString((byte[]) PosprinterService.this.que.getLast()));
                    return true;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public RoundQueue<byte[]> ReadBuffer() {
            new RoundQueue(500);
            RoundQueue<byte[]> queue = PosprinterService.this.que;
            if (this.portTp == PortType.USB) {
                queue = PosprinterService.this.xPrinterDev.getUsbRcData();
            }

            return queue;
        }

        public void ClearBuffer() {
            PosprinterService.this.que.clear();
        }

        public void CheckLinkedState(TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    return PosprinterService.this.xPrinterDev != null ? PosprinterService.this.xPrinterDev.GetPortInfo().PortIsOpen() : false;
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public void DisconnetNetPort(TaskCallback callback) {
            PosAsynncTask task = new PosAsynncTask(callback, new BackgroundInit() {
                public boolean doinbackground() {
                    PosprinterService.this.mMsg = PosprinterService.this.xPrinterDev.Close();
                    if (PosprinterService.this.mMsg.GetErrorCode().equals(ErrorCode.ClosePortSuccess)) {
                        PosprinterService.this.isConnected = false;
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
        }

        public List<String> OnDiscovery(PosPrinterDev.PortType portType, Context context) {
            this.mBound = new ArrayList();
            this.mFound = new ArrayList();
            if (portType == PortType.Bluetooth) {
                this.mbBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (this.mbBluetoothAdapter == null) {
                    Toast.makeText(context, "device didn't suport bluetooth!", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if (this.mbBluetoothAdapter.isEnabled()) {
                    if (this.mbBluetoothAdapter.enable()) {
                        if (!this.mbBluetoothAdapter.isDiscovering()) {
                            this.mbBluetoothAdapter.startDiscovery();
                        }

                        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
                        PosprinterService.this.registerReceiver(this.mReceiver, filter);
                        Set<BluetoothDevice> pairedDevice = this.mbBluetoothAdapter.getBondedDevices();
                        if (pairedDevice.size() > 0) {
                            Iterator var6 = pairedDevice.iterator();

                            while (var6.hasNext()) {
                                BluetoothDevice device = (BluetoothDevice) var6.next();
                                this.mBound.add(device.getName() + "\n" + device.getAddress());
                            }
                        } else {
                            Toast.makeText(context, "no paired device !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "enabel bluetooth fail1,", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "enabel bluetooth fail2,", Toast.LENGTH_SHORT).show();
                }

                if (this.mBound != null && this.mFound != null) {
                    this.mBound.addAll(this.mFound);
                }
            } else if (portType == PortType.USB) {
                this.mBound = PosPrinterDev.GetUsbPathNames(context);
            }

            return this.mBound;
        }

        public List<String> getBtAvailableDevice() {
            this.mbBluetoothAdapter.cancelDiscovery();
            return this.mFound;
        }
    }
}
