package net.rim.blackberry.api.mail;

import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;

final class DefaultService$Helper implements TransmissionServiceListener {
   private RIMMessagingService _service;
   private boolean _receivedSB;
   private static final String TYPE = "BBMAILAPI";

   public DefaultService$Helper(RIMMessagingService s) {
      this._service = s;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void waitForService() {
      if (this._service.getOutgoingServiceRecord() == null) {
         this._service.addTransmissionServiceListener("BBMAILAPI", 100, this);
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            try {
               var7 = true;
               synchronized (this) {
                  if (!this._receivedSB && this._service.getOutgoingServiceRecord() == null) {
                     this.wait();
                  }

                  var7 = false;
               }
            } catch (Throwable var12) {
               throw new Object(e.toString());
            }
         } finally {
            if (var7) {
               this._service.removeTransmissionServiceListener("BBMAILAPI", this);
            }
         }

         this._service.removeTransmissionServiceListener("BBMAILAPI", this);
      }
   }

   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object contextObject) {
      return false;
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
      if (aTransmissionService == this._service && (statusInt & 1) != 0 && (statusInt & 2) != 0) {
         synchronized (this) {
            this._receivedSB = true;
            this.notify();
         }
      }
   }
}
