package net.rim.device.cldc.io.btspp;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.bluetooth.ServiceRegistrationException;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.apps.internal.bluetooth.LocalServiceRecord;
import net.rim.device.internal.bluetooth.BluetoothME;

public class BluetoothServerSocketConnection implements BluetoothServerConnection, ServerSocketConnection, BluetoothSerialPortListener {
   private Object _semaphore = new Object();
   private BluetoothSerialPort _port = new BluetoothSerialPort(3, 3, 0, 2048, 2048, this);
   private LocalServiceRecord _serviceRecord;
   private boolean _serviceRecordAdded;
   private BluetoothStreamConnection _connection;
   private boolean _connectionInUse;
   private static final String PORT_NOT_OPEN = "Port not open";
   private static final boolean DEBUG = false;

   @Override
   public void close() {
      synchronized (this._semaphore) {
         if (this._port != null) {
            if (this._connection != null) {
               this._connection.close();
            }

            this._port.close();
            this._port = null;
         }

         this._semaphore.notifyAll();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public StreamConnection acceptAndOpen() {
      synchronized (this._semaphore) {
         if (this._port == null) {
            throw new IOException("Port not open");
         }

         if (!this._serviceRecordAdded) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               this._serviceRecord.validate();
               this._port
                  .addSDPRecord(this._serviceRecord.getAttributeIDs(), this._serviceRecord.getAttributeValues(), this._serviceRecord.getDeviceServiceClasses());
               this._serviceRecordAdded = true;
               var10 = false;
            } finally {
               if (var10) {
                  throw new ServiceRegistrationException();
               }
            }
         }

         while (this._port != null) {
            if (this._connection != null && this._connection.isConnected() && !this._connectionInUse) {
               this._connectionInUse = true;
               return this._connection;
            }

            try {
               this._semaphore.wait();
            } finally {
               continue;
            }
         }

         throw new InterruptedIOException();
      }
   }

   @Override
   public int getPSM() {
      throw new IllegalArgumentException();
   }

   @Override
   public int getRFCOMMChannel() {
      if (this._port == null) {
         throw new IllegalArgumentException();
      } else {
         return this._port.getRFCOMMChannel();
      }
   }

   @Override
   public boolean isConnected() {
      synchronized (this._semaphore) {
         return this._connection != null ? this._connection.isConnected() : false;
      }
   }

   @Override
   public void deviceConnected(boolean success) {
      synchronized (this._semaphore) {
         if (success) {
            this._connection = new BluetoothStreamConnection(this._semaphore, this._port);
            this._semaphore.notify();
         }
      }
   }

   @Override
   public void deviceDisconnected() {
      synchronized (this._semaphore) {
         if (this._connection != null) {
            this._connection.disconnected();
            this._connection = null;
            this._connectionInUse = false;
            this._semaphore.notify();
         }
      }
   }

   @Override
   public void dtrStateChange(boolean high) {
   }

   @Override
   public void dataReceived(int length) {
      synchronized (this._semaphore) {
         if (this._connection != null) {
            this._connection.dataReceived(length);
         }
      }
   }

   @Override
   public void dataSent() {
      synchronized (this._semaphore) {
         if (this._connection != null) {
            this._connection.dataSent();
         }
      }
   }

   @Override
   public int getServiceRecordHandle() {
      if (this._port == null) {
         throw new IllegalArgumentException();
      } else {
         return this._port.getSDPRecordHandle();
      }
   }

   @Override
   public LocalServiceRecord getServiceRecord() {
      if (this._port == null) {
         throw new IllegalArgumentException();
      } else {
         return this._serviceRecord;
      }
   }

   @Override
   public String getLocalAddress() {
      return BluetoothME.deviceAddressToString(BluetoothME.getLocalDeviceAddress(), false);
   }

   @Override
   public int getLocalPort() {
      return -1;
   }

   public BluetoothServerSocketConnection(BluetoothURL url) {
      String serviceName = url.getName();
      if (serviceName == null) {
         serviceName = "btspp";
      }

      this._serviceRecord = new LocalServiceRecord(this, url.getUUID(), serviceName, 1);
   }
}
