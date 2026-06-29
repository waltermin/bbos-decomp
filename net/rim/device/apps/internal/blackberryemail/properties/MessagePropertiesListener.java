package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public interface MessagePropertiesListener {
   void sendMethodSelected(SendMethod var1, Object var2);

   void recipientAdded(EmailHeaderModel var1, Object var2);

   void recipientRemoved(EmailHeaderModel var1, Object var2);
}
