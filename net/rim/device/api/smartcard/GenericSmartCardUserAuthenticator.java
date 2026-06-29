package net.rim.device.api.smartcard;

public class GenericSmartCardUserAuthenticator extends SmartCardUserAuthenticator {
   public GenericSmartCardUserAuthenticator() {
   }

   public GenericSmartCardUserAuthenticator(SmartCard smartCard) {
      super(smartCard, null);
   }

   @Override
   public String getName() {
      return ((StringBuffer)(new Object("Generic Smart Card Authenticator: "))).append(super.getName()).toString();
   }
}
