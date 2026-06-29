package net.rim.device.api.gps;

public interface LCSListener {
   void RRLPPayloadIndicationEvent(int var1, int var2);

   void reqAssistDataEvent();

   void notificationRequest(int var1, int var2);

   void verificationTimerExpiry();
}
