package net.rim.device.api.lbs.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.bluetooth.BluetoothME;

final class BluetoothGPSLocationDevice extends GPSDevice {
   private byte[] _deviceAddress;
   private BluetoothSerialPortInfo _btPortInfo;
   private BluetoothGPSLocationDevice$BluetoothDataThread _bluetoothDataThread;
   private int _reconnectAttempt;
   private int _reconnectThreadID = -1;
   private int _waitGPSDeviceID = -1;
   private static final int MAX_RECONNECT_ATTEMPTS;
   private static final String[] NMEAdiscards = new String[]{"01", "02", "03", "05"};
   private static final String[] NMEAwanted = new String[]{"00", "04"};

   BluetoothGPSLocationDevice(Object param) {
      super(param);
      this._btPortInfo = (BluetoothSerialPortInfo)param;
      this._deviceAddress = this._btPortInfo.getDeviceAddress();
      super._name = this._btPortInfo.getDeviceName();
      if (super._name == null || super._name.equals("")) {
         super._name = this.getString(14);
      }

      super._deviceID = new Object(this._deviceAddress);
   }

   @Override
   public final void setDeviceToUse() {
   }

   @Override
   public final boolean isInternalGPS() {
      return false;
   }

   @Override
   public final boolean messageDevice(Object contextMessage) {
      boolean result = false;
      if (contextMessage instanceof byte[] && this._bluetoothDataThread != null) {
         byte[] msg = (byte[])contextMessage;
         result = this._bluetoothDataThread.write(msg);
      }

      return result;
   }

   @Override
   protected final GPSLocationData singleFix() {
      GPSLocationData data = new GPSLocationData();
      data.setValid(false);
      if (super._status == 1) {
         super._locationDataInternal.copyInto(data);
         return data;
      }

      if (super._status != 4 && super._status != 10) {
         long _fixTimeEnd = System.currentTimeMillis() + 900000;
         BluetoothGPSLocationDevice$SingleFixListener singleFix = new BluetoothGPSLocationDevice$SingleFixListener(this, data);
         Application.getApplication().invokeAndWait(singleFix);

         while (!data._isValid && !singleFix.isStopped() && System.currentTimeMillis() < _fixTimeEnd) {
         }

         if (System.currentTimeMillis() > _fixTimeEnd) {
            singleFix.stop();
            return data;
         } else {
            data._isValid = false;
            return data;
         }
      } else {
         return null;
      }
   }

   @Override
   protected final boolean startReporting() {
      GPSProvider.getInstance().fireLocationDeviceEvent(this, this.getString(15));
      if (super._status != 8) {
         super._status = 2;
      }

      if (this._waitGPSDeviceID != -1) {
         Application.getApplication().cancelInvokeLater(this._waitGPSDeviceID);
      }

      Backlight.enable(true);
      this._waitGPSDeviceID = -1;
      super._locationDataInternal.reset();
      if (this._bluetoothDataThread == null) {
         this._bluetoothDataThread = new BluetoothGPSLocationDevice$BluetoothDataThread(this);
      }

      BluetoothME.addListener(Application.getApplication(), this._bluetoothDataThread);
      Application.getApplication().invokeLater(this._bluetoothDataThread);
      System.out
         .println(
            ((StringBuffer)(new Object("start reporting, status=")))
               .append(super._status)
               .append(" reconnectAttempt=")
               .append(this._reconnectAttempt)
               .toString()
         );
      return true;
   }

   @Override
   protected final boolean stopReporting() {
      System.out
         .println(
            ((StringBuffer)(new Object("stopReporting, status="))).append(super._status).append(" reconnectAttempt=").append(this._reconnectAttempt).toString()
         );
      EventLogger.logEvent(
         4560142210062134028L,
         ((StringBuffer)(new Object())).append(super._name).append(" reporting stopped. Status: ").append(super._status).toString().getBytes(),
         5
      );
      this.stopReconnect();
      this._reconnectAttempt = 0;
      boolean active = false;
      if (this._bluetoothDataThread != null) {
         BluetoothME.removeListener(Application.getApplication(), this._bluetoothDataThread);
         active = this._bluetoothDataThread.stopBluetooth();
      }

      if (this._waitGPSDeviceID != -1) {
         Application.getApplication().cancelInvokeLater(this._waitGPSDeviceID);
      }

      this._waitGPSDeviceID = -1;
      this._bluetoothDataThread = null;
      super._status = 0;
      this.setGPSinvalid();
      return active;
   }

   @Override
   public final boolean equals(Object obj) {
      EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("equals obj: "))).append(obj).toString().getBytes(), 0);
      if (obj instanceof BluetoothGPSLocationDevice) {
         return this.compareByteArrays(this._deviceAddress, ((BluetoothGPSLocationDevice)obj)._deviceAddress);
      } else {
         return obj instanceof Object ? obj.equals(this.getDeviceID()) : super.equals(obj);
      }
   }

   private final boolean compareByteArrays(byte[] first, byte[] second) {
      if (first != null && second != null && first.length == second.length) {
         for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private final void forceStopReporting() {
      EventLogger.logEvent(
         4560142210062134028L,
         ((StringBuffer)(new Object()))
            .append(super._name)
            .append(" Bluetooth never connected for ")
            .append(this._reconnectAttempt)
            .append(" attempts. Status: ")
            .append(super._status)
            .toString()
            .getBytes(),
         5
      );
      super._status = 4;
      GPSProvider.getInstance().fireLocationDeviceEvent(this, this.getString(7));
      this.stopReporting();
   }

   private final void attemptReconnect(boolean retryNow) {
      if (this._reconnectAttempt >= 10) {
         this.forceStopReporting();
      } else {
         if (this._waitGPSDeviceID != -1) {
            Application.getApplication().cancelInvokeLater(this._waitGPSDeviceID);
         }

         this._waitGPSDeviceID = -1;
         if (this._bluetoothDataThread != null) {
            this._bluetoothDataThread.stopBluetooth();
         }

         System.out
            .println(
               ((StringBuffer)(new Object("attemptReconnect(")))
                  .append(retryNow)
                  .append("), status=")
                  .append(super._status)
                  .append(" reconnectAttempt=")
                  .append(this._reconnectAttempt)
                  .toString()
            );
         super._status = 8;
         if (retryNow) {
            this.startReporting();
         } else {
            GPSProvider.getInstance().fireLocationDeviceEvent(this, this.getString(8));
            this._reconnectThreadID = Application.getApplication().invokeLater(new BluetoothGPSLocationDevice$2(this), 60000, false);
         }
      }
   }

   private final void stopReconnect() {
      if (this._reconnectThreadID != -1) {
         Application.getApplication().cancelInvokeLater(this._reconnectThreadID);
      }

      this._reconnectThreadID = -1;
   }

   private final void programSiRF() {
      Application.getApplication().invokeLater(new BluetoothGPSLocationDevice$3(this), 100, false);
   }

   static final int access$208(BluetoothGPSLocationDevice x0) {
      return x0._reconnectAttempt++;
   }
}
