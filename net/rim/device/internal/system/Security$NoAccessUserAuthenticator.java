package net.rim.device.internal.system;

import net.rim.device.api.system.UserAuthenticationException;
import net.rim.device.api.system.UserAuthenticator;

public final class Security$NoAccessUserAuthenticator extends UserAuthenticator {
   @Override
   public final String getName() {
      return "No Access Authenticator";
   }

   @Override
   public final Object getInformation(long key, Object parameter, Object defaultValue) {
      return defaultValue;
   }

   @Override
   public final boolean authenticate(String password) {
      return false;
   }

   @Override
   public final boolean initialize(String password) {
      throw new UserAuthenticationException();
   }

   @Override
   public final boolean isInitialized() {
      return true;
   }

   @Override
   public final int getMaxAuthenticationAttempts() {
      return 1;
   }

   @Override
   public final int getRemainingAuthenticationAttempts() {
      return 1;
   }

   @Override
   public final boolean setStateData(byte[] initializationData) {
      return true;
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   public final byte[] getStateData() {
      return null;
   }

   @Override
   public final boolean isInitializationPossible() {
      return true;
   }

   @Override
   public final boolean isReadyForInitialization() {
      return true;
   }

   @Override
   public final boolean isConfigured() {
      return true;
   }

   @Override
   public final void configure() {
   }
}
