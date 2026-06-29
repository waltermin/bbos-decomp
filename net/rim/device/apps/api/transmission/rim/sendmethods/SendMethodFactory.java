package net.rim.device.apps.api.transmission.rim.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;

public interface SendMethodFactory {
   int PRIORITY_NORMAL_EMAIL = 0;
   int PRIORITY_SECURE_EMAIL = 1;

   SendMethod[] create(RIMModel var1, ServiceRecord var2, Object var3);

   long getEncodingUID();

   int getPriority();
}
