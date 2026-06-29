package net.rim.device.cldc.io.lstp;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.api.io.IONotRoutableException;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

final class BluetoothLayer extends SerialLayer implements BluetoothSerialPortListener, GlobalEventListener {
   private BluetoothSerialPort _port;
   private static final boolean DEBUG = true;
   private static final boolean DEBUG_IO = false;
   private static final byte[] UUID = new byte[]{66, 108, 97, 99, 107, 66, 101, 114, 114, 121, 66, 121, 112, 97, 115, 115};

   protected BluetoothLayer(Transport transport) {
      super(transport);
      Application app = Application.getApplication();
      app.addGlobalEventListener(this);
      this.initialize();
   }

   private final void initialize() {
      if (!BluetoothDeviceManager.isWirelessBypassEnabled()) {
         if (this._port != null) {
            this._port.close();
            this._port = null;
            return;
         }
      } else if (this._port == null) {
         StringBuffer sb = new StringBuffer("BlackBerry Bypass Service");
         int pin = DeviceInfo.getDeviceId();
         sb.append(" P:0x");
         sb.append(Integer.toHexString(pin).toUpperCase());
         sb.append(" R:0x0");
         sb.append(Integer.toHexString(RadioInfo.getNetworkType()).toUpperCase());
         sb.append(" V:0x");
         sb.append(Integer.toHexString(131075).toUpperCase());
         this._port = new BluetoothSerialPort(UUID, sb.toString(), 7, 3, 0, 2048, 2048, this);
         return;
      }
   }

   @Override
   protected final MuxerThread createMuxerThread() {
      return new BluetoothMuxerThread(this);
   }

   @Override
   protected final void nativeOpen() {
      this._port.setDsr(true);
      super._dataAvailable = 1;
      this.sendAck((byte)57);
   }

   @Override
   protected final void nativeClose() {
   }

   @Override
   protected final void configure(int baudrate, int fragmentSize) {
      super.configure(baudrate, fragmentSize);
   }

   @Override
   protected final int nativeRead(byte[] b, int offset, int length) throws IONotRoutableException {
      if (this._port == null) {
         throw new IONotRoutableException();
      } else {
         return this._port.read(b, offset, length);
      }
   }

   private final void dumpArray(byte[] b, int length) {
      for (int i = 0; i < length; i++) {
         String s = Integer.toHexString(b[i] & 255);
         if (s.length() == 1) {
            System.out.print('0');
         }

         System.out.print(s);
      }

      System.out.println();
   }

   @Override
   protected final int nativeWrite(byte[] b, int offset, int length) throws IONotRoutableException {
      if (this._port == null) {
         throw new IONotRoutableException();
      } else {
         return this._port.write(b, offset, length);
      }
   }

   @Override
   protected final void nativeEnableDtrFix() {
   }

   @Override
   protected final void shutdownReceiveThread() {
      this.dataReceived(0);
   }

   @Override
   public final void deviceConnected(boolean success) {
      System.out.println("lstp: deviceConnected " + success);
      if (success && !super._lstpUtil.getLinkState()) {
         this.startNativeLayer();
      }
   }

   @Override
   public final void deviceDisconnected() {
      System.out.println("lstp: deviceDisconnected");
      this.close(true);
   }

   @Override
   public final void dtrStateChange(boolean high) {
      System.out.println("lstp: dtrStateChange " + high);
   }

   @Override
   public final void dataReceived(int length) {
      synchronized (super._receiveLock) {
         super._dataAvailable = 1;
         super._receiveLock.notifyAll();
      }
   }

   @Override
   public final void dataSent() {
      synchronized (super._sendLock) {
         super._sendLock.notifyAll();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L) {
         try {
            this.initialize();
         } finally {
            return;
         }
      }
   }
}
