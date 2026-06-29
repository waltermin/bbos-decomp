package net.rim.device.api.bluetooth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.IOPort;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothSDP;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.vm.Message;
import net.rim.vm.Process;

public final class BluetoothSerialPort extends IOPort {
   private int _portHandle = -1;
   private boolean _isServer;
   private BluetoothSerialPortListener _listener;
   private Application _app;
   private Runnable _cleanupRunnable;
   private byte[] _remoteAddress;
   private int _sdpRecordHandle = -1;
   public static final int BAUD_2400 = 0;
   public static final int BAUD_4800 = 1;
   public static final int BAUD_7200 = 2;
   public static final int BAUD_9600 = 3;
   public static final int BAUD_19200 = 4;
   public static final int BAUD_38400 = 5;
   public static final int BAUD_57600 = 6;
   public static final int BAUD_115200 = 7;
   public static final int BAUD_230400 = 8;
   public static final int DATA_FORMAT_DATA_BITS_5 = 0;
   public static final int DATA_FORMAT_DATA_BITS_6 = 2;
   public static final int DATA_FORMAT_DATA_BITS_7 = 1;
   public static final int DATA_FORMAT_DATA_BITS_8 = 3;
   public static final int DATA_FORMAT_STOP_BITS_1 = 0;
   public static final int DATA_FORMAT_STOP_BITS_1_5 = 4;
   public static final int DATA_FORMAT_PARITY_NONE = 0;
   public static final int DATA_FORMAT_PARITY_ON = 8;
   public static final int DATA_FORMAT_PARITY_ODD = 0;
   public static final int DATA_FORMAT_PARITY_EVEN = 32;
   public static final int DATA_FORMAT_PARITY_MARK = 16;
   public static final int DATA_FORMAT_PARITY_SPACE = 48;
   public static final int FLOW_CONTROL_NONE = 0;
   public static final int FLOW_CONTROL_RTC_CTS = 12;
   public static final int FLOW_CONTROL_DTR_DSR = 48;
   public static final int FLOW_CONTROL_XON_XOFF = 3;
   private static final boolean DEBUG = false;
   public static final byte[] DEFAULT_UUID = new byte[]{0, 0, 17, 1, 0, 0, 16, 0, -128, 0, 0, -128, 95, -101, 52, -5};

   private static final void assertPermission() {
      ApplicationControl.assertBluetoothSerialProfileAllowed(true);
   }

   private BluetoothSerialPort() throws IOException {
      if (ITPolicy.getBoolean(34, 5, false)) {
         throw new IOException("Disabled by IT Policy");
      }

      if (!isSupported()) {
         throw new UnsupportedOperationException();
      }

      this._app = Application.getApplication();
   }

   public BluetoothSerialPort(
      BluetoothSerialPortInfo info, int baudRate, int dataFormat, int flowControl, int rxBufferSize, int txBufferSize, BluetoothSerialPortListener listener
   ) throws IOException {
      this();
      this.addListener(listener);
      this._remoteAddress = info.getDeviceAddress();

      try {
         this._portHandle = openClientPort(
            this._remoteAddress, info.getDevicePageScanInfo(), info.getServerID(), baudRate, dataFormat, flowControl, rxBufferSize, txBufferSize
         );
      } catch (IOException e) {
         this._app.removeListener(42, this);
         throw e;
      }

      this._cleanupRunnable = new BluetoothSerialPort$BluetoothSerialPortCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
   }

   public BluetoothSerialPort(
      String serviceName, int baudRate, int dataFormat, int flowControl, int rxBufferSize, int txBufferSize, BluetoothSerialPortListener listener
   ) {
      this(null, serviceName, baudRate, dataFormat, flowControl, rxBufferSize, txBufferSize, listener);
   }

   public BluetoothSerialPort(
      byte[] uuid, String serviceName, int baudRate, int dataFormat, int flowControl, int rxBufferSize, int txBufferSize, BluetoothSerialPortListener listener
   ) {
      this(baudRate, dataFormat, flowControl, rxBufferSize, txBufferSize, listener);
      this.addDefaultSDPRecord(uuid, serviceName);
   }

   public BluetoothSerialPort(int baudRate, int dataFormat, int flowControl, int rxBufferSize, int txBufferSize, BluetoothSerialPortListener listener) throws IOException {
      this();
      this._isServer = true;
      this.addListener(listener);

      try {
         this._portHandle = openServerPort(baudRate, dataFormat, flowControl, rxBufferSize, txBufferSize);
      } catch (IOException e) {
         this._app.removeListener(42, this);
         throw e;
      }

      this._cleanupRunnable = new BluetoothSerialPort$BluetoothSerialPortCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
   }

   public final int getSDPRecordHandle() {
      return this._sdpRecordHandle;
   }

   public final void addSDPRecord(int[] attributeIDs, byte[][] attributeValues, int classOfDevice) throws IOException {
      if (this._sdpRecordHandle == -1) {
         int securityLevel = 11;
         this._sdpRecordHandle = BluetoothSDP.addRecord(
            this._portHandle, false, attributeIDs, attributeValues, classOfDevice, 0, this.getRFCOMMChannel(), securityLevel
         );
         if (this._sdpRecordHandle == -1) {
            throw new IOException("Unable to register SDP record");
         }
      }
   }

   public final byte[] getRemoteAddress() {
      return this._remoteAddress;
   }

   public final int getRFCOMMChannel() {
      return getRFCOMMChannel(this._portHandle);
   }

   private final void addDefaultSDPRecord(byte[] uuid, String serviceName) {
      if (uuid == null) {
         uuid = DEFAULT_UUID;
      }

      byte[] nameBytes = null;

      try {
         nameBytes = serviceName.getBytes("UTF8");
      } catch (UnsupportedEncodingException var11) {
      }

      int nameBytesLength = nameBytes.length;
      if (nameBytesLength > 256) {
         throw new IllegalArgumentException();
      }

      int numAttributes = 6;
      int[] attributeIDs = new int[numAttributes];
      byte[][] attributeValues = new byte[numAttributes][];
      DataBuffer buf = new DataBuffer();
      int i = 0;
      attributeIDs[i] = 1;
      buf.writeByte(53);
      int uuidLength = uuid.length;
      buf.writeByte(4 + uuidLength);
      switch (uuidLength) {
         case 2:
            buf.writeByte(25);
            break;
         case 4:
            buf.writeByte(26);
            break;
         case 16:
            buf.writeByte(28);
            break;
         default:
            throw new IllegalArgumentException();
      }

      buf.write(uuid, 0, uuidLength);
      buf.writeByte(25);
      buf.writeShort(4353);
      attributeValues[i++] = buf.toArray();
      attributeIDs[i] = 4;
      buf.reset();
      buf.writeByte(53);
      buf.writeByte(12);
      buf.writeByte(53);
      buf.writeByte(3);
      buf.writeByte(25);
      buf.writeShort(256);
      buf.writeByte(53);
      buf.writeByte(5);
      buf.writeByte(25);
      buf.writeShort(3);
      buf.writeByte(8);
      buf.writeByte(getRFCOMMChannel(this._portHandle));
      attributeValues[i++] = buf.toArray();
      attributeIDs[i] = 5;
      buf.reset();
      buf.writeByte(53);
      buf.writeByte(3);
      buf.writeByte(25);
      buf.writeShort(4098);
      attributeValues[i++] = buf.toArray();
      attributeIDs[i] = 6;
      buf.reset();
      buf.writeByte(53);
      buf.writeByte(9);
      buf.writeByte(9);
      buf.writeShort(25966);
      buf.writeByte(9);
      buf.writeShort(106);
      buf.writeByte(9);
      buf.writeShort(256);
      attributeValues[i++] = buf.toArray();
      attributeIDs[i] = 8;
      buf.reset();
      buf.writeByte(8);
      buf.writeByte(255);
      attributeValues[i++] = buf.toArray();
      attributeIDs[i] = 256;
      buf.reset();
      buf.writeByte(37);
      buf.writeByte(nameBytesLength + 1);
      buf.write(nameBytes, 0, nameBytesLength);
      buf.writeByte(0);
      attributeValues[i++] = buf.toArray();
      this.addSDPRecord(attributeIDs, attributeValues, 0);
   }

   public static final boolean isSupported() {
      return BluetoothME.isSupported();
   }

   public final void setProperties(int baudRate, int dataFormat, int flowControl) {
   }

   public final void setDsr(boolean state) {
      assertPermission();
      setDsr(this._portHandle, state);
   }

   public final boolean getDtr() {
      assertPermission();
      return getDtr(this._portHandle);
   }

   public final void disconnect() {
      if (this._isServer) {
         assertPermission();
         if (this._portHandle != -1) {
            disconnect(this._portHandle);
            return;
         }
      } else {
         this.close();
      }
   }

   @Override
   public final void close() {
      assertPermission();
      this.closePort();
      this._app.removeListener(42, this);
      ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
   }

   private final void closePort() {
      if (this._portHandle != -1) {
         close(this._portHandle);
         this._portHandle = -1;
      }

      if (this._sdpRecordHandle != -1) {
         BluetoothSDP.removeRecord(this._sdpRecordHandle);
         this._sdpRecordHandle = -1;
      }

      this._remoteAddress = null;
   }

   @Override
   public final int write(byte[] data) {
      return this.write(data, 0, data.length);
   }

   @Override
   public final int write(byte[] data, int offset, int length) {
      assertPermission();
      return write(this._portHandle, data, offset, length);
   }

   @Override
   public final int write(int b) {
      assertPermission();
      return write(this._portHandle, b);
   }

   @Override
   public final int read(byte[] data) {
      return this.read(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      assertPermission();
      return read(this._portHandle, data, offset, length);
   }

   @Override
   public final int read() {
      assertPermission();
      return read(this._portHandle);
   }

   public static final BluetoothSerialPortInfo[] getSerialPortInfo() {
      assertPermission();
      BluetoothDeviceManager btManager = BluetoothDeviceManager.getInstance();
      if (btManager == null) {
         throw new UnsupportedOperationException();
      } else {
         return btManager.getSerialPortInfo();
      }
   }

   private static final native int openClientPort(byte[] var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   private static final native int openServerPort(int var0, int var1, int var2, int var3, int var4);

   private static final native void disconnect(int var0);

   private static final native void close(int var0);

   private static final native int write(int var0, byte[] var1, int var2, int var3);

   private static final native int write(int var0, int var1);

   private static final native int read(int var0, byte[] var1, int var2, int var3);

   private static final native int read(int var0);

   private static final native void setDsr(int var0, boolean var1);

   private static final native boolean getDtr(int var0);

   private static final native int getRFCOMMChannel(int var0);

   private final void addListener(BluetoothSerialPortListener listener) {
      assertPermission();
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(42) == null) {
            dispatchManager.setDispatcher(42, new BluetoothSerialEventDispatcher());
         }
      }

      this._listener = listener;
      this._app.addListener(42, this);
   }

   final void dispatch(Message message) {
      if (message.getSubMessage() == this._portHandle) {
         int data0 = message.getData0();
         switch (message.getEvent()) {
            case 10752:
               break;
            case 10753:
               this._listener.dtrStateChange(data0 != 0);
               return;
            case 10754:
               this._listener.dataReceived(data0);
               return;
            case 10755:
               this._listener.dataSent();
               break;
            case 10756:
            default:
               boolean success = data0 == 0;
               this._listener.deviceConnected(success);
               this._remoteAddress = (byte[])message.getObject0();
               if (this._sdpRecordHandle != -1) {
                  BluetoothSDP.updateServiceAvailability(this._sdpRecordHandle, 0);
                  return;
               }
               break;
            case 10757:
               this._listener.deviceDisconnected();
               this._remoteAddress = null;
               if (this._sdpRecordHandle != -1) {
                  BluetoothSDP.updateServiceAvailability(this._sdpRecordHandle, 255);
                  return;
               }
         }
      }
   }
}
