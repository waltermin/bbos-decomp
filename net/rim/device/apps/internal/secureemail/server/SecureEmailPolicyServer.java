package net.rim.device.apps.internal.secureemail.server;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.RecipientData;

public interface SecureEmailPolicyServer {
   boolean providesPolicyForService(ServiceRecord var1);

   boolean isInitialized();

   boolean initialize(SecureEmailServerOperationListener var1);

   boolean isPolicyUpdateRequired();

   boolean updatePolicy(SecureEmailServerOperationListener var1);

   int[] getEncodingActions(EmailMessageModel var1, ServiceRecord var2, RecipientData[] var3, int var4);

   long getPreferredEncodingUID(EmailMessageModel var1, ServiceRecord var2, RecipientData[] var3, int var4);

   String getServerName();
}
