package net.rim.device.internal.system;

public interface SecurityCallHandler {
   boolean isEnabled();

   boolean emergencyCallSupported();

   boolean outgoingCallSupported();

   void makeEmergencyCall();

   void makeEmergencyCall(boolean var1);

   void placeCall();
}
