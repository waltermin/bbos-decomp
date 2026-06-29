package net.rim.device.cldc.io.btspp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public class BluetoothSerialConnection implements SocketConnection, BluetoothSerialPortListener, BluetoothConnection {
   private boolean _connected;
   private boolean _closed;
   private boolean _isServer;
   private BluetoothSerialPortInfo _info;
   private BluetoothSerialPort _port;
   private int _bytesAvailable;
   private Object _semaphore = new Object();
   private Object _writeSemaphore = new Object();
   private InputStream _input;
   private OutputStream _output;
   private long _readTimeout;
   private static final long TIMEOUT_PERIOD = 60000L;
   private static final boolean DEBUG = false;

   @Override
   public void close() {
      this._closed = true;
      if (!this._isServer) {
         this._port.close();
      } else {
         this._port.disconnect();
      }

      this.deviceDisconnected();
   }

   @Override
   public boolean isConnected() {
      return this._connected;
   }

   @Override
   public void deviceConnected(boolean success) {
      synchronized (this._semaphore) {
         this._connected = success;
         this._semaphore.notifyAll();
      }
   }

   @Override
   public void deviceDisconnected() {
      synchronized (this._semaphore) {
         this._connected = false;
         this._semaphore.notifyAll();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notifyAll();
      }
   }

   @Override
   public void dtrStateChange(boolean high) {
   }

   @Override
   public void dataReceived(int length) {
      synchronized (this._semaphore) {
         this._bytesAvailable += length;
         this._semaphore.notifyAll();
      }
   }

   @Override
   public void dataSent() {
      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notifyAll();
      }
   }

   @Override
   public InputStream openInputStream() {
      this.checkClosed();
      this.waitForConnection();
      if (this._input == null) {
         this._input = new BluetoothSerialConnection$BluetoothInputStream(this);
      }

      return this._input;
   }

   @Override
   public DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this.openInputStream()));
   }

   @Override
   public OutputStream openOutputStream() {
      this.checkClosed();
      this.waitForConnection();
      if (this._output == null) {
         this._output = new BluetoothSerialConnection$BluetoothOutputStream(this);
      }

      return this._output;
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      return (DataOutputStream)(new Object(this.openOutputStream()));
   }

   @Override
   public byte[] getRemoteAddress() {
      return this._port == null ? null : this._port.getRemoteAddress();
   }

   @Override
   public void setSocketOption(byte option, int value) {
   }

   @Override
   public int getSocketOption(byte option) {
      return -1;
   }

   @Override
   public String getLocalAddress() {
      return null;
   }

   @Override
   public int getLocalPort() {
      return -1;
   }

   @Override
   public String getAddress() {
      return null;
   }

   @Override
   public int getPort() {
      return -1;
   }

   public BluetoothSerialConnection(BluetoothSerialPortInfo info, BluetoothSerialPort port) {
      if (info != null && port != null) {
         this._isServer = true;
         this._info = info;
         this._port = port;
         this._bytesAvailable = 0;
         this._connected = true;
      } else {
         throw new Object();
      }
   }

   public BluetoothSerialConnection(byte[] address, int channel, int maxPacketSize, boolean allowReadTimeouts) {
      int pageScanInfo = 0;
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      BluetoothDevice device = btManager.getInRangeDevice(address);
      if (device != null) {
         pageScanInfo = device.getPageScanInfo();
      }

      this._info = (BluetoothSerialPortInfo)(new Object(address, pageScanInfo, null, channel, null));
      this._isServer = false;
      this._bytesAvailable = 0;
      if (allowReadTimeouts) {
         this._readTimeout = 60000;
      }

      this._port = (BluetoothSerialPort)(new Object(this._info, 3, 3, 0, maxPacketSize, maxPacketSize, this));
      synchronized (this._semaphore) {
         label43:
         try {
            this._semaphore.wait(60000);
         } finally {
            break label43;
         }
      }

      if (!this._connected) {
         this._port.close();
         throw new Object("Unable to connect.");
      }
   }

   private void checkClosed() {
      if (this._closed) {
         throw new Object();
      }
   }

   private void waitForConnection() {
      synchronized (this._semaphore) {
         if (!this._connected) {
            label38:
            try {
               this._semaphore.wait(60000);
            } finally {
               break label38;
            }

            if (!this._connected) {
               throw new Object("Unable to connect.");
            }
         }
      }
   }

   public BluetoothSerialConnection(BluetoothSerialPort port) {
      if (port == null) {
         throw new Object();
      }

      this._isServer = true;
      this._port = port;
      this._bytesAvailable = 0;
      this._connected = true;
   }

   static int access$110(BluetoothSerialConnection x0) {
      return x0._bytesAvailable--;
   }

   static int access$120(BluetoothSerialConnection x0, int x1) {
      return x0._bytesAvailable -= x1;
   }
}
