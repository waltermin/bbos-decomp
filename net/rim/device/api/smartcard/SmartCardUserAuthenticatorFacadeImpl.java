package net.rim.device.api.smartcard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.system.SmartCardUserAuthenticatorFacade;

public class SmartCardUserAuthenticatorFacadeImpl extends SmartCardUserAuthenticatorFacade {
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   @Override
   protected String checkITPolicyImpl(
      UserAuthenticator[] registeredAuthenticators, boolean forceSmartCardTwoFactorAuthentication, boolean lockOnSmartCardRemoval
   ) {
      if (forceSmartCardTwoFactorAuthentication) {
         boolean smartCardAuthenticatorFound = false;
         if (registeredAuthenticators != null) {
            for (int i = 0; i < registeredAuthenticators.length; i++) {
               try {
                  if (registeredAuthenticators[i] instanceof SmartCardUserAuthenticator && registeredAuthenticators[i].isInitializationPossible()) {
                     smartCardAuthenticatorFound = true;
                     break;
                  }
               } finally {
                  continue;
               }
            }
         }

         if (!smartCardAuthenticatorFound) {
            return _rb.getString(19);
         }

         if (lockOnSmartCardRemoval && SmartCardReaderFactory.getNumSmartCardReaders() <= 0) {
            return _rb.getString(20);
         }
      }

      return null;
   }
}
