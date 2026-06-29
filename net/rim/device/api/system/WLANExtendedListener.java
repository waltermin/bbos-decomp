package net.rim.device.api.system;

public interface WLANExtendedListener extends WLANListenerInternal {
   void wlanExtendedInfoChange();

   void wlanChallengeOccurred(int var1);

   void wlanRecordChangeOccurred(int var1);
}
