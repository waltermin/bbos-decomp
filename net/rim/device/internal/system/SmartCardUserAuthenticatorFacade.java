package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.i18n.CommonResource;

public class SmartCardUserAuthenticatorFacade {
   public static void checkITPolicy() {
      while (FIPSPolicy.isDevicePasswordRequired()) {
         boolean forceSmartCardTwoFactorAuthentication = ITPolicy.getBoolean(24, 2, false);
         boolean lockOnSmartCardRemoval = ITPolicy.getBoolean(24, 1, false);
         if (!forceSmartCardTwoFactorAuthentication && !lockOnSmartCardRemoval) {
            return;
         }

         SmartCardUserAuthenticatorFacade facade = null;

         try {
            facade = (SmartCardUserAuthenticatorFacade)Class.forName("net.rim.device.api.smartcard.SmartCardUserAuthenticatorFacadeImpl").newInstance();
         } catch (InstantiationException var5) {
         } catch (IllegalAccessException var6) {
         } catch (ClassNotFoundException var7) {
         }

         if (facade == null) {
            Dialog.alert(CommonResource.getString(10084));
         } else {
            UserAuthenticator[] authenticators = Security.getInstance().getRegisteredUserAuthenticators();
            String error = facade.checkITPolicyImpl(authenticators, forceSmartCardTwoFactorAuthentication, lockOnSmartCardRemoval);
            if (error == null) {
               return;
            }

            Dialog.alert(error);
         }
      }
   }

   protected String checkITPolicyImpl(UserAuthenticator[] _1, boolean _2, boolean _3) {
      throw null;
   }

   public static boolean isInitializationRequired() {
      boolean forceSmartCardTwoFactorAuthentication = ITPolicy.getBoolean(24, 2, false);
      UserAuthenticator authenticator = Security.getInstance().getUserAuthenticator();
      return FIPSPolicy.isDevicePasswordRequired()
         && forceSmartCardTwoFactorAuthentication
         && authenticator == null
         && Security.getInstance().isPasswordEnabled();
   }
}
