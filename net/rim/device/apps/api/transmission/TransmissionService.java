package net.rim.device.apps.api.transmission;

import net.rim.device.internal.io.TrafficLogger;

public interface TransmissionService {
   int CANNOT_RECEIVE_OR_SEND = 0;
   int CAN_SEND = 1;
   int CAN_RECEIVE = 2;

   Object getContext();

   long getFactoryIdentifier();

   void addTransmissionServiceListener(String var1, int var2, TransmissionServiceListener var3);

   void removeTransmissionServiceListener(String var1, TransmissionServiceListener var2);

   void transmitObject(String var1, Object var2, TransmissionStatusListener var3, int var4, Object var5);

   void cancelTransmitObject(int var1, Object var2);

   void setDefaultTransmissionStatusListener(TransmissionStatusListener var1);

   int getStatus();

   void setTrafficLogger(TrafficLogger var1);
}
