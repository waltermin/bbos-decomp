package net.rim.device.cldc.io.btgoep;

import javax.bluetooth.ServiceRegistrationException;
import javax.microedition.io.Connection;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.apps.internal.bluetooth.LocalServiceRecord;
import net.rim.device.cldc.io.btspp.BluetoothServerConnection;
import net.rim.device.cldc.io.btspp.BluetoothStreamConnection;
import net.rim.device.cldc.io.btspp.BluetoothURL;

public final class BluetoothGOEPServerConnection extends OBEXServerSession implements BluetoothServerConnection, SessionNotifier, BluetoothSerialPortListener {
   private Object _semaphore = new Object();
   private BluetoothSerialPort _port = (BluetoothSerialPort)(new Object(3, 3, 0, 8192, 8192, this));
   private LocalServiceRecord _serviceRecord;
   private boolean _serviceRecordAdded;
   private BluetoothStreamConnection _connection;
   private boolean _connectionInUse;
   private static final String PORT_NOT_OPEN;
   private static final boolean DEBUG;

   public BluetoothGOEPServerConnection(BluetoothURL url) {
      String serviceName = url.getName();
      if (serviceName == null) {
         serviceName = "btgoep";
      }

      this._serviceRecord = new LocalServiceRecord(this, url.getUUID(), serviceName, 2);
   }

   @Override
   public final LocalServiceRecord getServiceRecord() {
      if (this._port == null) {
         throw new Object();
      } else {
         return this._serviceRecord;
      }
   }

   @Override
   public final int getServiceRecordHandle() {
      if (this._port == null) {
         throw new Object();
      } else {
         return this._port.getSDPRecordHandle();
      }
   }

   @Override
   public final int getPSM() {
      throw new Object();
   }

   @Override
   public final int getRFCOMMChannel() {
      if (this._port == null) {
         throw new Object();
      } else {
         return this._port.getRFCOMMChannel();
      }
   }

   @Override
   public final boolean isConnected() {
      synchronized (this._semaphore) {
         return this._connection != null ? this._connection.isConnected() : false;
      }
   }

   @Override
   public final void deviceConnected(boolean success) {
      synchronized (this._semaphore) {
         if (success) {
            this._connection = new BluetoothStreamConnection(this._semaphore, this._port);
            this._semaphore.notify();
         }
      }
   }

   @Override
   public final void deviceDisconnected() {
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
   public final void dtrStateChange(boolean high) {
   }

   @Override
   public final void dataReceived(int length) {
      synchronized (this._semaphore) {
         if (this._connection != null) {
            this._connection.dataReceived(length);
         }
      }
   }

   @Override
   public final void dataSent() {
      synchronized (this._semaphore) {
         if (this._connection != null) {
            this._connection.dataSent();
         }
      }
   }

   @Override
   public final Connection acceptAndOpen(ServerRequestHandler handler) {
      return this.acceptAndOpen(handler, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Connection acceptAndOpen(ServerRequestHandler handler, Authenticator auth) {
      if (handler == null) {
         throw new Object();
      }

      synchronized (this._semaphore) {
         if (this._port == null) {
            throw new Object("Port not open");
         }

         if (!this._serviceRecordAdded) {
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               this._serviceRecord.validate();
               this._port
                  .addSDPRecord(
                     this._serviceRecord.getAttributeIDs(), (byte[][])this._serviceRecord.getAttributeValues(), this._serviceRecord.getDeviceServiceClasses()
                  );
               this._serviceRecordAdded = true;
               var12 = false;
            } finally {
               if (var12) {
                  throw new ServiceRegistrationException();
               }
            }
         }

         while (this._port != null) {
            if (this._connection != null && this._connection.isConnected() && !this._connectionInUse) {
               this.init(handler, this._connection.openInputStream(), this._connection.openOutputStream(), auth);
               ((Thread)(new Object(this))).start();
               this._connectionInUse = true;
               return this._connection;
            }

            try {
               this._semaphore.wait();
            } finally {
               continue;
            }
         }

         throw new Object();
      }
   }

   @Override
   public final void close() {
      synchronized (this._semaphore) {
         if (this._port != null) {
            if (this._connection != null) {
               this._connection.close();
               this._connection.disconnected();
            }

            this._port.close();
            this._port = null;
         }

         super._disconnected = true;
         this._semaphore.notifyAll();
      }
   }
}
