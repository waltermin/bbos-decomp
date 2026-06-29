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
   private static final boolean DEBUG_TIMING;

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
         ((StringBuffer)(new Object()))
            .append(this.this$0._name)
            .append(" BluetoothSerialPortListener.deviceConnected( ")
            .append(success)
            .append(" ). Status: ")
            .append(this.this$0._status)
            .toString()
            .getBytes(),
         5
      );
      if (success) {
         this.this$0.stopReconnect();
         this.this$0._reconnectAttempt = 1;
         GPSProvider.getInstance().fireLocationDeviceEvent(this.this$0, this.this$0.getString(5));
         this.this$0._waitGPSDeviceID = Application.getApplication().invokeLater(new BluetoothGPSLocationDevice$1(this), 15000, false);
         this.this$0.programSiRF();
      } else {
         System.out
            .println(
               ((StringBuffer)(new Object("deviceConnected(false), status=")))
                  .append(this.this$0._status)
                  .append(" reconnectAttempt=")
                  .append(this.this$0._reconnectAttempt)
                  .toString()
            );
         this.this$0.attemptReconnect(this.this$0._status != 8);
      }
   }

   @Override
   public void deviceDisconnected() {
      EventLogger.logEvent(
         4560142210062134028L,
         ((StringBuffer)(new Object()))
            .append(this.this$0._name)
            .append(" BluetoothSerialPortListener.deviceDisconnected(). Status: ")
            .append(this.this$0._status)
            .toString()
            .getBytes(),
         5
      );
      System.out
         .println(
            ((StringBuffer)(new Object("deviceDisconnected, status=")))
               .append(this.this$0._status)
               .append(" reconnectAttempt=")
               .append(this.this$0._reconnectAttempt)
               .toString()
         );
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
            ((StringBuffer)(new Object()))
               .append(this.this$0._name)
               .append(" connecting to Bluetooth, attempt #")
               .append(this.this$0._reconnectAttempt)
               .append(". Status: ")
               .append(this.this$0._status)
               .toString()
               .getBytes(),
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

            this._port = (BluetoothSerialPort)(new Object(this.this$0._btPortInfo, 1, 3, 0, 2000, 2000, this));
            System.out
               .println(
                  ((StringBuffer)(new Object("BluetoothDataThread.run(), status=")))
                     .append(this.this$0._status)
                     .append(" reconnectAttempt=")
                     .append(this.this$0._reconnectAttempt)
                     .append(" port=")
                     .append(this._port)
                     .toString()
               );
         } catch (Throwable var12) {
            this.this$0._status = 10;
            EventLogger.logEvent(
               4560142210062134028L,
               ((StringBuffer)(new Object("IOEx BluetoothSerialPortInfo for ")))
                  .append(this.this$0._name)
                  .append(" msg:")
                  .append(ioex.getMessage())
                  .append(". Status: ")
                  .append(this.this$0._status)
                  .toString()
                  .getBytes(),
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
            EventLogger.logEvent(
               4560142210062134028L,
               ((StringBuffer)(new Object("Message device error, data:")))
                  .append((String)(new Object(msg)))
                  .append(". Status: ")
                  .append(this.this$0._status)
                  .toString()
                  .getBytes(),
               2
            );
            return result;
         }
      } else {
         return result;
      }
   }
}
