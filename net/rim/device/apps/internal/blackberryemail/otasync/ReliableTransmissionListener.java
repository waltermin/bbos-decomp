package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;

public interface ReliableTransmissionListener {
   void failedTransmission(ServiceRecord var1, Object var2, int var3);

   void successfulTransmission(ServiceRecord var1, Object var2);
}
