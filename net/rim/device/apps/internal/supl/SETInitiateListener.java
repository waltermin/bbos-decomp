package net.rim.device.apps.internal.supl;

import net.rim.device.api.gps.LCSListener;

public final class SETInitiateListener implements LCSListener {
   private static SuplSession _activeSession = null;

   @Override
   public final void reqAssistDataEvent() {
      try {
         new SuplSessionManager();
      } finally {
         return;
      }
   }

   @Override
   public final void RRLPPayloadIndicationEvent(int sessionID, int length) {
   }

   @Override
   public final void notificationRequest(int type, int length) {
   }

   @Override
   public final void verificationTimerExpiry() {
   }
}
