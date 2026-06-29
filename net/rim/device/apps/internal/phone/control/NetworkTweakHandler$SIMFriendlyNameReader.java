package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;

class NetworkTweakHandler$SIMFriendlyNameReader implements SIMCardStatusListener, SIMCardEfTask {
   int[] _lineIds = PhoneUtilities.getAllLineIds();

   public NetworkTweakHandler$SIMFriendlyNameReader() {
      SIMCard.addListener(Application.getApplication(), this);
   }

   @Override
   public synchronized void doWork(SIMCardEfHandler efh) {
      int result = efh.infoRequest(24);
      if (result == 0) {
         int size = efh.getRecordLength();
         byte[] buffer = new byte[size];

         for (int i = this._lineIds.length - 1; i >= 0; i--) {
            result = efh.readRequest(24, 1, this._lineIds[i], buffer);
            if (result != 0) {
               return;
            }

            PhoneUtilities.setLineDescription(this._lineIds[i], SIMCard.decodeAlphaId(buffer, 0, size - 14), false);
         }
      }
   }

   @Override
   public void cardReady() {
      SIMCard.removeListener(Application.getApplication(), this);
      new SIMCardEfHandler().startTask(this, false);
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
}
