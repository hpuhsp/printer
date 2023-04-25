package net.printer.sdk.utils;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 15:15
 * @UpdateRemark: 更新说明：
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class PosPrinterDev {
    private PosPrinterDev.PortInfo mPortInfo = new PosPrinterDev.PortInfo();
    private PosPrinterDev.PrinterPort mPort = null;
    private RoundQueue<byte[]> usbData;

    public PosPrinterDev(PosPrinterDev.PortType portType, Context context) {
        this.mPortInfo.mPortType = portType;
        this.mPortInfo.mContext = context;
    }

    public PosPrinterDev(PosPrinterDev.PortType portType, Context context, String usbPathName) {
        this.mPortInfo.mPortType = portType;
        this.mPortInfo.mContext = context;
        if (!usbPathName.equals("") && usbPathName != null) {
            this.mPortInfo.mUsbPathName = usbPathName;
        }
    }

    public PosPrinterDev(PosPrinterDev.PortType portType, String bluetoothID) {
        this.mPortInfo.mPortType = portType;
        this.mPortInfo.mBluetoothID = bluetoothID;
    }

    public PosPrinterDev(PosPrinterDev.PortType portType, String ethernetIP, int ethernetPort) {
        this.mPortInfo.mPortType = portType;
        this.mPortInfo.mEthernetIP = ethernetIP;
        this.mPortInfo.mEthernetPort = ethernetPort;
    }

    private void ResetPar() {
        if (this.mPortInfo != null) {
            this.mPortInfo = null;
        }

        this.mPortInfo = new PosPrinterDev.PortInfo();
        if (this.mPort != null) {
            this.mPort.ClosePort();
            this.mPort = null;
        }

    }

    public PosPrinterDev.ReturnMessage Open() {
        PosPrinterDev.ReturnMessage retVar = null;
        switch (this.mPortInfo.mPortType) {
            case USB:
                if (this.mPortInfo.mUsbPathName.equals("")) {
                    retVar = this.Open(this.mPortInfo.mPortType, this.mPortInfo.mContext);
                } else {
                    retVar = this.Open(this.mPortInfo.mPortType, this.mPortInfo.mContext, this.mPortInfo.mUsbPathName);
                }
                break;
            case Bluetooth:
                retVar = this.Open(this.mPortInfo.mPortType, this.mPortInfo.mBluetoothID);
                break;
            case Ethernet:
                retVar = this.Open(this.mPortInfo.mPortType, this.mPortInfo.mEthernetIP, this.mPortInfo.mEthernetPort);
                break;
            default:
                retVar = new PosPrinterDev.ReturnMessage();
        }

        return retVar;
    }

    private PosPrinterDev.ReturnMessage Open(PosPrinterDev.PortType portType, Context context) {
        this.ResetPar();
        if (portType != PosPrinterDev.PortType.USB) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Port type wrong !\n", 0);
        } else if (context == null) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Context is null !\n", 0);
        } else {
            this.mPortInfo.mContext = context;
            this.mPortInfo.mPortType = PosPrinterDev.PortType.USB;
            this.mPortInfo.mUsbPathName = "";
            this.mPort = new PosPrinterDev.UsbPort(this.mPortInfo);
            return this.mPort.OpenPort();
        }
    }

    private PosPrinterDev.ReturnMessage Open(PosPrinterDev.PortType portType, Context context, String usbPathName) {
        this.ResetPar();
        if (portType != PosPrinterDev.PortType.USB) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Port type wrong !\n", 0);
        } else if (context == null) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Context is null !\n", 0);
        } else if (usbPathName == null) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "usbPathName is null !\n", 0);
        } else {
            this.mPortInfo.mContext = context;
            this.mPortInfo.mPortType = PosPrinterDev.PortType.USB;
            this.mPortInfo.mUsbPathName = usbPathName;
            this.mPort = new PosPrinterDev.UsbPort(this.mPortInfo);
            return this.mPort.OpenPort();
        }
    }

    private PosPrinterDev.ReturnMessage Open(PosPrinterDev.PortType portType, String bluetoothID) {
        this.ResetPar();
        if (portType != PosPrinterDev.PortType.Bluetooth) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Port type wrong !\n", 0);
        } else if (!BluetoothAdapter.checkBluetoothAddress(bluetoothID)) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "BluetoothID wrong !\n", 0);
        } else {
            this.mPortInfo.mBluetoothID = bluetoothID;
            this.mPortInfo.mPortType = PosPrinterDev.PortType.Bluetooth;
            this.mPort = new PosPrinterDev.BluetoothPort(this.mPortInfo);
            return this.mPort.OpenPort();
        }
    }

    private PosPrinterDev.ReturnMessage Open(PosPrinterDev.PortType portType, String ethernetIP, int ethernetPort) {
        this.ResetPar();
        if (portType != PosPrinterDev.PortType.Ethernet) {
            return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Port type wrong !\n", 0);
        } else {
            try {
                Inet4Address.getByName(ethernetIP);
            } catch (Exception var5) {
                return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Ethernet ip wrong !\n", 0);
            }

            if (ethernetPort <= 0) {
                return new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Ethernet port wrong !\n", 0);
            } else {
                this.mPortInfo.mEthernetPort = ethernetPort;
                this.mPortInfo.mEthernetIP = ethernetIP;
                this.mPortInfo.mPortType = PosPrinterDev.PortType.Ethernet;
                this.mPort = new PosPrinterDev.EthernetPort(this.mPortInfo);
                return this.mPort.OpenPort();
            }
        }
    }

    public PosPrinterDev.ReturnMessage Write(int data) {
        data &= 255;
        return this.mPort.Write(data);
    }

    public PosPrinterDev.ReturnMessage Write(byte[] data) {
        return this.mPort.Write(data);
    }

    public PosPrinterDev.ReturnMessage Write(byte[] data, int offset, int count) {
        return this.mPort.Write(data, offset, count);
    }

    public PosPrinterDev.ReturnMessage Read(byte[] buffer) {
        return this.Read(buffer, 0, buffer.length);
    }

    public PosPrinterDev.ReturnMessage Read(byte[] buffer, int offset, int count) {
        PosPrinterDev.ReturnMessage retvar = null;
        retvar = this.mPort.Read(buffer, offset, count);
        return retvar;
    }

    public int Read() {
        byte[] tem = new byte[1];
        return this.Read(tem, 0, 1).mErrorCode == PosPrinterDev.ErrorCode.ReadDataSuccess ? tem[0] : -1;
    }

    public synchronized PosPrinterDev.ReturnMessage Close() {
        return this.mPort == null ? new PosPrinterDev.ReturnMessage(PosPrinterDev.ErrorCode.ClosePortFailed, "Not opened port !", 0) : this.mPort.ClosePort();
    }

    private static boolean CheckUsbVid(UsbDevice usbDevice) {
        int[] xprinterUsbID = new int[]{1659, 1046, 7358, 1155, 8137, 1003, 11575, 1208, 22304, 26728};
        int vid = usbDevice.getVendorId();
        int[] var6 = xprinterUsbID;
        int var5 = xprinterUsbID.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            int id = var6[var4];
            if (vid == id) {
                return true;
            }
        }

        return false;
    }

    public static List<String> GetUsbPathNames(Context context) {
        List<String> usbNames = new ArrayList();
        UsbManager usbManager = (UsbManager) context.getSystemService("usb");
        HashMap<String, UsbDevice> usbList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbList.values().iterator();

        while (true) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = (UsbDevice) deviceIterator.next();

                for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                    if (device.getInterface(iInterface).getInterfaceClass() == 7 && device.getInterface(iInterface).getInterfaceSubclass() == 1 && CheckUsbVid(device)) {
                        usbNames.add(device.getDeviceName());
                        break;
                    }
                }
            }

            if (usbNames.size() == 0) {
                usbNames = null;
            }

            return usbNames;
        }
    }

    public RoundQueue<byte[]> getUsbRcData() {
        return this.usbData;
    }

    public PosPrinterDev.PortInfo GetPortInfo() {
        this.mPortInfo.mIsOpened = this.mPort.PortIsOpen();
        return this.mPortInfo;
    }

    private class BluetoothPort extends PosPrinterDev.PrinterPort {
        private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private BluetoothAdapter mBtAdapter = null;
        private BluetoothDevice mBtDevice = null;
        private BluetoothSocket mBtSocket = null;
        private OutputStream mOutPut = null;
        private InputStream mInPut = null;

        public BluetoothPort(PosPrinterDev.PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType == PosPrinterDev.PortType.Bluetooth && BluetoothAdapter.checkBluetoothAddress(portInfo.mBluetoothID)) {
                this.mPortInfo.mParIsOK = true;
                this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                this.mPortInfo.mParIsOK = false;
            }
        }

        PosPrinterDev.ReturnMessage OpenPort() {
            if (!this.mPortInfo.mParIsOK) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                try {
                    if (this.mBtAdapter == null) {
                        return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Not Bluetooth adapter !\n", 0);
                    }

                    if (!this.mBtAdapter.isEnabled()) {
                        return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Bluetooth adapter was closed !\n", 0);
                    }

                    this.mBtAdapter.cancelDiscovery();
                    this.mBtDevice = this.mBtAdapter.getRemoteDevice(this.mPortInfo.mBluetoothID);
                    this.mBtSocket = this.mBtDevice.createRfcommSocketToServiceRecord(this.SPP_UUID);
                    this.mBtSocket.connect();
                    this.mOutPut = null;
                    this.mOutPut = this.mBtSocket.getOutputStream();
                    this.mInPut = null;
                    this.mInPut = this.mBtSocket.getInputStream();
                    this.mIsOpen = true;
                } catch (Exception var2) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, var2.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortSuccess, "Open bluetooth port success !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage ClosePort() {
            try {
                if (this.mOutPut != null) {
                    this.mOutPut.flush();
                }

                if (this.mBtSocket != null) {
                    this.mBtSocket.close();
                }

                this.mIsOpen = false;
                this.mOutPut = null;
                this.mInPut = null;
            } catch (Exception var2) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ClosePortFailed, var2.toString(), 0);
            }

            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ClosePortSuccess, "Close bluetooth port success !\n", 0);
        }

        PosPrinterDev.ReturnMessage Write(int data) {
            if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
                try {
                    this.mOutPut.write(data);
                } catch (Exception var3) {
                    this.ClosePort();
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var3.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send 1 byte .\n", 1);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Write(byte[] data) {
            if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
                try {
                    this.mOutPut.write(data);
                } catch (Exception var3) {
                    this.ClosePort();
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var3.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Write(byte[] data, int offset, int count) {
            if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
                try {
                    this.mOutPut.write(data, offset, count);
                } catch (Exception var5) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var5.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
            } else {
                this.ClosePort();
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer, int offset, int count) {
            if (this.mIsOpen && this.mBtSocket.isConnected() && this.mInPut != null) {
                int readBytes;
                try {
                    readBytes = this.mInPut.read(buffer, offset, count);
                } catch (Exception var6) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, var6.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes);
            } else {
                this.ClosePort();
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer) {
            return this.Read(buffer, 0, buffer.length);
        }

        int Read() {
            if (this.mIsOpen && this.mBtSocket.isConnected() && this.mInPut != null) {
                try {
                    return this.mInPut.read();
                } catch (Exception var2) {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        boolean PortIsOpen() {
            byte[] b = new byte[4];
            PosPrinterDev.ReturnMessage msg = this.Read(b);
            if (msg.GetReadByteCount() == -1) {
                this.mIsOpen = false;
            } else {
                this.mIsOpen = true;
            }

            return this.mIsOpen;
        }
    }

    public static enum ErrorCode {
        OpenPortFailed,
        OpenPortSuccess,
        ClosePortFailed,
        ClosePortSuccess,
        WriteDataFailed,
        WriteDataSuccess,
        ReadDataSuccess,
        ReadDataFailed,
        UnknownError;

        private ErrorCode() {
        }
    }

    private class EthernetPort extends PosPrinterDev.PrinterPort {
        private InetAddress mInetAddr;
        private SocketAddress mSocketAddr;
        private Socket mNetSocket = new Socket();
        private OutputStream mOutput;
        private InputStream mInput;
        private Thread mthread;
        private PosPrinterDev.ReturnMessage r;
        private Process p;

        public EthernetPort(PosPrinterDev.PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType == PosPrinterDev.PortType.Ethernet && portInfo.mEthernetPort > 0) {
                try {
                    this.mInetAddr = Inet4Address.getByName(portInfo.mEthernetIP);
                    this.mPortInfo.mParIsOK = true;
                } catch (Exception var4) {
                    this.mPortInfo.mParIsOK = false;
                }
            } else {
                this.mPortInfo.mParIsOK = false;
            }

        }

        PosPrinterDev.ReturnMessage OpenPort() {
            if (!this.mPortInfo.mParIsOK) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                try {
                    this.mSocketAddr = new InetSocketAddress(this.mInetAddr, this.mPortInfo.mEthernetPort);
                    this.mNetSocket.connect(this.mSocketAddr, 1000);
                    if (this.mOutput != null) {
                        this.mOutput = null;
                    }

                    this.mOutput = this.mNetSocket.getOutputStream();
                    if (this.mInput != null) {
                        this.mInput = null;
                    }

                    this.mInput = this.mNetSocket.getInputStream();
                    this.mIsOpen = true;
                } catch (NetworkOnMainThreadException var2) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, var2.toString(), 0);
                } catch (UnknownHostException var3) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, var3.toString(), 0);
                } catch (IOException var4) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, var4.toString(), 0);
                } catch (Exception var5) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, var5.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortSuccess, "Open ethernet port success !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage ClosePort() {
            try {
                if (this.mOutput != null) {
                    this.mOutput.flush();
                }

                if (this.mNetSocket != null) {
                    this.mNetSocket.close();
                }

                this.mIsOpen = false;
                this.mOutput = null;
                this.mInput = null;
            } catch (Exception var2) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ClosePortFailed, var2.toString(), 0);
            }

            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ClosePortSuccess, "Close ethernet port success !\n", 0);
        }

        PosPrinterDev.ReturnMessage Write(int data) {
            if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
                try {
                    this.mOutput.write(data);
                } catch (Exception var3) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var3.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send 1 byte .\n", 1);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Write(byte[] data) {
            if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
                try {
                    this.mOutput.write(data);
                } catch (Exception var3) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var3.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Write(byte[] data, int offset, int count) {
            if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
                try {
                    this.mOutput.write(data, offset, count);
                } catch (Exception var5) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, var5.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer, int offset, int count) {
            if (this.mIsOpen && this.mInput != null && this.mNetSocket.isConnected()) {
                int readBytes;
                try {
                    readBytes = this.mInput.read(buffer, offset, count);
                    if (readBytes == -1) {
                        return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, "Ethernet port was close !\n", 0);
                    }
                } catch (Exception var6) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, var6.toString(), 0);
                }

                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes);
            } else {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer) {
            return this.Read(buffer, 0, buffer.length);
        }

        int Read() {
            if (this.mIsOpen && this.mNetSocket.isConnected() && this.mInput != null) {
                try {
                    return this.mInput.read();
                } catch (Exception var2) {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        boolean PortIsOpen() {
            this.mIsOpen = this.pingHost(this.mPortInfo.mEthernetIP);
            if (!this.mIsOpen) {
                this.mIsOpen = this.pingHost(this.mPortInfo.mEthernetIP);
            }

            return this.mIsOpen;
        }

        private boolean pingHost(String str) {
            boolean result = false;
            BufferedReader bufferedReader = null;

            try {
                Thread.sleep(2000L);
                this.p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + str);
                InputStream ins = this.p.getInputStream();
                InputStreamReader reader = new InputStreamReader(ins);
                bufferedReader = new BufferedReader(reader);
                String line = null;

                while (bufferedReader.readLine() != null) {
                }

                int status = this.p.waitFor();
                if (status == 0) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException var18) {
                result = false;
            } catch (InterruptedException var19) {
                result = false;
            } finally {
                if (this.p != null) {
                    this.p.destroy();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException var17) {
                        var17.printStackTrace();
                    }
                }

            }

            return result;
        }
    }

    private class NetConnectThread extends Thread {
        public PosPrinterDev.ReturnMessage returnMessage = null;
        private PosPrinterDev.PrinterPort mTemPort = null;

        public NetConnectThread(PosPrinterDev.PrinterPort port) {
            this.mTemPort = port;
        }

        public void run() {
            this.returnMessage = this.mTemPort.OpenPort();
        }
    }

    private class NetReadThread extends Thread {
        public PosPrinterDev.ReturnMessage returnMessage = null;
        private PosPrinterDev.PrinterPort mTemPort = null;
        public byte[] reBuffer = null;
        public int reOffset = 0;
        public int reCount = 0;

        public NetReadThread(PosPrinterDev.PrinterPort port, byte[] buffer, int offset, int count) {
            this.mTemPort = port;
            this.reBuffer = buffer;
            this.reOffset = offset;
            this.reCount = count;
        }

        public void run() {
            this.returnMessage = this.mTemPort.Read(this.reBuffer, this.reOffset, this.reCount);
        }
    }

    public class PortInfo {
        private PosPrinterDev.PortType mPortType;
        private String mUsbPathName;
        private int mUsbPid;
        private int mUsbVid;
        private int mEthernetPort;
        private String mEthernetIP;
        private String mBluetoothID;
        private boolean mParIsOK;
        private Context mContext;
        private boolean mIsOpened;

        public PortInfo() {
            this.mPortType = PosPrinterDev.PortType.Unknown;
            this.mUsbPathName = "";
            this.mUsbPid = 0;
            this.mUsbVid = 0;
            this.mEthernetPort = 0;
            this.mEthernetIP = "";
            this.mBluetoothID = "";
            this.mParIsOK = false;
            this.mContext = null;
            this.mIsOpened = false;
        }

        public PosPrinterDev.PortType GetPortType() {
            return this.mPortType;
        }

        public String GetPortName() {
            return this.mPortType.name();
        }

        public String GetUsbPathName() {
            return this.mUsbPathName;
        }

        public int GetUsbVid() {
            return this.mUsbVid;
        }

        public int GetUsbPid() {
            return this.mUsbPid;
        }

        public int GetEthernetPort() {
            return this.mEthernetPort;
        }

        public String GetEthernetIP() {
            return this.mEthernetIP;
        }

        public String GetBluetoothID() {
            return this.mBluetoothID;
        }

        public boolean PortIsOpen() {
            return this.mIsOpened;
        }
    }

    public static enum PortType {
        Unknown,
        USB,
        Bluetooth,
        Ethernet;

        private PortType() {
        }
    }

    private abstract class PrinterPort {
        protected PosPrinterDev.PortInfo mPortInfo = null;
        protected Queue<Byte> mRxdQueue = null;
        protected Queue<Byte> mTxdQueue = null;
        protected boolean mIsOpen = false;

        abstract PosPrinterDev.ReturnMessage OpenPort();

        abstract PosPrinterDev.ReturnMessage ClosePort();

        abstract PosPrinterDev.ReturnMessage Write(int var1);

        abstract PosPrinterDev.ReturnMessage Write(byte[] var1);

        abstract PosPrinterDev.ReturnMessage Write(byte[] var1, int var2, int var3);

        abstract PosPrinterDev.ReturnMessage Read(byte[] var1, int var2, int var3);

        abstract PosPrinterDev.ReturnMessage Read(byte[] var1);

        abstract int Read();

        abstract boolean PortIsOpen();

        public PrinterPort(PosPrinterDev.PortInfo portInfo) {
            this.mPortInfo = portInfo;
        }

        public int GetRxdCount() {
            return this.mRxdQueue != null ? this.mRxdQueue.size() : 0;
        }

        public int GetTxdCount() {
            return this.mTxdQueue != null ? this.mTxdQueue.size() : 0;
        }
    }

    public class ReturnMessage {
        private PosPrinterDev.ErrorCode mErrorCode;
        private String mErrorStrings;
        private int mReadBytes;
        private int mWriteBytes;

        public ReturnMessage() {
            this.mReadBytes = -1;
            this.mWriteBytes = -1;
            this.mErrorCode = PosPrinterDev.ErrorCode.UnknownError;
            this.mErrorStrings = "Unknown error\n";
            this.mReadBytes = -1;
            this.mWriteBytes = -1;
        }

        private ReturnMessage(PosPrinterDev.ErrorCode ec, String es) {
            this.mReadBytes = -1;
            this.mWriteBytes = -1;
            this.mErrorCode = ec;
            this.mErrorStrings = es;
        }

        private ReturnMessage(PosPrinterDev.ErrorCode ec, String es, int count) {
            this.mReadBytes = -1;
            this.mWriteBytes = -1;
            this.mErrorCode = ec;
            this.mErrorStrings = es;
            switch (ec) {
                case WriteDataSuccess:
                    this.mWriteBytes = count;
                    break;
                case ReadDataSuccess:
                    this.mReadBytes = count;
            }

        }

        public PosPrinterDev.ErrorCode GetErrorCode() {
            return this.mErrorCode;
        }

        public String GetErrorStrings() {
            return this.mErrorStrings;
        }

        public int GetReadByteCount() {
            return this.mReadBytes;
        }

        public int GetWriteByteCount() {
            return this.mWriteBytes;
        }
    }

    public class UsbPort extends PosPrinterDev.PrinterPort {
        private UsbManager mUsbManager = null;
        private UsbDevice mUsbDevice = null;
        private UsbInterface mUsbInterface = null;
        private UsbDeviceConnection mUsbDeviceConnection = null;
        private UsbEndpoint mUsbInEndpoint = null;
        private UsbEndpoint mUsbOutEndpoint = null;
        private PendingIntent mPermissionIntent = null;
        private String mUserUsbName = null;
        private String ACTION_USB_PERMISSION = "net.xprinter.xprintersdk.USB_PERMISSION";
        private boolean mPermissioning = true;
        private int usbdata;
        public RoundQueue<byte[]> data;
        private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (PosPrinterDev.UsbPort.this.ACTION_USB_PERMISSION.equals(action)) {
                    Log.d("onReceive id is", "" + Thread.currentThread().getId());
                    PosPrinterDev.UsbPort.this.mPermissioning = false;
                    synchronized (this) {
                        UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                        if (intent.getBooleanExtra("permission", false) && device != null) {
                            PosPrinterDev.UsbPort.this.mUsbDevice = device;
                        }
                    }
                }

            }
        };
        Thread usbReadThread = new Thread(new Runnable() {
            public void run() {
                while (PosPrinterDev.UsbPort.this.mIsOpen) {
                    PosPrinterDev.UsbPort.this.Read();
                }

            }
        });

        private int setusbData(int d1) {
            return this.usbdata = d1;
        }

        public int getusbdata() {
            return this.usbdata;
        }

        public UsbPort(PosPrinterDev.PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType != PosPrinterDev.PortType.USB && portInfo.mContext != null && portInfo.mUsbPathName.equals("")) {
                this.mPortInfo.mParIsOK = false;
            } else {
                this.mPortInfo.mParIsOK = true;
                if (this.mPortInfo.mUsbPathName != null && !portInfo.mUsbPathName.equals("")) {
                    this.mUserUsbName = this.mPortInfo.mUsbPathName;
                }
            }

        }

        private List<UsbDevice> GetUsbDeviceList() {
            List<UsbDevice> temList = new ArrayList();
            this.mUsbManager = (UsbManager) this.mPortInfo.mContext.getSystemService("usb");
            HashMap<String, UsbDevice> deviceList = this.mUsbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while (true) {
                while (deviceIterator.hasNext()) {
                    UsbDevice device = (UsbDevice) deviceIterator.next();

                    for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                        if (device.getInterface(iInterface).getInterfaceClass() == 7 && device.getInterface(iInterface).getInterfaceSubclass() == 1 && PosPrinterDev.CheckUsbVid(device)) {
                            temList.add(device);
                            break;
                        }
                    }
                }

                if (temList.size() == 0) {
                    return null;
                }

                return temList;
            }
        }

        PosPrinterDev.ReturnMessage OpenPort() {
            if (!this.mPortInfo.mParIsOK) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                List<UsbDevice> temDevList = this.GetUsbDeviceList();
                if (temDevList == null) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Not find XPrinter's USB printer !\n", 0);
                } else {
                    this.mUsbDevice = null;
                    if (this.mUserUsbName == null) {
                        if (this.mUsbManager.hasPermission((UsbDevice) temDevList.get(0))) {
                            this.mUsbDevice = (UsbDevice) temDevList.get(0);
                        } else {
                            this.mPermissionIntent = PendingIntent.getBroadcast(this.mPortInfo.mContext, 0, new Intent(this.ACTION_USB_PERMISSION), 0);
                            IntentFilter filter = new IntentFilter(this.ACTION_USB_PERMISSION);
                            filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                            filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                            filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                            filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                            this.mPortInfo.mContext.registerReceiver(this.mUsbReceiver, filter);
                            this.mUsbManager.requestPermission((UsbDevice) temDevList.get(0), this.mPermissionIntent);
                        }
                    } else {
                        boolean isEq = false;
                        Iterator var4 = temDevList.iterator();

                        while (var4.hasNext()) {
                            UsbDevice dev = (UsbDevice) var4.next();
                            if (dev.getDeviceName().equals(this.mUserUsbName)) {
                                if (this.mUsbManager.hasPermission(dev)) {
                                    this.mUsbDevice = dev;
                                } else {
                                    this.mPermissionIntent = PendingIntent.getBroadcast(this.mPortInfo.mContext, 0, new Intent(this.ACTION_USB_PERMISSION), 0);
                                    IntentFilter filterx = new IntentFilter(this.ACTION_USB_PERMISSION);
                                    filterx.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                                    filterx.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                                    filterx.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                                    filterx.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                                    this.mPortInfo.mContext.registerReceiver(this.mUsbReceiver, filterx);
                                    this.mUsbManager.requestPermission(dev, this.mPermissionIntent);
                                }

                                isEq = true;
                                break;
                            }
                        }

                        if (!isEq) {
                            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Not find " + this.mUserUsbName + " !\n", 0);
                        }
                    }

                    Log.d("open id is", "" + Thread.currentThread().getId());
                    if (this.mUsbDevice == null) {
                        return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Get USB communication permission failed !\n", 0);
                    } else {
                        for (int iInterface = 0; iInterface < this.mUsbDevice.getInterfaceCount(); ++iInterface) {
                            if (this.mUsbDevice.getInterface(iInterface).getInterfaceClass() == 7) {
                                for (int iEndpoint = 0; iEndpoint < this.mUsbDevice.getInterface(iInterface).getEndpointCount(); ++iEndpoint) {
                                    if (this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint).getType() == 2) {
                                        if (this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint).getDirection() == 128) {
                                            this.mUsbInEndpoint = this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint);
                                        } else {
                                            this.mUsbOutEndpoint = this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint);
                                        }
                                    }

                                    if (this.mUsbInEndpoint != null && this.mUsbOutEndpoint != null) {
                                        break;
                                    }
                                }

                                this.mUsbInterface = this.mUsbDevice.getInterface(iInterface);
                                break;
                            }
                        }

                        this.mUsbDeviceConnection = this.mUsbManager.openDevice(this.mUsbDevice);
                        if (this.mUsbDeviceConnection != null && this.mUsbDeviceConnection.claimInterface(this.mUsbInterface, true)) {
                            this.mPortInfo.mUsbPathName = this.mUsbDevice.getDeviceName();
                            this.mIsOpen = true;
                            this.data = new RoundQueue(500);
                            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortSuccess, "Open USB port success !\n", 0);
                        } else {
                            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.OpenPortFailed, "Can't Claims exclusive access to UsbInterface", 0);
                        }
                    }
                }
            }
        }

        PosPrinterDev.ReturnMessage ClosePort() {
            if (this.mUsbDeviceConnection != null) {
                this.mUsbInEndpoint = null;
                this.mUsbOutEndpoint = null;
                this.mUsbDeviceConnection.releaseInterface(this.mUsbInterface);
                this.mUsbDeviceConnection.close();
                this.mUsbDeviceConnection = null;
            }

            this.mIsOpen = false;
            return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ClosePortSuccess, "Close usb connection success !\n", 0);
        }

        PosPrinterDev.ReturnMessage Write(int data) {
            byte[] tem = new byte[]{(byte) (data & 255)};
            return this.Write(tem);
        }

        PosPrinterDev.ReturnMessage Write(byte[] data) {
            return this.Write(data, 0, data.length);
        }

        PosPrinterDev.ReturnMessage Write(byte[] data, int offset, int count) {
            if (!this.mIsOpen) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "USB port was closed !\n", 0);
            } else {
                byte[] temData = new byte[count];

                for (int i = offset; i < offset + count; ++i) {
                    temData[i - offset] = data[i];
                }

                int requesttime = 0;

                try {
                    int writeCount = this.mUsbDeviceConnection.bulkTransfer(this.mUsbOutEndpoint, temData, temData.length, requesttime);
                    Log.i("USBwrite", String.valueOf(writeCount));
                    this.setusbData(writeCount);
                    return writeCount < 0 ? PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n", 0) : PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataSuccess, "send " + writeCount + " bytes.\n", writeCount);
                } catch (NullPointerException var7) {
                    var7.printStackTrace();
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n", 0);
                }
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer, int offset, int count) {
            if (!this.mIsOpen) {
                return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, "USB port was closed !\n", 0);
            } else {
                byte[] temBuffer = new byte[count];
                int readBytes = this.mUsbDeviceConnection.bulkTransfer(this.mUsbInEndpoint, buffer, count, 3000);
                if (readBytes < 0) {
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataFailed, "usb port read bulkTransfer failed !\n", 0);
                } else {
                    for (int i = offset; i < offset + readBytes; ++i) {
                        buffer[i] = temBuffer[i - offset];
                    }

                    this.data.addLast(buffer);
                    Log.e("TAGUsb", Arrays.toString(buffer));
                    return PosPrinterDev.this.new ReturnMessage(PosPrinterDev.ErrorCode.ReadDataSuccess, "Read " + readBytes + " bytes.\n", readBytes);
                }
            }
        }

        PosPrinterDev.ReturnMessage Read(byte[] buffer) {
            return this.Read(buffer, 0, buffer.length);
        }

        int Read() {
            byte[] temBuffer = new byte[1];
            Log.e("TAGUsb", Arrays.toString(temBuffer));
            return this.Read(temBuffer).GetErrorCode() == PosPrinterDev.ErrorCode.OpenPortFailed ? -1 : temBuffer[0];
        }

        public RoundQueue<byte[]> getData() {
            PosPrinterDev.this.usbData = this.data;
            return PosPrinterDev.this.usbData;
        }

        boolean PortIsOpen() {
            if (this.mUsbDevice != null && this.mUsbInEndpoint != null && this.mUsbOutEndpoint != null) {
                new ArrayList();
                List<String> temStr = PosPrinterDev.GetUsbPathNames(this.mPortInfo.mContext);
                if (temStr != null && temStr.size() > 0) {
                    Iterator var3 = temStr.iterator();

                    while (var3.hasNext()) {
                        String str = (String) var3.next();
                        if (str.equals(this.mUsbDevice.getDeviceName())) {
                            return this.mIsOpen = true;
                        }
                    }

                    return this.mIsOpen = false;
                } else {
                    return this.mIsOpen = false;
                }
            } else {
                return this.mIsOpen = false;
            }
        }
    }
}


