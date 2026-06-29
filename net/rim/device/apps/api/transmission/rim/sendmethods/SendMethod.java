package net.rim.device.apps.api.transmission.rim.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;

public interface SendMethod {
   int FLAG_WARN_USER;
   int FLAG_SECURE_METHOD;
   int FLAG_INCLUDE_SECURE_ENCODING_TYPE_IN_DESCRIPTION;
   int FLAG_OVERRIDING_DEFAULT;
   int FLAG_ALWAYS_DISPLAY;

   boolean send(RIMModel var1, Object var2);

   boolean isConfigured(RIMModel var1, Object var2);

   boolean isConfigurable(RIMModel var1, Object var2);

   ServiceRecord getServiceRecord();

   long getEncodingUID();

   int getEncodingAction();

   void setFlags(int var1);

   void clearFlags(int var1);

   int getFlags();
}
