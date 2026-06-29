package net.rim.device.cldc.io.btl2cap;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.ServiceRegistrationException;
import net.rim.device.apps.internal.bluetooth.LocalServiceRecord;
import net.rim.device.cldc.io.btspp.BluetoothServerConnection;
import net.rim.device.cldc.io.btspp.BluetoothURL;
import net.rim.device.internal.bluetooth.BluetoothSDP;

public class L2CAPConnectionNotifierImpl implements L2CAPConnectionNotifier, BluetoothServerConnection {
   private Object _semaphore;
   private LocalServiceRecord _serviceRecord;
   private int _serviceRecordHandle = -1;
   private L2CAPConnectionImpl _connection;
   private boolean _closed;
   private boolean _connectionInUse;
   private static final String PORT_NOT_OPEN = "Port not open";
   private static final boolean DEBUG = false;

   @Override
   public void close() {
      synchronized (this._semaphore) {
         this._semaphore.notify();
         this._closed = true;
      }

      this._connection.cleanup(true);
      if (this._serviceRecordHandle != -1) {
         BluetoothSDP.removeRecord(this._serviceRecordHandle);
         this._serviceRecordHandle = -1;
      }
   }

   void connectionClosed() {
      synchronized (this._semaphore) {
         this._connectionInUse = false;
      }
   }

   void incomingConnection() {
      synchronized (this._semaphore) {
         this._semaphore.notify();
      }
   }

   @Override
   public int getServiceRecordHandle() {
      if (this._closed) {
         throw new Object();
      } else {
         return this._serviceRecordHandle;
      }
   }

   @Override
   public int getPSM() {
      if (this._closed) {
         throw new Object();
      } else {
         return this._connection.getPSM();
      }
   }

   @Override
   public int getRFCOMMChannel() {
      throw new Object();
   }

   @Override
   public boolean isConnected() {
      return this._connection.isConnected();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public L2CAPConnection acceptAndOpen() {
      synchronized (this._semaphore) {
         if (this._closed) {
            throw new Object("Connection closed");
         }

         if (this._serviceRecordHandle == -1) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               this._serviceRecord.validate();
               int ex = 11;
               this._serviceRecordHandle = BluetoothSDP.addRecord(
                  this._connection.getPSMHandle(),
                  true,
                  this._serviceRecord.getAttributeIDs(),
                  this._serviceRecord.getAttributeValues(),
                  this._serviceRecord.getDeviceServiceClasses(),
                  1,
                  this.getPSM(),
                  ex
               );
               if (this._serviceRecordHandle == -1) {
                  throw new ServiceRegistrationException();
               }

               var10 = false;
            } finally {
               if (var10) {
                  throw new ServiceRegistrationException();
               }
            }
         }

         if (this._connectionInUse) {
            throw new BluetoothConnectionException(3);
         }

         if (!this._connection.isConnected()) {
            label78:
            try {
               this._semaphore.wait();
            } finally {
               break label78;
            }
         }

         if (this._closed) {
            throw new Object("Connection closed");
         }

         this._connectionInUse = true;
         return this._connection;
      }
   }

   @Override
   public LocalServiceRecord getServiceRecord() {
      if (this._closed) {
         throw new Object();
      } else {
         return this._serviceRecord;
      }
   }

   public L2CAPConnectionNotifierImpl(BluetoothURL url) {
      this._semaphore = new Object();
      String serviceName = url.getName();
      if (serviceName == null) {
         serviceName = "btl2cap";
      }

      this._connection = new L2CAPConnectionImpl(this, url);
      this._serviceRecord = new LocalServiceRecord(this, url.getUUID(), serviceName, 0);
   }
}
