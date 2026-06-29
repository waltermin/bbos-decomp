package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.Security;

final class SecurityOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID = -133933004;
   private static final int TIMEOUT_TAG = 1;
   private static final int LOCK_WHEN_HOLSTERED_TAG = 2;
   private static final int ALLOW_OUTGOING_CALL_WHEN_LOCKED_TAG = 3;
   private static final int PASSWORD_ENABLED_TAG = 4;
   private static final int CONTENT_PROTECTION_ENABLED_TAG = 5;
   private static final int CONTENT_COMPRESSION_ENABLED_TAG = 6;
   private static final int DISABLE_NUMERIC_PASSWORD_INPUT_TAG = 7;
   private static final int IT_POLICY_SERVICE_COLOUR_TAG = 8;
   private static final int OTHER_SERVICE_COLOUR_TAG = 9;
   private static final int EXCLUDE_ADDRESS_BOOK_TAG = 10;
   private static final int MAX_PASSWORD_ATTEMPTS_TAG = 11;
   private static final int CONTENT_PROTECTION_STRENGTH_TAG = 12;
   private static final int PASSWORD_REQUIRED_FOR_APP_INSTALL_TAG = 13;

   SecurityOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -133933004;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      Security security = Security.getInstance();
      ConverterUtilities.convertInt(buffer, 1, security.getTimeout(), 4);
      if (security.getLockWhenHolstered()) {
         ConverterUtilities.writeEmptyField(buffer, 2);
      }

      if (security.getAllowOutgoingCallWhileLocked()) {
         ConverterUtilities.writeEmptyField(buffer, 3);
      }

      if (security.isPasswordEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 4);
      }

      if (PersistentContent.isEncryptionEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 5);
      }

      ConverterUtilities.convertInt(buffer, 12, PersistentContent.getEncryptionStrength(), 4);
      if (security.isAddressBookExcludedFromContentProtection()) {
         ConverterUtilities.writeEmptyField(buffer, 10);
      }

      if (PersistentContent.isCompressionEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 6);
      }

      if (!security.getSmartPasswordEntry()) {
         ConverterUtilities.writeEmptyField(buffer, 7);
      }

      ConverterUtilities.convertInt(buffer, 8, security.getSecurityITPolicyServiceColour(), 4);
      ConverterUtilities.convertInt(buffer, 9, security.getSecurityOtherServiceColour(), 4);
      ConverterUtilities.convertInt(buffer, 11, security.getMaxPasswordAttempts(), 4);
      if (security.getPasswordRequiredForAppInstall()) {
         ConverterUtilities.writeEmptyField(buffer, 13);
      }
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         boolean gottimeout = false;
         boolean lockWhenHolstered = false;
         boolean allowOutgoingCallWhenLocked = false;
         boolean enablePassword = false;
         boolean enableContentProtection = false;
         boolean enableContentCompression = false;
         boolean disableSmartPasswordEntry = false;
         boolean gotITPolicyServiceColour = false;
         boolean gotOtherServiceColour = false;
         boolean excludeAddressBook = false;
         boolean gotMaxPasswordAttempts = false;
         boolean gotContentProtectionStrength = false;
         boolean passwordRequiredForAppInstall = false;
         int timeout = 0;
         int itPolicyServiceColour = 0;
         int otherServiceColour = 0;
         int maxPasswordAttempts = -1;
         int contentProtectionStrength = -1;

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer);
            switch (tag) {
               case 1:
               default:
                  gottimeout = true;
                  timeout = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  lockWhenHolstered = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 3:
                  allowOutgoingCallWhenLocked = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 4:
                  enablePassword = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 5:
                  enableContentProtection = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 6:
                  enableContentCompression = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 7:
                  disableSmartPasswordEntry = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 8:
                  gotITPolicyServiceColour = true;
                  itPolicyServiceColour = ConverterUtilities.readInt(buffer);
                  break;
               case 9:
                  gotOtherServiceColour = true;
                  otherServiceColour = ConverterUtilities.readInt(buffer);
                  break;
               case 10:
                  excludeAddressBook = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 11:
                  gotMaxPasswordAttempts = true;
                  maxPasswordAttempts = ConverterUtilities.readInt(buffer);
                  break;
               case 12:
                  gotContentProtectionStrength = true;
                  contentProtectionStrength = ConverterUtilities.readInt(buffer);
                  break;
               case 13:
                  passwordRequiredForAppInstall = true;
                  ConverterUtilities.skipField(buffer);
               case 0:
                  ConverterUtilities.skipField(buffer);
            }
         }

         Security security = Security.getInstance();
         if (gottimeout) {
            security.setTimeout(timeout);
         }

         security.setPasswordRequiredForAppInstall(passwordRequiredForAppInstall);
         security.setLockWhenHolstered(lockWhenHolstered);
         security.setAllowOutgoingCallWhileLocked(allowOutgoingCallWhenLocked);
         PersistentContentInternal.setContentCompression(enableContentCompression);
         security.setSmartPasswordEntry(!disableSmartPasswordEntry);
         if (gotITPolicyServiceColour || gotOtherServiceColour) {
            security.setSecurityServiceColours(itPolicyServiceColour, otherServiceColour);
         }

         security.setExcludeAddressBookFromContentProtection(excludeAddressBook);
         if (gotMaxPasswordAttempts) {
            security.setMaxPasswordAttempts(maxPasswordAttempts);
         }

         boolean lockSystem = false;
         if (enableContentProtection) {
            enablePassword = true;
         }

         if (gotContentProtectionStrength) {
            security.setContentProtection(enableContentProtection, contentProtectionStrength);
         }

         if (!security.isPasswordEnabled() && enablePassword) {
            ApplicationRegistry.getApplicationRegistry().replace(5862813030521710644L, Boolean.TRUE);
            LockEventLogger.logLockEvent(1282634608);
            lockSystem = true;
         }

         if (!PersistentContent.isEncryptionEnabled() && enableContentProtection) {
            ApplicationRegistry.getApplicationRegistry().replace(-746144713976755387L, Boolean.TRUE);
            LockEventLogger.logLockEvent(1282634595);
            lockSystem = true;
         }

         if (lockSystem) {
            ApplicationManager.getApplicationManager().lockSystem(true);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 9206737719270818227L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         9206737719270818227L,
         -3455386809805045760L,
         -4394903006263251010L,
         -3455386809805045760L,
         8906172480279495146L,
         463674593572421888L,
         1246482708980695296L,
         -1080581586446909429L
      };
   }
}
