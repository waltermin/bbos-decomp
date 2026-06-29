package net.rim.device.api.smartcard;

import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.system.Security;

public class GenericSmartCardUserAuthenticatorFacadeImpl extends GenericSmartCardUserAuthenticatorFacade {
   @Override
   protected UserAuthenticator getSmartCardUserAuthenticatorImpl(SmartCard smartCard) {
      return new GenericSmartCardUserAuthenticator(smartCard);
   }

   @Override
   protected boolean isSmartCardAuthenticatorInstalledImpl() {
      Security security = Security.getInstance();
      UserAuthenticator userAuthenticator = security.getUserAuthenticator();
      return userAuthenticator instanceof SmartCardUserAuthenticator;
   }
}
