package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;

public final class ApplicationControl {
   public static final int LAPI_ALLOWED_FLAG;
   public static final int LAPI_PROMPT_FLAG;
   public static final int SCREEN_CAPTURE_ALLOWED_FLAG;
   public static final int SCREEN_CAPTURE_PROMPT_FLAG;

   private ApplicationControl() {
   }

   public static final boolean reloadModulePermissions() {
      return ApplicationControlImpl.reloadModulePermissions();
   }

   public static final boolean reloadDefaultModulePermissions() {
      return ApplicationControlImpl.reloadDefaultModulePermissions();
   }

   public static final void disableXmit() {
      ApplicationControlImpl.disableXmit();
   }

   public static final boolean isXmitDisabled() {
      return ApplicationControlImpl.isXmitDisabled();
   }

   public static final void scheduleDeviceReset(String reason) {
      ApplicationControlImpl.scheduleDeviceReset(reason);
   }

   public static final void scheduleDeviceReset(String reason, long timeBetweenResetWarnings) {
      ApplicationControlImpl.scheduleDeviceReset(reason, timeBetweenResetWarnings);
   }

   public static final void scheduleDeviceReset(String reason, int numResetWarnings, long timeBetweenResetWarnings) {
      ApplicationControlImpl.scheduleDeviceReset(reason, numResetWarnings, timeBetweenResetWarnings);
   }

   public static final void resetPrompts(int moduleHandle) {
      ApplicationControlImpl.resetPrompts(moduleHandle);
   }

   public static final void resetAllPrompts() {
      ApplicationControlImpl.resetAllPrompts();
   }

   public static final void resetAllPrompts(int allowFlag, int promptFlag) {
      ApplicationControlImpl.resetAllPrompts(allowFlag, promptFlag);
   }

   public static final int isInternalConnectionAllowed(byte[] hash, String domain) {
      return ApplicationControlImpl.isDomainAllowed(hash, domain, (byte)1);
   }

   public static final int isInternalConnectionAllowed(String domain, boolean checkProcess) {
      return isInternalConnectionAllowed(domain, checkProcess, null);
   }

   public static final int isInternalConnectionAllowed(String domain, boolean checkProcess, int[] additionalModules) {
      int rc;
      if ((rc = ApplicationControlImpl.isAllowedTernary(3, 4, checkProcess, additionalModules)) != 1) {
         return rc == 0 ? rc : 2;
      } else {
         return ApplicationControlImpl.isConnectionAllowed(domain, (byte)1, checkProcess, additionalModules);
      }
   }

   public static final int isInternalConnectionAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 3, 4);
   }

   public static final void assertInternalConnectionAllowed(String domain, boolean checkProcess) {
      if (isInternalConnectionAllowed(domain, checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[3]);
      }
   }

   public static final int isExternalConnectionAllowed(byte[] hash, String domain) {
      return ApplicationControlImpl.isDomainAllowed(hash, domain, (byte)2);
   }

   public static final int isExternalConnectionAllowed(String domain, boolean checkProcess) {
      return isExternalConnectionAllowed(domain, checkProcess, null);
   }

   public static final int isExternalConnectionAllowed(String domain, boolean checkProcess, int[] additionalModules) {
      int rc;
      if ((rc = ApplicationControlImpl.isAllowedTernary(5, 6, checkProcess, additionalModules)) != 1) {
         return rc == 0 ? rc : 2;
      } else {
         return ApplicationControlImpl.isConnectionAllowed(domain, (byte)2, checkProcess, additionalModules);
      }
   }

   public static final int isExternalConnectionAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 5, 6);
   }

   public static final void assertExternalConnectionAllowed(String domain, boolean checkProcess) {
      if (isExternalConnectionAllowed(domain, checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[5]);
      }
   }

   public static final boolean isBrowserFilterAllowed(byte[] hash, String domain) {
      return ApplicationControlImpl.isDomainAllowed(hash, domain, (byte)3) == 0;
   }

   public static final boolean isBrowserFilterAllowed(String domain, boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(12, checkProcess) ? true : ApplicationControlImpl.isConnectionAllowed(domain, (byte)3, checkProcess) == 0;
   }

   public static final boolean isBrowserFilterAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 12) == 0;
   }

   public static final void assertBrowserFilterAllowed(String domain, boolean checkProcess) {
      if (!isBrowserFilterAllowed(domain, checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[12]);
      }
   }

   public static final boolean isRequiredApp(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissionsIgnoringBypass(moduleHandle, 0) == 0;
   }

   public static final boolean isExcludedApp(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissionsIgnoringBypass(moduleHandle, 1) == 0;
   }

   public static final boolean isIPCAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(2, checkProcess);
   }

   public static final boolean isIPCAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 2) == 0;
   }

   public static final void assertIPCAllowed(boolean checkProcess) {
      ApplicationControlImpl.assertIPCAllowed(checkProcess);
   }

   public static final boolean isLocalConnectionAllowed(boolean checkProcess) {
      return isLocalConnectionAllowed(checkProcess, null);
   }

   public static final boolean isLocalConnectionAllowed(boolean checkProcess, int[] additionalModules) {
      return ApplicationControlImpl.isAllowed(7, checkProcess, additionalModules)
         && (ControlledAccess.verifyRRISignatures(checkProcess) || ITPolicy.getBoolean(24, 16, true));
   }

   public static final boolean isLocalConnectionAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 7) == 0;
   }

   public static final void assertLocalConnectionAllowed(boolean checkProcess) {
      if (!isLocalConnectionAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[7]);
      }
   }

   public static final int isPhoneAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(8, 9, checkProcess);
   }

   public static final int isPhoneAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 8, 9);
   }

   public static final void assertPhoneAllowed(boolean checkProcess) {
      if (isPhoneAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[8]);
      }
   }

   public static final void assertPhonePermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isPhoneAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 8, 9);
   }

   public static final boolean isPIMAllowed(boolean checkProcess) {
      return isPIMAllowed(checkProcess, null);
   }

   public static final boolean isPIMAllowed(boolean checkProcess, int[] additionalModules) {
      return ApplicationControlImpl.isAllowed(11, checkProcess, additionalModules);
   }

   public static final boolean isPIMAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 11) == 0;
   }

   public static final void assertPIMAllowed(boolean checkProcess) {
      if (!isPIMAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[11]);
      }
   }

   public static final boolean isEventInjectorAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(13, checkProcess);
   }

   public static final boolean isEventInjectorAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 13) == 0;
   }

   public static final void assertEventInjectorAllowed(boolean checkProcess) {
      if (!isEventInjectorAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[13]);
      }
   }

   public static final boolean isEmailAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(10, checkProcess);
   }

   public static final boolean isEmailAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 10) == 0;
   }

   public static final void assertEmailAllowed(boolean checkProcess) {
      if (!isEmailAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[10]);
      }
   }

   public static final boolean isBluetoothSerialProfileAllowed(boolean checkProcess) {
      return isBluetoothSerialProfileAllowed(checkProcess, null);
   }

   public static final boolean isBluetoothSerialProfileAllowed(boolean checkProcess, int[] additionalModules) {
      return ApplicationControlImpl.isAllowed(14, checkProcess, additionalModules);
   }

   public static final boolean isBluetoothSerialProfileAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 14) == 0;
   }

   public static final void assertBluetoothSerialProfileAllowed(boolean checkProcess) {
      if (!isBluetoothSerialProfileAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[14]);
      }
   }

   public static final boolean isHandheldKeyStoreAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(15, checkProcess);
   }

   public static final boolean isHandheldKeyStoreAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 15) == 0;
   }

   public static final void assertHandheldKeyStoreAllowed(boolean checkProcess) {
      if (!isHandheldKeyStoreAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[15]);
      }
   }

   public static final boolean isKeyStoreMediumSecurityAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(16, checkProcess);
   }

   public static final boolean isKeyStoreMediumSecurityAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 16) == 0;
   }

   public static final void assertKeyStoreMediumSecurityAllowed(boolean checkProcess) {
      if (!isKeyStoreMediumSecurityAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[16]);
      }
   }

   public static final int isLocationApiAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(17, 18, checkProcess);
   }

   public static final int isLocationApiAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 17, 18);
   }

   public static final void assertLocationApiAllowed(boolean checkProcess) {
      if (isLocationApiAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[17]);
      }
   }

   public static final void assertLocationApiPermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isLocationApiAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 17, 18);
   }

   public static final boolean isThemeDataAllowed(int moduleHandle) {
      return ApplicationControlImpl.isThemeDataAllowed(moduleHandle);
   }

   public static final void assertThemeDataAllowed(int moduleHandle) {
      if (!isThemeDataAllowed(moduleHandle)) {
         ApplicationControlImpl.logDenial(moduleHandle, 19);
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[19]);
      }
   }

   public static final boolean isAuthenticatorApiAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(20, checkProcess);
   }

   public static final boolean isAuthenticatorApiAllowed(int moduleHandle) {
      return ApplicationControlImpl.isAuthenticatorApiAllowed(moduleHandle);
   }

   public static final void assertAuthenticatorApiAllowed(boolean checkProcess) {
      if (!isAuthenticatorApiAllowed(checkProcess)) {
         throw new ControlledAccessException();
      }
   }

   public static final boolean isFileApiAllowed(boolean checkProcess) {
      return isFileApiAllowed(checkProcess, null);
   }

   public static final boolean isFileApiAllowed(boolean checkProcess, int[] additionalModules) {
      return ApplicationControlImpl.isAllowed(21, checkProcess, additionalModules);
   }

   public static final boolean isFileApiAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 21) == 0;
   }

   public static final void assertFileApiAllowed(boolean checkProcess) {
      if (!isFileApiAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[21]);
      }
   }

   public static final boolean isCMMApiAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowed(22, checkProcess);
   }

   public static final boolean isCMMApiAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 22) == 0;
   }

   public static final void assertCMMApiAllowed(boolean checkProcess) {
      if (!isCMMApiAllowed(checkProcess)) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[22]);
      }
   }

   public static final int isChangeDeviceSettingsAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(23, 24, checkProcess);
   }

   public static final int isChangeDeviceSettingsAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 23, 24);
   }

   public static final void assertChangeDeviceSettingsAllowed(boolean checkProcess) {
      if (isChangeDeviceSettingsAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[23]);
      }
   }

   public static final void assertChangeDeviceSettingsPermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isChangeDeviceSettingsAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 23, 24);
   }

   public static final int isScreenCaptureAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(25, 26, checkProcess);
   }

   public static final int isScreenCaptureAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 25, 26);
   }

   public static final void assertScreenCaptureAllowed(boolean checkProcess) {
      if (isScreenCaptureAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[25]);
      }
   }

   public static final void assertScreenCapturePermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isScreenCaptureAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 25, 26);
   }

   public static final int isWiFiAllowed(boolean checkProcess) {
      return isWiFiAllowed(checkProcess, null);
   }

   public static final int isWiFiAllowed(boolean checkProcess, int[] additionalModules) {
      return ApplicationControlImpl.isAllowedTernary(27, 28, checkProcess, additionalModules);
   }

   public static final int isWiFiAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 27, 28);
   }

   public static final void assertWiFiAllowed(boolean checkProcess) {
      if (isWiFiAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[27]);
      }
   }

   public static final void assertWiFiPermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isWiFiAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 27, 28);
   }

   public static final int isIdleTimerAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(29, 30, checkProcess);
   }

   public static final int isIdleTimerAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 29, 30);
   }

   public static final void assertIdleTimerAllowed(boolean checkProcess) {
      if (isIdleTimerAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[29]);
      }
   }

   public static final void assertIdleTimerPermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isIdleTimerAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 29, 30);
   }

   public static final int isMediaAllowed(boolean checkProcess) {
      return ApplicationControlImpl.isAllowedTernary(31, 32, checkProcess);
   }

   public static final int isMediaAllowed(int moduleHandle) {
      return ApplicationControlImpl.checkModulePermissions(moduleHandle, 31, 32);
   }

   public static final void assertMediaAllowed(boolean checkProcess) {
      if (isMediaAllowed(checkProcess) != 0) {
         throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[31]);
      }
   }

   public static final void assertMediaPermitted(boolean checkProcess, ResourceBundleFamily rb, int rbKey) {
      int perm = isMediaAllowed(checkProcess);
      ApplicationControlImpl.doPromptWork(perm, rb, rbKey, 31, 32);
   }

   public static final String getBrowserFilterConnectionDomains(byte[] hash) {
      return ApplicationControlImpl.getBrowserFilterConnectionDomains(hash);
   }

   public static final String getInternalConnectionDomains(byte[] hash) {
      return ApplicationControlImpl.getInternalConnectionDomains(hash);
   }

   public static final String getExternalConnectionDomains(byte[] hash) {
      return ApplicationControlImpl.getExternalConnectionDomains(hash);
   }

   public static final void removeModule(int moduleHandle) {
      ApplicationControlImpl.removeModule(moduleHandle);
   }

   public static final void doPromptWork(int ternary, ResourceBundleFamily rb, int rbKey, int allowFlag, int promptFlag) {
      ApplicationControlImpl.doPromptWork(ternary, rb, rbKey, allowFlag, promptFlag);
   }

   public static final ApplicationPermissions buildPermissions(long permissions, long nonDefaults) {
      return ApplicationControlImpl.buildPermissions(permissions, nonDefaults);
   }

   public static final long getRequestedPermissions(ApplicationPermissions requestedPermissions) {
      return ApplicationControlImpl.getRequestedPermissions(requestedPermissions);
   }

   public static final long getUserPermission(int moduleHandle, int flag) {
      return ApplicationControlImpl.getUserPermission(moduleHandle, flag);
   }

   public static final long getDefaultUserPermission(int flag) {
      return ApplicationControlImpl.getDefaultUserPermission(flag);
   }

   public static final boolean isUserSettingPresent(int moduleHandle) {
      return ApplicationControlImpl.isUserSettingPresent(moduleHandle);
   }

   public static final boolean isModuleSettingPresent(byte[] hash) {
      return ApplicationControlImpl.isModuleSettingPresent(hash);
   }

   public static final boolean isUserPermissionSet(int moduleHandle, int flag) {
      return ApplicationControlImpl.isUserPermissionSet(moduleHandle, flag);
   }

   public static final int getPolicyPermission(int flag, boolean checkProcess) {
      return ApplicationControlImpl.getPolicyPermission(flag, checkProcess);
   }

   public static final int getPolicyPermissionTernary(int allowedFlag, int promptFlag, boolean checkProcess) {
      return ApplicationControlImpl.getPolicyPermissionTernary(allowedFlag, promptFlag, checkProcess);
   }

   public static final int getPolicyPermission(String domain, int flag, boolean checkProcess) {
      return ApplicationControlImpl.getPolicyPermission(domain, flag, checkProcess);
   }

   public static final int getPolicyPermissionTernary(String domain, int allowFlag, int promptFlag, boolean checkProcess) {
      return ApplicationControlImpl.getPolicyPermissionTernary(domain, allowFlag, promptFlag, checkProcess);
   }

   public static final long getPermittedPermission(int moduleHandle, byte[] moduleHash, int flag) {
      return ApplicationControlImpl.getPermittedPermission(moduleHandle, moduleHash, flag);
   }

   public static final boolean setModuleUserPermission(byte[] moduleHash, int moduleHandle, ApplicationPermissions appPermissions) {
      return ApplicationControlImpl.setModuleUserPermission(moduleHash, moduleHandle, appPermissions);
   }

   public static final boolean isPromptResponseSaved(int promptFlag) {
      return ApplicationControlImpl.isPromptResponseSaved(promptFlag);
   }

   public static final boolean isFlagSetBoolean(byte[] hash, int allowFlag) {
      return ApplicationControlImpl.isFlagSetBoolean(hash, allowFlag);
   }

   public static final boolean isBitSet(long mask, int flag) {
      return ApplicationControlImpl.isBitSet(mask, flag);
   }

   public static final long getPermissionFlags(int allowFlag) {
      return ApplicationControlImpl.getPermissionFlags(allowFlag);
   }

   public static final long getPermissionFlags(int allowFlag, int promptFlag) {
      return ApplicationControlImpl.getPermissionFlags(allowFlag, promptFlag);
   }

   public static final void removeUserSetting(int handle) {
      ApplicationControlImpl.removeUserSetting(handle);
   }

   public static final void removeUserSettings(boolean removeDefaults) {
      ApplicationControlImpl.removeUserSettings(removeDefaults);
   }

   public static final boolean setRestrictiveDefaultPermission() {
      return ApplicationControlImpl.setRestrictiveDefaultPermission();
   }

   public static final boolean setDefaultPermission() {
      return ApplicationControlImpl.setDefaultPermission();
   }

   public static final boolean setPermissiveDefaultPermission() {
      return ApplicationControlImpl.setPermissiveDefaultPermission();
   }

   public static final boolean isSignedWithRRI(int moduleHandle) {
      return ApplicationControlImpl.isSignedWithRRI(moduleHandle);
   }

   public static final boolean differsFromUserDefaults(int moduleHandle) {
      return ApplicationControlImpl.differsFromUserDefaults(moduleHandle);
   }
}
