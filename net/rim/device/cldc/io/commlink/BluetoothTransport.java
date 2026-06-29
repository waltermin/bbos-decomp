package net.rim.device.cldc.io.commlink;

import java.io.IOException;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

final class BluetoothTransport extends SerialTransport implements BluetoothSerialPortListener, GlobalEventListener {
   private BluetoothSerialPort _port;
   private static final boolean DEBUG = false;
   private static final boolean DEBUG_IO = false;
   private static final byte[] UUID = new byte[]{66, 108, 97, 99, 107, 66, 101, 114, 114, 121, 68, 115, 107, 116, 111, 112};

   BluetoothTransport(Transport transport, ProfileString profile) {
      super(transport, profile);
      Application app = Application.getApplication();
      app.addGlobalEventListener(this);
      this.initialize();
   }

   private final void initialize() {
      if (!BluetoothDeviceManager.isDesktopConnectivityEnabled()) {
         if (this._port != null) {
            this._port.close();
            this._port = null;
         }
      } else if (this._port == null) {
         StringBuffer sb = new StringBuffer("BlackBerry Desktop Service");
         int pin = DeviceInfo.getDeviceId();
         sb.append(" P:0x");
         sb.append(Integer.toHexString(pin).toUpperCase());
         sb.append(" R:0x0");
         sb.append(Integer.toHexString(RadioInfo.getNetworkType()).toUpperCase());
         sb.append(" V:0x");
         sb.append(Integer.toHexString(131076).toUpperCase());
         this._port = new BluetoothSerialPort(UUID, sb.toString(), 7, 3, 0, 1024, 1024, this);
         return;
      }
   }

   @Override
   protected final boolean lowLevelOpen(int baud) {
      this.init();
      super._timeout = 0;
      return true;
   }

   @Override
   protected final void lowLevelClose() {
      this.fini();
   }

   @Override
   protected final int lowLevelRead(byte[] b) throws IOException {
      if (this._port == null) {
         throw new IOException();
      } else {
         return this._port.read(b);
      }
   }

   private final void dumpArray(byte[] b, int offset, int length) {
      for (int i = 0; i < length; i++) {
         String s = Integer.toHexString(b[i + offset] & 255);
         if (s.length() == 1) {
            System.out.print('0');
         }

         System.out.print(s);
      }

      System.out.println();
   }

   @Override
   protected final int lowLevelWrite(byte[] b, int off, int len) throws IOException {
      if (this._port == null) {
         throw new IOException();
      } else {
         return this._port.write(b, off, len);
      }
   }

   @Override
   protected final void flushReadBuffer() {
   }

   @Override
   protected final void setBaud(int baud) {
   }

   @Override
   protected final void setDsr(boolean state) {
   }

   @Override
   protected final boolean getDtr() {
      return super._dtr;
   }

   @Override
   final void standbyMode() {
   }

   @Override
   public final void connected() {
   }

   @Override
   final int sendChallenge(int challengeType) {
      if (challengeType != 2 && challengeType != 18 && ITPolicy.getBoolean(34, 17, false)) {
         return 32768;
      }

      if (super._security.isPasswordEnabled()) {
         super._transport._statusScreen.pop();
      }

      return super.sendChallenge(challengeType);
   }

   @Override
   final boolean checkResponse(byte[] rxBuffer, int off, int len) {
      if (super._security.isPasswordEnabled()) {
         super._transport._statusScreen.push();
      }

      return super.checkResponse(rxBuffer, off, len);
   }

   protected final int getBufferSize() {
      return 1024;
   }

   @Override
   public final void deviceConnected(boolean success) {
      if (success) {
         if (super._transport.startThread(this)) {
            synchronized (super._dtrSemaphore) {
               super._dtr = true;
               super._dtrSemaphore.notify();
            }
         }
      }
   }

   @Override
   public final void deviceDisconnected() {
      synchronized (super._readSemaphore) {
         super._exception = new DTRException();
         super._readSemaphore.notify();
      }

      synchronized (super._dtrSemaphore) {
         super._dtr = false;
         super._dtrSemaphore.notify();
      }
   }

   @Override
   public final void dtrStateChange(boolean high) {
   }

   @Override
   public final void dataReceived(int length) {
      synchronized (super._readSemaphore) {
         super._readSemaphore.notify();
      }
   }

   @Override
   public final void dataSent() {
      synchronized (super._writeSemaphore) {
         label25:
         try {
            this.sendWriteBuffer();
         } finally {
            break label25;
         }

         super._writeSemaphore.notify();
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
