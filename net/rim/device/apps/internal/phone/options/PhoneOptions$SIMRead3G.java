package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;

class PhoneOptions$SIMRead3G implements SIMCardStatusListener, SIMCardEfTask {
   int _lineId;
   byte[] _buffer;

   public byte[] getBuffer() {
      return this._buffer;
   }

   @Override
   public synchronized void doWork(SIMCardEfHandler efh) {
      int result = efh.infoRequest(80);
      if (result != 0) {
         this._buffer = null;
      } else {
         int size = efh.getRecordLength();
         this._buffer = new byte[size];
         result = efh.readRequest(80, 1, this._lineId, this._buffer);
         if (result != 0) {
            this._buffer = null;
         }
      }
   }

   @Override
   public void cardReady() {
   }

   @Override
   public void cardInserted() {
   }

   @Override
   public void cardUpdated() {
   }

   @Override
   public void cardInvalid(int code, int subCode) {
   }

   @Override
   public void cardFault(int code) {
   }

   @Override
   public void smsEFFull() {
   }

   @Override
   public void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public void responseMarkSMSAsRead(int status, int packetId) {
   }

   public PhoneOptions$SIMRead3G(int lineId) {
      this._lineId = lineId;
      this._buffer = null;
   }
}
