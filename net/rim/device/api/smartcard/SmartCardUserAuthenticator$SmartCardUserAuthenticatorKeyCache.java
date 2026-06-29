package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.RIMKeyStore;

public class SmartCardUserAuthenticator$SmartCardUserAuthenticatorKeyCache extends RIMKeyStore {
   private SmartCardUserAuthenticator$SmartCardUserAuthenticatorKeyCache() {
      super("Smart Card User Authenticator Key Cache");
   }

   @Override
   public KeyStoreTicket getTicket() {
      return null;
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return true;
   }

   SmartCardUserAuthenticator$SmartCardUserAuthenticatorKeyCache(SmartCardUserAuthenticator$1 x0) {
      this();
   }
}
