package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.internal.system.RadioInternal;

public final class SimCardStatusHandler implements SIMCardStatusListener {
   public final void VerifyNetworkSelectionMode() {
      int savedMode = RadioInternal.getNetworkSelectionMode();
      if ((1 << savedMode & RadioInternal.getAvailableNetworkSelectionModes()) == 0) {
         RadioInternal.setNetworkSelectionMode(0);
      }
   }

   @Override
   public final void cardUpdated() {
      this.VerifyNetworkSelectionMode();
   }

   @Override
   public final void cardReady() {
      this.VerifyNetworkSelectionMode();
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }
}
