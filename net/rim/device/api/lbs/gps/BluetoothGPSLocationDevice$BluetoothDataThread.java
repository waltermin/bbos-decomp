package net.rim.device.api.lbs.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothMEListener;

class BluetoothGPSLocationDevice$BluetoothDataThread extends Thread implements BluetoothSerialPortListener, BluetoothMEListener {
   private BluetoothSerialPort _port;
   private Object _lock;
   private boolean _poweredOff;
   long _start;
   long _parseTime;
   NmeaStream _nmeaStream;
   private final BluetoothGPSLocationDevice this$0;
   private static final boolean DEBUG_TIMING = false;

   void appendIncomingData(int length) {
      this._nmeaStream.appendData(this._port, length);
      if (this._nmeaStream.length() > 50) {
         boolean wasNotReporting = this.this$0._status != 1;
         if (wasNotReporting) {
            this.this$0._status = 1;
         }

         if (this._nmeaStream.parseBuffer(this.this$0._locationDataInternal)) {
            GPSProvider.getInstance().updateLocationData(true);
         }

         if (wasNotReporting && this.this$0._locationDataInternal._satelliteCount == 0) {
            GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(16));
         }
      }
   }

   @Override
   public void dataReceived(int length) {
      this.appendIncomingData(length);
   }

   @Override
   public void dataSent() {
   }

   @Override
   public void deviceConnected(boolean success) {
      EventLogger.logEvent(
         4560142210062134028L,
         (this.this$0._name + " BluetoothSerialPortListener.deviceConnected( " + success + " ). Status: " + this.this$0._status).getBytes(),
         5
      );
      if (success) {
         this.this$0.stopReconnect();
         this.this$0._reconnectAttempt = 1;
         GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(5));
         this.this$0._waitGPSDeviceID = Application.getApplication().invokeLater(new BluetoothGPSLocationDevice$1(this), 15000, false);
         this.this$0.programSiRF();
      } else {
         System.out.println("deviceConnected(false), status=" + this.this$0._status + " reconnectAttempt=" + this.this$0._reconnectAttempt);
         this.this$0.attemptReconnect(this.this$0._status != 8);
      }
   }

   @Override
   public void deviceDisconnected() {
      EventLogger.logEvent(
         4560142210062134028L, (this.this$0._name + " BluetoothSerialPortListener.deviceDisconnected(). Status: " + this.this$0._status).getBytes(), 5
      );
      System.out.println("deviceDisconnected, status=" + this.this$0._status + " reconnectAttempt=" + this.this$0._reconnectAttempt);
      if (this.this$0._status != 8) {
         this.this$0.attemptReconnect(false);
      }
   }

   @Override
   public void dtrStateChange(boolean high) {
   }

   @Override
   public void powerOffComplete() {
      this._poweredOff = true;
      this.this$0.forceStopReporting();
   }

   @Override
   public void powerOnComplete(boolean success) {
   }

   private boolean stopBluetooth() {
      boolean active = this._port != null;
      if (active) {
         this._port.close();
      }

      this._port = null;
      return active;
   }

   BluetoothGPSLocationDevice$BluetoothDataThread(BluetoothGPSLocationDevice this$0) {
      this.this$0 = this$0;
      this._lock = new Object();
      this._poweredOff = false;
      this._nmeaStream = new NmeaStream();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      if (!this._poweredOff) {
         int status = BluetoothME.requestPowerOn();
         if (status == 2) {
            GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(2));
         } else {
            GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(3));
         }

         EventLogger.logEvent(
            4560142210062134028L,
            (this.this$0._name + " connecting to Bluetooth, attempt #" + this.this$0._reconnectAttempt + ". Status: " + this.this$0._status).getBytes(),
            5
         );

         label80:
         try {
            if (status == 2) {
               label75:
               try {
                  synchronized (this._lock) {
                     this._lock.wait(3000);
                  }
               } finally {
                  break label75;
               }
            }

            this._port = new BluetoothSerialPort(this.this$0._btPortInfo, 1, 3, 0, 2000, 2000, this);
            System.out
               .println(
                  "BluetoothDataThread.run(), status=" + this.this$0._status + " reconnectAttempt=" + this.this$0._reconnectAttempt + " port=" + this._port
               );
         } catch (Throwable var12) {
            this.this$0._status = 10;
            EventLogger.logEvent(
               4560142210062134028L,
               ("IOEx BluetoothSerialPortInfo for " + this.this$0._name + " msg:" + ioex.getMessage() + ". Status: " + this.this$0._status).getBytes(),
               2
            );
            break label80;
         }

         if (this.this$0._status == 10) {
            GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(4));
         }
      }
   }

   private boolean write(byte[] msg) {
      boolean result = false;
      if (this._port != null) {
         try {
            return this._port.write(msg) == msg.length;
         } finally {
            EventLogger.logEvent(4560142210062134028L, ("Message device error, data:" + new String(msg) + ". Status: " + this.this$0._status).getBytes(), 2);
            return result;
         }
      } else {
         return result;
      }
   }
}
