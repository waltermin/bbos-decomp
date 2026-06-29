package net.rim.device.cldc.io.sync;

import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.vm.WeakReference;

public final class SyncConnection implements Connection {
   private Transport _transport;
   private long _sid;
   private boolean _closed;
   private WeakReference _listener;

   @Override
   public final void close() {
      if (!this._closed) {
         this._closed = true;
         this._transport.deRegister(this);
         this.notifyListener(16, null, 0);
         this.reset();
      }
   }

   public final long getSid() {
      return this._sid;
   }

   public final boolean isSerialConnection() {
      return this._transport.isSerialTransportEnabledFor(this._sid);
   }

   public final int send(SyncDatagramBase aSyncDatagram, boolean checkInSyncDatagram) throws IOException {
      if (this._closed) {
         throw new IOException();
      }

      long sid = aSyncDatagram.getSid();
      if (sid == -1) {
         aSyncDatagram.setSid(this._sid);
      }

      String userId = aSyncDatagram.getUserId();
      if (userId == null) {
         ServiceRecord sr = ServiceBook.getSB().getRecordByCidAndSid("SYNC", this._sid);
         if (sr == null) {
            throw new IOException("Attempt to send data for a service that has no service record.");
         }

         userId = String.valueOf(sr.getUserId());
         aSyncDatagram.setUserId(userId);
      }

      return this._transport.send(aSyncDatagram, checkInSyncDatagram);
   }

   public final void setListener(SyncConnectionListener aListener) {
      if (aListener != null) {
         this._listener = new WeakReference(aListener);
      } else {
         this._listener = null;
      }
   }

   final void notifyListener(int anEventId, Object anObject, int aTransactionId) {
      if (!this._closed) {
         if (this._listener != null) {
            SyncConnectionListener xSyncConnectionListener = (SyncConnectionListener)this._listener.get();
            if (xSyncConnectionListener != null) {
               try {
                  xSyncConnectionListener.onSyncConnectionEvent(anEventId, anObject, aTransactionId);
                  return;
               } finally {
                  return;
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   SyncConnection(String sidString, DatagramTransportBase aTransport) throws IOException {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._sid = Long.parseLong(sidString);
         var5 = false;
      } finally {
         if (var5) {
            throw new IOException();
         }
      }

      this._transport = (Transport)aTransport;
      this._transport.register(this);
   }

   private final void reset() {
      this._transport = null;
      this._listener = null;
      this._sid = -1;
   }
}
