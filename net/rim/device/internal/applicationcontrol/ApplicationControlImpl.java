package net.rim.device.internal.applicationcontrol;

import java.io.UnsupportedEncodingException;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.ForcedResetManager;
import net.rim.device.internal.ui.security.component.PermissionDialog;
import net.rim.vm.Array;
import net.rim.vm.DebugSupport;
import net.rim.vm.EventLog;
import net.rim.vm.Process;
import net.rim.vm.ThreadSpecificData;
import net.rim.vm.TraceBack;

final class ApplicationControlImpl {
   private static final long FLAG;
   private static final long GUID;
   private static final long PERM_KEY;
   private static final long USER_PERMS_KEY;
   private static final long STACK_HASHES_KEY;
   private static final int ALLOW;
   private static final int PROMPT;
   private static final int DENY;
   private static ApplicationRegistry _ar;
   private static int _processModuleHandle;
   private static byte[] _processModuleHash;
   private static UserPermissions _userPermissions;
   private static StackTracePermissions _responseStackPermissions;
   private static SystemPermissions _permissions;
   private static IntHashtable _transactions;
   private static boolean _isDesktopVM = false;

   static final void removeModule(int moduleHandle) {
      if (moduleHandle != 0) {
         synchronized (_permissions) {
            _permissions.invalidate();
            doSetModulePermissions(moduleHandle, 0);
            _userPermissions.removeSetting(moduleHandle);
         }
      }
   }

   static final boolean reloadModulePermissions() {
      long time = System.currentTimeMillis();
      boolean resetRequired = false;
      synchronized (_permissions) {
         _permissions.init();
         _userPermissions.load(_permissions.getDefaultPermissions());
         resetRequired = applyDefaultPermissions();
      }

      time = System.currentTimeMillis() - time;
      System.out.println("APP CONTROL DATABASE GENERATION TIME: " + time);
      return resetRequired;
   }

   static final boolean reloadDefaultModulePermissions() {
      long time = System.currentTimeMillis();
      boolean resetRequired = false;
      synchronized (_permissions) {
         _permissions.init();
         resetRequired = applyDefaultPermissions();
      }

      time = System.currentTimeMillis() - time;
      System.out.println("APP CONTROL RELOAD DEFAULTS TIME: " + time);
      return resetRequired;
   }

   private static final boolean applyDefaultPermissions() {
      long time = System.currentTimeMillis();
      boolean resetRequired = false;
      synchronized (_permissions) {
         UserSetting userDefaults = _userPermissions.getDefaultSetting();
         UserSetting backedUpDefaults = _userPermissions.getBackedUpDefaultSetting();
         long oldPermissions = userDefaults.getPermissions();
         long combinedPermissions = combinePermissions(_permissions.getPermittedPermissions(), oldPermissions);
         if (oldPermissions != combinedPermissions) {
            _userPermissions.setPermissions(userDefaults, combinedPermissions);
         }

         if (isMoreRestrictive(oldPermissions, userDefaults.getPermissions())) {
            if (backedUpDefaults == null) {
               backedUpDefaults = new UserSetting(ApplicationControlConstants.FILLED_HASH, oldPermissions);
               _userPermissions.putBackedUpDefaultSetting(backedUpDefaults);
            }

            System.out.println("AC-More-Restr: Default: " + oldPermissions + "|" + userDefaults.getPermissions());
            String text = "AC-MRD: " + oldPermissions + "|" + userDefaults.getPermissions();
            EventLog.logEvent(-4492041993596154793L, System.currentTimeMillis(), (byte)5, text.getBytes());
            resetRequired = true;
         } else if (backedUpDefaults != null) {
            _userPermissions.setPermissions(userDefaults, combinePermissions(_permissions.getPermittedPermissions(), backedUpDefaults.getPermissions()));
            if (isMoreRestrictive(oldPermissions, userDefaults.getPermissions())) {
               System.out.println("AC-More-Restr: bak Default: " + oldPermissions + "|" + userDefaults.getPermissions());
               String text = "AC-MRBD: " + oldPermissions + "|" + userDefaults.getPermissions();
               EventLog.logEvent(-4492041993596154793L, System.currentTimeMillis(), (byte)5, text.getBytes());
               resetRequired = true;
            }
         }

         byte[] hash = new byte[20];

         for (int i = _permissions.getHandles().length - 1; i >= 0; i--) {
            int handle = _permissions.getHandles()[i];
            if (!CodeModuleManager.getModuleHash(handle, hash)) {
               throw new RuntimeException();
            }

            oldPermissions = doGetModulePermissions(handle);
            boolean policySettingPresent = isModuleSettingPresent(hash);
            long policyPermissions = getModulePolicyPermittedPermissions(handle, hash);
            UserSetting us = _userPermissions.getSetting(handle);
            long newPermissions;
            if (us == null) {
               newPermissions = policySettingPresent ? policyPermissions : combinePermissions(policyPermissions, userDefaults.getPermissions());
            } else {
               newPermissions = combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt());
            }

            doSetModulePermissions(handle, newPermissions);
            if (!resetRequired && oldPermissions != 0 && isMoreRestrictive(oldPermissions, newPermissions)) {
               System.out
                  .println(
                     "AC-More-Restr: " + CodeModuleManager.getModuleName(handle) + ": " + Long.toString(oldPermissions) + "|" + Long.toString(newPermissions)
                  );
               String text = "AC-MRM: " + CodeModuleManager.getModuleName(handle) + "|" + oldPermissions + "|" + userDefaults.getPermissions();
               EventLog.logEvent(-4492041993596154793L, System.currentTimeMillis(), (byte)5, text.getBytes());
               resetRequired = true;
            }
         }
      }

      time = System.currentTimeMillis() - time;
      System.out.println("APP CONTROL DEFAULTS APPLY TIME: " + time);
      return resetRequired;
   }

   static final void checkInitialization() {
      synchronized (_permissions) {
         for (int crc = CodeStore.getModuleHandles(null); !_permissions.areValid(crc); crc = CodeStore.getModuleHandles(null)) {
            System.out.println("AC: new CRC = " + crc);
            int[] oldHandles = _permissions.loadHandles();
            int[] newHandles = _permissions.getHandles();

            for (int i = 0; i < oldHandles.length; i++) {
               if (Arrays.getIndex(newHandles, oldHandles[i]) == -1) {
                  removeModule(oldHandles[i]);
               }
            }

            byte[] hash = new byte[20];
            int newHandleIndex = 0;
            int newHandle = 0;

            while (newHandleIndex < newHandles.length) {
               newHandle = newHandles[newHandleIndex];
               if (Arrays.getIndex(oldHandles, newHandle) == -1) {
                  if (!addModule(newHandle)) {
                     Arrays.removeAt(newHandles, newHandleIndex);
                  } else {
                     newHandleIndex++;
                  }
               } else {
                  UserSetting us = _userPermissions.getSetting(newHandle);
                  if (us != null) {
                     if (CodeModuleManager.getModuleHash(newHandle, hash)) {
                        if (!us.hashEquals(hash)) {
                           _userPermissions.removeSetting(newHandle);
                           long policy = getModulePolicyPermittedPermissions(newHandle, hash);
                           doSetModulePermissions(newHandle, combinePermissions(policy, _userPermissions.getDefaultSetting().getPermissions()));
                        }
                     } else {
                        _userPermissions.removeSetting(newHandle);
                     }
                  }

                  newHandleIndex++;
               }
            }
         }
      }
   }

   static final boolean addModule(int moduleHandle) {
      if (moduleHandle == 0) {
         return false;
      }

      byte[] hash;
      try {
         hash = CodeModuleManager.getModuleHash(moduleHandle);
      } catch (IllegalArgumentException iae) {
         return false;
      }

      if (hash == null) {
         return false;
      }

      synchronized (_permissions) {
         UserSetting us = _userPermissions.getSetting(hash);
         boolean policySettingPresent = isModuleSettingPresent(hash);
         long policyPermissions = getModulePolicyPermittedPermissions(moduleHandle, hash);
         long perms;
         if (us == null) {
            perms = policySettingPresent ? policyPermissions : combinePermissions(policyPermissions, _userPermissions.getDefaultSetting().getPermissions());
         } else {
            perms = combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt());
            _userPermissions.putSetting(moduleHandle, us, false);
            us.setPermissions(perms);
         }

         return doSetModulePermissions(moduleHandle, perms);
      }
   }

   static final void addTransactionModule(int newModuleHandle, int transactionHandle) {
      if (_transactions == null) {
         _transactions = new IntHashtable();
      }

      IntVector handles = (IntVector)_transactions.get(transactionHandle);
      if (handles == null) {
         handles = new IntVector();
      }

      handles.addElement(newModuleHandle);
      _transactions.put(transactionHandle, handles);
   }

   static final void endTransactionAddModules(int transactionHandle) {
      if (_transactions != null) {
         IntVector handles = (IntVector)_transactions.get(transactionHandle);
         if (handles == null) {
            throw new IllegalArgumentException();
         }

         handles.trimToSize();
         int[] array = handles.getArray();

         for (int i = array.length - 1; i >= 0; i--) {
            addModule(array[i]);
         }
      }
   }

   static final void removeUserSetting(int handle) {
      UserSetting us = _userPermissions.getSetting(handle);
      if (handle != 0 && us != null) {
         if (isModuleSettingPresent(us.getHash())) {
            doSetModulePermissions(handle, getModulePermissions(handle, us.getHash()));
         } else {
            doSetModulePermissions(handle, _userPermissions.getDefaultSetting().getPermissions());
         }

         _userPermissions.removeSetting(handle, us);
      }
   }

   static final void removeUserSettings(boolean removeDefaults) {
      UserSetting defaultSetting = _userPermissions.getDefaultSetting();
      int[] handles = _userPermissions.getLoadedHandles();

      for (int i = 0; i < handles.length; i++) {
         int currHandle = handles[i];
         byte[] hash = _userPermissions.getSetting(currHandle).getHash();
         if (isModuleSettingPresent(hash)) {
            long policyPermissions = getModulePermissions(currHandle, hash);
            doSetModulePermissions(currHandle, policyPermissions);
         } else {
            doSetModulePermissions(currHandle, removeDefaults ? _permissions.getDefaultPermissions() : defaultSetting.getPermissions());
         }
      }

      _userPermissions.clear(removeDefaults ? _permissions.getDefaultPermissions() : defaultSetting.getPermissions());
   }

   static final int getPolicyPermission(int flag, boolean checkProcess) {
      return getStackPolicyPermission(flag, checkProcess);
   }

   static final int getPolicyPermissionTernary(int allowedFlag, int promptFlag, boolean checkProcess) {
      int allowPerm = getStackPolicyPermission(allowedFlag, checkProcess);
      if (allowPerm == 0) {
         return isStackPromptPolicyPermission(promptFlag, checkProcess) ? 2 : 0;
      } else {
         return 1;
      }
   }

   static final int getPolicyPermission(String domain, int flag, boolean checkProcess) {
      if (getPolicyPermission(flag, true) == 0) {
         return 0;
      }

      switch (flag) {
         case 12:
            byte domainTag = 3;
            return isConnectionAllowed(domain, domainTag, true);
         default:
            throw new IllegalArgumentException();
      }
   }

   static final int getPolicyPermissionTernary(String domain, int allowFlag, int promptFlag, boolean checkProcess) {
      int perm = getPolicyPermissionTernary(allowFlag, promptFlag, checkProcess);
      if (perm != 1) {
         return perm == 0 ? perm : 2;
      }

      byte domainTag;
      switch (allowFlag) {
         case 3:
            domainTag = 1;
            break;
         case 5:
            domainTag = 2;
            break;
         default:
            throw new IllegalArgumentException();
      }

      return isConnectionAllowed(domain, domainTag, true);
   }

   private static final long getPolicyPermissionIgnoringDefaults(int moduleHandle, byte[] moduleHash, int flag) {
      boolean policySettingPresent = isModuleSettingPresent(moduleHash);
      return policySettingPresent ? getModulePermission(moduleHandle, moduleHash, flag) : Long.MIN_VALUE >>> flag & _permissions.getPermittedPermissions();
   }

   private static final int getStackPolicyPermission(int flag, boolean checkProcess) {
      if (checkProcess && _processModuleHandle != 0 && !isSignedWithRRI(_processModuleHandle) && isSettingPresent(_processModuleHash)) {
         long perm = getPolicyPermissionIgnoringDefaults(_processModuleHandle, _processModuleHash, flag);
         if (perm == 0) {
            return 1;
         }
      }

      int[] handles = TraceBack.getCallingModules();
      byte[] hash = new byte[20];

      for (int i = handles.length - 1; i >= 0; i--) {
         int handle = handles[i];
         if (!isSignedWithRRI(handle)) {
            if (!CodeModuleManager.getModuleHash(handle, hash)) {
               return 1;
            }

            long perm = getPolicyPermissionIgnoringDefaults(handle, hash, flag);
            if (perm == 0) {
               return 1;
            }
         }
      }

      return 0;
   }

   private static final boolean isStackPromptPolicyPermission(int flag, boolean checkProcess) {
      if (checkProcess && _processModuleHandle != 0 && !isSignedWithRRI(_processModuleHandle) && isSettingPresent(_processModuleHash)) {
         long perm = getPolicyPermissionIgnoringDefaults(_processModuleHandle, _processModuleHash, flag);
         if (perm != 0) {
            return true;
         }
      }

      int[] handles = TraceBack.getCallingModules();
      byte[] hash = new byte[20];

      for (int i = handles.length - 1; i >= 0; i--) {
         int handle = handles[i];
         if (!isSignedWithRRI(handle)) {
            if (!CodeModuleManager.getModuleHash(handle, hash)) {
               return true;
            }

            long perm = getPolicyPermissionIgnoringDefaults(handle, hash, flag);
            if (perm != 0) {
               return true;
            }
         }
      }

      return false;
   }

   static final long getPermittedPermission(int moduleHandle, byte[] moduleHash, int flag) {
      if (moduleHandle == 0 || !isModuleSettingPresent(moduleHash)) {
         return _permissions.getPermittedPermissions() & Long.MIN_VALUE >>> flag;
      } else {
         return isPermitted(moduleHash, flag) ? Long.MIN_VALUE >>> flag : 0;
      }
   }

   static final long getUserPermission(int moduleHandle, int flag) {
      UserSetting us = _userPermissions.getSetting(moduleHandle);
      return getUserPermission(us, flag);
   }

   public static final long getDefaultUserPermission(int flag) {
      UserSetting us = _userPermissions.getDefaultSetting();
      return getUserPermission(us, flag);
   }

   private static final long getUserPermission(UserSetting us, int flag) {
      if (us == null) {
         us = _userPermissions.getDefaultSetting();
         if (us == null) {
            return _permissions.getDefaultPermissions() & Long.MIN_VALUE >>> flag;
         }
      }

      return us.getPermissions() & Long.MIN_VALUE >>> flag;
   }

   static final boolean isUserSettingPresent(int moduleHandle) {
      return _userPermissions.getSetting(moduleHandle) != null;
   }

   static final boolean isUserPermissionSet(int moduleHandle, int flag) {
      UserSetting us = _userPermissions.getSetting(moduleHandle);
      return isUserPermissionSet(us, flag);
   }

   private static final boolean isUserPermissionSet(UserSetting userSetting, int flag) {
      return userSetting == null ? false : (userSetting.getIsSet() & Long.MIN_VALUE >>> flag) != 0;
   }

   static final boolean differsFromUserDefaults(int moduleHandle) {
      UserSetting us = _userPermissions.getSetting(moduleHandle);
      if (us != null) {
         UserSetting defaults = _userPermissions.getDefaultSetting();
         return (defaults.getPermissions() & us.getIsSet()) != (us.getPermissions() & us.getIsSet());
      } else {
         return false;
      }
   }

   static final boolean isSignedWithRRI(int moduleHandle) {
      synchronized (_permissions) {
         return (doGetModulePermissions(moduleHandle) & 1) != 0;
      }
   }

   static final boolean isAllowed(int flag, boolean checkProcess) {
      return isAllowed(flag, checkProcess, null);
   }

   static final boolean isAllowed(int flag, boolean checkProcess, int[] additionalModules) {
      checkInitialization();
      boolean traceMatched = false;
      int[] trace = TraceBack.getCallingModules();
      Thread thread = Thread.currentThread();
      ApplicationControlImpl$CachedPermissions cp = (ApplicationControlImpl$CachedPermissions)ThreadSpecificData.get(thread, 1);
      if (cp != null && cp._check == _permissions.getCheck() && cp.equals(trace)) {
         if ((cp._perms & Long.MIN_VALUE >>> flag) != 0) {
            return true;
         }

         traceMatched = true;
      }

      boolean rc = doGetCallingModulesPermission(
            Long.MIN_VALUE >>> flag, 0, 1, _processModuleHandle == 0 ? false : checkProcess, _processModuleHandle, additionalModules
         )
         == 2;
      if (!rc) {
         logDenial(_processModuleHandle, flag);
      }

      if (cp == null || !traceMatched) {
         cp = new ApplicationControlImpl$CachedPermissions(trace);
         ThreadSpecificData.set(thread, 1, cp);
      }

      long cache = rc ? Long.MIN_VALUE >>> flag | cp._perms : (Long.MIN_VALUE >>> flag ^ -1) & cp._perms;
      cp.setCachedPerms(cache, _permissions.getCheck());
      return rc;
   }

   static final int isAllowedTernary(int allowFlag, int promptFlag, boolean checkProcess) {
      return isAllowedTernary(allowFlag, promptFlag, checkProcess, null);
   }

   static final int isAllowedTernary(int allowFlag, int promptFlag, boolean checkProcess, int[] additionalModules) {
      checkInitialization();
      int rc = doGetCallingModulesPermission(
         Long.MIN_VALUE >>> allowFlag,
         Long.MIN_VALUE >>> promptFlag,
         1,
         _processModuleHandle == 0 ? false : checkProcess,
         _processModuleHandle,
         additionalModules
      );
      if (rc != 2) {
         if (rc == 1) {
            return 2;
         }

         logDenial(_processModuleHandle, allowFlag);
         return 1;
      } else {
         return 0;
      }
   }

   static final int permissionMaskToTriState(long permissions, int allowFlag, int promptFlag) {
      if ((permissions & Long.MIN_VALUE >>> allowFlag) != 0) {
         return (permissions & Long.MIN_VALUE >>> promptFlag) != 0 ? 2 : 0;
      } else {
         return 1;
      }
   }

   static final int isConnectionAllowed(String domain, byte domainTag, boolean checkProcess) {
      return isConnectionAllowed(domain, domainTag, checkProcess, null);
   }

   static final int isConnectionAllowed(String domain, byte domainTag, boolean checkProcess, int[] additionalModules) {
      boolean prompt = false;
      if (checkProcess && _processModuleHandle != 0 && !isSignedWithRRI(_processModuleHandle)) {
         int perm = 1;
         if (isSettingPresent(_processModuleHash)) {
            perm = isDomainAllowed(_processModuleHash, domain, domainTag);
         }

         if (perm == 1) {
            return 1;
         }

         prompt |= perm == 2;
      }

      int[] handles = TraceBack.getCallingModules();
      byte[] hash = new byte[20];

      for (int i = handles.length - 1; i >= 0; i--) {
         int handle = handles[i];
         if (!isSignedWithRRI(handle)) {
            if (!CodeModuleManager.getModuleHash(handle, hash)) {
               return 1;
            }

            int perm = 1;
            if (isSettingPresent(hash)) {
               perm = isDomainAllowed(hash, domain, domainTag);
            }

            if (perm == 1) {
               return 1;
            }

            prompt |= perm == 2;
         }
      }

      int numAdditionalModules = additionalModules == null ? 0 : additionalModules.length;

      for (int i = numAdditionalModules - 1; i >= 0; i--) {
         int handle = additionalModules[i];
         if (!isSignedWithRRI(handle)) {
            if (!CodeModuleManager.getModuleHash(handle, hash)) {
               return 1;
            }

            int perm = 1;
            if (isSettingPresent(hash)) {
               perm = isDomainAllowed(hash, domain, domainTag);
            }

            if (perm == 1) {
               return 1;
            }

            prompt |= perm == 2;
         }
      }

      return prompt ? 2 : 0;
   }

   static final boolean isFlagSetBoolean(byte[] hash, int allowFlag) {
      return isFlagSet(hash, allowFlag);
   }

   static final int isFlagSetTernary(byte[] hash, int allowFlag, int promptFlag) {
      if (isFlagSet(hash, allowFlag)) {
         return isFlagSet(hash, promptFlag) ? 2 : 0;
      } else {
         return 1;
      }
   }

   static final void assertIPCAllowed(boolean checkProcess) {
      if (!_isDesktopVM) {
         if (!ApplicationControl.isIPCAllowed(checkProcess)) {
            throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[2]);
         }
      }
   }

   static final boolean isThemeDataAllowed(int moduleHandle) {
      boolean rc = checkModulePermissions(moduleHandle, 19) == 0;
      if (rc && !isSignedWithRRI(moduleHandle)) {
         setGrantedPermissions(17592186044416L);
      }

      return rc;
   }

   static final boolean isAuthenticatorApiAllowed(int moduleHandle) {
      boolean rc = checkModulePermissions(moduleHandle, 20, -1) == 0;
      if (rc) {
         setGrantedPermissions(8796093022208L);
      }

      return rc;
   }

   static final int checkModulePermissions(int moduleHandle, int allowFlag) {
      return checkModulePermissions(moduleHandle, allowFlag, -1, 63);
   }

   static final int checkModulePermissionsIgnoringBypass(int moduleHandle, int allowFlag) {
      return checkModulePermissions(moduleHandle, allowFlag, -1, 0);
   }

   static final int checkModulePermissions(int moduleHandle, int allowFlag, int promptFlag) {
      return checkModulePermissions(moduleHandle, allowFlag, promptFlag, 63);
   }

   private static final int checkModulePermissions(int moduleHandle, int allowFlag, int promptFlag, int bypassFlag) {
      checkInitialization();
      UserSetting us = _userPermissions.getSetting(moduleHandle);
      long permissions;
      synchronized (_permissions) {
         if (moduleHandle == 0) {
            if (us == null) {
               permissions = _permissions.getDefaultPermissions();
            } else {
               permissions = us.getPermissions();
            }
         } else {
            permissions = doGetModulePermissions(moduleHandle);
         }
      }

      if (bypassFlag != 63 || (permissions & Long.MIN_VALUE >>> bypassFlag) == 0) {
         if (allowFlag != -1 && (permissions & Long.MIN_VALUE >>> allowFlag) == 0) {
            return 1;
         }

         if (promptFlag != -1
            && (permissions & Long.MIN_VALUE >>> promptFlag) != 0
            && (us == null || (us.getDontPrompt() & Long.MIN_VALUE >>> promptFlag) == 0)) {
            return 2;
         }
      }

      return 0;
   }

   static final boolean setRestrictiveDefaultPermission() {
      return setModuleUserPermission(ApplicationControlConstants.EMPTY_HASH, 0, 2229815737965346816L);
   }

   static final boolean setDefaultPermission() {
      return setModuleUserPermission(ApplicationControlConstants.EMPTY_HASH, 0, _userPermissions.getDefaultSetting().getPermissions());
   }

   static final boolean setPermissiveDefaultPermission() {
      return setModuleUserPermission(ApplicationControlConstants.EMPTY_HASH, 0, 3869672972855803904L);
   }

   static final boolean setModuleUserPermission(byte[] moduleHash, int moduleHandle, ApplicationPermissions appPermissions) {
      if (appPermissions != null && moduleHash != null) {
         int[] appPerms = appPermissions.getPermissionKeys();
         long setAppPerms = 0;

         for (int i = 0; i < appPerms.length; i++) {
            int currAppPermKey = appPerms[i];
            int currAppPerm = appPermissions.getPermissionInternal(currAppPermKey);
            int permission = ApplicationControlConstants.APP_PERM_TO_APP_CONTROL_MAP[currAppPermKey];
            if (currAppPerm != -1) {
               setAppPerms |= getPermissionFlags(permission);
               setAppPerms |= correspondingPromptFlag(permission);
            }
         }

         UserSetting defaults = _userPermissions.getDefaultSetting();
         UserSetting us = getOrCreateUserSetting(moduleHash, moduleHandle);
         return applyModulePermissions(
            moduleHash,
            moduleHandle,
            us,
            setAppPerms & convertApplicationPermissions(appPermissions) | (setAppPerms ^ -1) & defaults.getPermissions(),
            us.getDontPrompt() & (setAppPerms ^ -1),
            setAppPerms
         );
      } else {
         throw new IllegalArgumentException();
      }
   }

   static final boolean setModuleUserPermission(byte[] moduleHash, int moduleHandle, long permissions) {
      UserSetting us = getOrCreateUserSetting(moduleHash, moduleHandle);
      return applyModulePermissions(moduleHash, moduleHandle, us, permissions, us.getDontPrompt(), us.getIsSet());
   }

   private static final boolean applyModulePermissions(byte[] moduleHash, int moduleHandle, UserSetting us, long permissions, long dontPrompt, long isSet) {
      synchronized (_permissions) {
         _permissions.invalidate();
         long policyPermissions = getPolicyPermissions(moduleHandle, moduleHash);
         long oldPermissions;
         if (moduleHandle == 0) {
            oldPermissions = us.getPermissions();
         } else {
            oldPermissions = doGetModulePermissions(moduleHandle);
         }

         UserSetting oldUS = new UserSetting(us);
         if (moduleHandle == 0) {
            _userPermissions.setPermissions(oldUS, us, combinePermissions(policyPermissions, permissions));
            if (isMoreRestrictive(oldPermissions, us.getPermissions())) {
               return true;
            }
         } else {
            _userPermissions.setPermissions(oldUS, us, permissions, dontPrompt, isSet);
            long newPermissions = combinePermissions(policyPermissions, permissions, dontPrompt);
            doSetModulePermissions(moduleHandle, newPermissions);
            if (isMoreRestrictive(oldPermissions, newPermissions)) {
               return true;
            }
         }

         return false;
      }
   }

   static final boolean setModulePermission(byte[] moduleHash, int moduleHandle, long permissions) {
      if (moduleHandle != 0 && moduleHash != null) {
         synchronized (_permissions) {
            _permissions.invalidate();
            long policyPermissions = getPolicyPermissions(moduleHandle, moduleHash);
            long oldPermissions = doGetModulePermissions(moduleHandle);
            long newPermissions = combinePermissions(policyPermissions, permissions);
            doSetModulePermissions(moduleHandle, newPermissions);
            return isMoreRestrictive(oldPermissions, newPermissions);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final long getPolicyPermissions(int moduleHandle, byte[] moduleHash) {
      long policyPermissions = 0;
      if (moduleHandle == 0) {
         return _permissions.getPermittedPermissions();
      } else if (moduleHash == null) {
         throw new IllegalArgumentException();
      } else {
         return getModulePolicyPermittedPermissions(moduleHandle, moduleHash);
      }
   }

   private static final UserSetting getOrCreateUserSetting(byte[] moduleHash, int moduleHandle) {
      UserSetting us = _userPermissions.getSetting(moduleHandle);
      if (us == null) {
         us = new UserSetting(moduleHash, _userPermissions.getDefaultSetting().getPermissions());
         _userPermissions.putSetting(moduleHandle, us, false);
         return us;
      }

      if (moduleHandle == 0) {
         _userPermissions.removeBackedUpDefaultSetting();
      }

      return us;
   }

   static final boolean isPromptResponseSaved(int promptFlag) {
      boolean saved = false;
      long mask = Long.MIN_VALUE >>> promptFlag;
      UserSetting us = _userPermissions.getSetting(_processModuleHandle);
      if (us != null) {
         saved |= (us.getDontPrompt() & mask) != 0;
      } else if (!isSignedWithRRI(_processModuleHandle)) {
         saved = false;
      }

      int[] handles = TraceBack.getCallingModules();

      for (int i = handles.length - 1; i >= 0 && saved; i--) {
         int handle = handles[i];
         us = _userPermissions.getSetting(handle);
         if (us != null) {
            saved |= (us.getDontPrompt() & mask) != 0;
         } else if (!isSignedWithRRI(handle)) {
            saved = false;
         }
      }

      return saved;
   }

   private static final void saveResponsePermissions(int[] handles, int allowFlag, int promptFlag, boolean allow) {
      byte[] hash = new byte[20];
      if (_processModuleHandle != 0 && Arrays.getIndex(handles, _processModuleHandle) == -1) {
         Arrays.add(handles, _processModuleHandle);
      }

      long allowMask = Long.MIN_VALUE >>> allowFlag;
      long promptMask = Long.MIN_VALUE >>> promptFlag;

      for (int i = 0; i < handles.length; i++) {
         int moduleHandle = handles[i];
         if (!isSignedWithRRI(moduleHandle)) {
            if (!CodeModuleManager.getModuleHash(moduleHandle, hash)) {
               throw new IllegalArgumentException();
            }

            UserSetting us = _userPermissions.getSetting(moduleHandle);
            if (us == null) {
               us = new UserSetting(hash, doGetModulePermissions(moduleHandle));
               _userPermissions.putSetting(moduleHandle, us, false);
            } else if (!us.hashEquals(hash)) {
               throw new RuntimeException();
            }

            UserSetting oldUS = new UserSetting(us);
            long perms = us.getPermissions();
            if (!allow) {
               perms &= allowMask ^ -1;
            }

            _userPermissions.setPermissions(
               oldUS, us, perms & (promptMask ^ -1), us.getDontPrompt() | promptMask | allowMask, us.getIsSet() | promptMask | allowMask, false
            );
            long policyPermissions = getModulePolicyPermittedPermissions(moduleHandle, hash);
            doSetModulePermissions(moduleHandle, combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt()));
         }
      }

      _userPermissions.commit();
   }

   static final void resetPrompts(int moduleHandle) {
      _responseStackPermissions.removeResponses(moduleHandle);
      UserSetting us = _userPermissions.getSetting(moduleHandle);

      byte[] hash;
      try {
         hash = CodeModuleManager.getModuleHash(moduleHandle);
      } catch (IllegalArgumentException iae) {
         return;
      }

      if (us != null && hash != null) {
         UserSetting oldUS = new UserSetting(us);
         _userPermissions.resetPrompts(oldUS, us);
         long policyPermissions = getModulePolicyPermittedPermissions(moduleHandle, hash);
         doSetModulePermissions(moduleHandle, combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt()));
      }
   }

   static final void resetPrompt(int moduleHandle, int allowFlag, int promptFlag) {
      _responseStackPermissions.removeResponses(moduleHandle, allowFlag, promptFlag);
      UserSetting us = _userPermissions.getSetting(moduleHandle);

      byte[] hash;
      try {
         hash = CodeModuleManager.getModuleHash(moduleHandle);
      } catch (IllegalArgumentException iae) {
         return;
      }

      if (us != null && hash != null) {
         UserSetting oldUS = new UserSetting(us);
         _userPermissions.resetPrompt(oldUS, us, Long.MIN_VALUE >>> allowFlag | Long.MIN_VALUE >>> promptFlag);
         long policyPermissions = getModulePolicyPermittedPermissions(moduleHandle, hash);
         doSetModulePermissions(moduleHandle, combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt()));
      }
   }

   static final void resetAllPrompts() {
      _responseStackPermissions.removeAllResponses();
      int[] handles = _userPermissions.getLoadedHandles();

      for (int i = 0; i < handles.length; i++) {
         int currHandle = handles[i];
         UserSetting us = _userPermissions.getSetting(currHandle);
         if (us != null) {
            byte[] hash = us.getHash();
            UserSetting oldUS = new UserSetting(us);
            _userPermissions.resetPrompts(oldUS, us, false);
            long policyPermissions = getModulePolicyPermittedPermissions(currHandle, hash);
            doSetModulePermissions(currHandle, combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt()));
         }
      }

      _userPermissions.commit();
   }

   static final void resetAllPrompts(int allowFlag, int promptFlag) {
      _responseStackPermissions.removeAllResponses(allowFlag, promptFlag);
      int[] handles = _userPermissions.getLoadedHandles();
      long mask = Long.MIN_VALUE >>> allowFlag | Long.MIN_VALUE >>> promptFlag;

      for (int i = 0; i < handles.length; i++) {
         int currHandle = handles[i];
         UserSetting us = _userPermissions.getSetting(currHandle);
         if (us != null) {
            byte[] hash = us.getHash();
            UserSetting oldUS = new UserSetting(us);
            _userPermissions.resetPrompt(oldUS, us, mask, false);
            long policyPermissions = getModulePolicyPermittedPermissions(currHandle, hash);
            doSetModulePermissions(currHandle, combinePermissions(policyPermissions, us.getPermissions(), us.getDontPrompt()));
         }
      }

      _userPermissions.commit();
   }

   private static final long getModulePermission(int handle, byte[] hash, int flag) {
      return isFlagSet(hash, flag) ? Long.MIN_VALUE >>> flag : 0;
   }

   private static final long getModuleControlPermissions(int handle, byte[] hash) {
      long perms = 0;
      perms |= ControlledAccess.verifyCodeModuleSignature(handle, 51) ? 1 : 0;
      perms |= isFlagSet(hash, 0) ? Long.MIN_VALUE : 0;
      perms |= isFlagSet(hash, 1) ? 4611686018427387904L : 0;
      return perms | (isFlagSet(hash, 20) ? 8796093022208L : 0);
   }

   private static final long getModulePolicyPermittedPermissions(int handle, byte[] hash) {
      boolean policySettingPresent = isModuleSettingPresent(hash);
      long permissions;
      if (policySettingPresent) {
         permissions = getModuleAppControlPermittedPermissions(handle, hash);
      } else {
         permissions = _permissions.getPermittedPermissions();
      }

      return permissions | getModuleControlPermissions(handle, hash);
   }

   private static final long getModulePermissions(int handle, byte[] hash) {
      long perms = getModuleControlPermissions(handle, hash);
      if ((perms & 1) != 0) {
         return perms;
      }

      long flag = Long.MIN_VALUE;
      perms |= isFlagSet(hash, 2) ? flag >>> 2 : 0;
      perms |= isFlagSet(hash, 3) ? flag >>> 3 : 0;
      perms |= isFlagSet(hash, 4) ? flag >>> 4 : 0;
      perms |= isFlagSet(hash, 5) ? flag >>> 5 : 0;
      perms |= isFlagSet(hash, 6) ? flag >>> 6 : 0;
      perms |= isFlagSet(hash, 7) ? flag >>> 7 : 0;
      perms |= isFlagSet(hash, 8) ? flag >>> 8 : 0;
      perms |= isFlagSet(hash, 9) ? flag >>> 9 : 0;
      perms |= isFlagSet(hash, 10) ? flag >>> 10 : 0;
      perms |= isFlagSet(hash, 11) ? flag >>> 11 : 0;
      perms |= isFlagSet(hash, 12) ? flag >>> 12 : 0;
      perms |= isFlagSet(hash, 13) ? flag >>> 13 : 0;
      perms |= isFlagSet(hash, 14) ? flag >>> 14 : 0;
      perms |= isFlagSet(hash, 15) ? flag >>> 15 : 0;
      perms |= isFlagSet(hash, 16) ? flag >>> 16 : 0;
      perms |= isFlagSet(hash, 17) ? flag >>> 17 : 0;
      perms |= isFlagSet(hash, 18) ? flag >>> 18 : 0;
      perms |= isFlagSet(hash, 19) ? flag >>> 19 : 0;
      perms |= isFlagSet(hash, 21) ? flag >>> 21 : 0;
      perms |= isFlagSet(hash, 22) ? flag >>> 22 : 0;
      perms |= isFlagSet(hash, 23) ? flag >>> 23 : 0;
      perms |= isFlagSet(hash, 24) ? flag >>> 24 : 0;
      perms |= isFlagSet(hash, 25) ? flag >>> 25 : 0;
      perms |= isFlagSet(hash, 26) ? flag >>> 26 : 0;
      perms |= isFlagSet(hash, 27) ? flag >>> 27 : 0;
      perms |= isFlagSet(hash, 28) ? flag >>> 28 : 0;
      perms |= isFlagSet(hash, 29) ? flag >>> 29 : 0;
      perms |= isFlagSet(hash, 30) ? flag >>> 30 : 0;
      perms |= isFlagSet(hash, 31) ? flag >>> 31 : 0;
      return perms | (isFlagSet(hash, 32) ? flag >>> 32 : 0);
   }

   private static final long getModuleAppControlPermittedPermissions(int handle, byte[] hash) {
      long perms = getModuleControlPermissions(handle, hash);
      if ((perms & 1) != 0) {
         return perms;
      }

      long flag = Long.MIN_VALUE;
      perms |= isPermitted(hash, 2) ? flag >>> 2 : 0;
      perms |= isPermitted(hash, 3) ? flag >>> 3 : 0;
      perms |= isPermitted(hash, 4) ? flag >>> 4 : 0;
      perms |= isPermitted(hash, 5) ? flag >>> 5 : 0;
      perms |= isPermitted(hash, 6) ? flag >>> 6 : 0;
      perms |= isPermitted(hash, 7) ? flag >>> 7 : 0;
      perms |= isPermitted(hash, 8) ? flag >>> 8 : 0;
      perms |= isPermitted(hash, 9) ? flag >>> 9 : 0;
      perms |= isPermitted(hash, 10) ? flag >>> 10 : 0;
      perms |= isPermitted(hash, 11) ? flag >>> 11 : 0;
      perms |= isPermitted(hash, 12) ? flag >>> 12 : 0;
      perms |= isPermitted(hash, 13) ? flag >>> 13 : 0;
      perms |= isPermitted(hash, 14) ? flag >>> 14 : 0;
      perms |= isPermitted(hash, 15) ? flag >>> 15 : 0;
      perms |= isPermitted(hash, 16) ? flag >>> 16 : 0;
      perms |= isPermitted(hash, 17) ? flag >>> 17 : 0;
      perms |= isPermitted(hash, 18) ? flag >>> 18 : 0;
      perms |= isPermitted(hash, 19) ? flag >>> 19 : 0;
      perms |= isPermitted(hash, 21) ? flag >>> 21 : 0;
      perms |= isPermitted(hash, 22) ? flag >>> 22 : 0;
      perms |= isPermitted(hash, 23) ? flag >>> 23 : 0;
      perms |= isPermitted(hash, 24) ? flag >>> 24 : 0;
      perms |= isPermitted(hash, 25) ? flag >>> 25 : 0;
      perms |= isPermitted(hash, 26) ? flag >>> 26 : 0;
      perms |= isPermitted(hash, 27) ? flag >>> 27 : 0;
      perms |= isPermitted(hash, 28) ? flag >>> 28 : 0;
      perms |= isPermitted(hash, 29) ? flag >>> 29 : 0;
      perms |= isPermitted(hash, 30) ? flag >>> 30 : 0;
      perms |= isPermitted(hash, 31) ? flag >>> 31 : 0;
      return perms | (isPermitted(hash, 32) ? flag >>> 32 : 0);
   }

   static final long getRequestedPermissions(ApplicationPermissions requestedPermissions) {
      long perms = 0;
      long flag = Long.MIN_VALUE;
      perms |= requestedPermissions.getPermissionInternal(0) != 997 ? flag >>> 20 : 0;
      perms |= requestedPermissions.getPermissionInternal(11) != 997 ? flag >>> 2 : 0;
      perms |= requestedPermissions.getPermissionInternal(10) != 997 ? flag >>> 3 : 0;
      perms |= requestedPermissions.getPermissionInternal(7) != 997 ? flag >>> 5 : 0;
      perms |= requestedPermissions.getPermissionInternal(13) != 997 ? flag >>> 7 : 0;
      perms |= requestedPermissions.getPermissionInternal(15) != 997 ? flag >>> 8 : 0;
      perms |= requestedPermissions.getPermissionInternal(5) != 997 ? flag >>> 10 : 0;
      perms |= requestedPermissions.getPermissionInternal(16) != 997 ? flag >>> 11 : 0;
      perms |= requestedPermissions.getPermissionInternal(2) != 997 ? flag >>> 12 : 0;
      perms |= requestedPermissions.getPermissionInternal(6) != 997 ? flag >>> 13 : 0;
      perms |= requestedPermissions.getPermissionInternal(1) != 997 ? flag >>> 14 : 0;
      perms |= requestedPermissions.getPermissionInternal(9) != 997 ? flag >>> 15 : 0;
      perms |= requestedPermissions.getPermissionInternal(12) != 997 ? flag >>> 16 : 0;
      perms |= requestedPermissions.getPermissionInternal(14) != 997 ? flag >>> 17 : 0;
      perms |= requestedPermissions.getPermissionInternal(18) != 997 ? flag >>> 19 : 0;
      perms |= requestedPermissions.getPermissionInternal(8) != 997 ? flag >>> 21 : 0;
      perms |= requestedPermissions.getPermissionInternal(4) != 997 ? flag >>> 22 : 0;
      perms |= requestedPermissions.getPermissionInternal(3) != 997 ? flag >>> 23 : 0;
      perms |= requestedPermissions.getPermissionInternal(17) != 997 ? flag >>> 25 : 0;
      perms |= requestedPermissions.getPermissionInternal(19) != 997 ? flag >>> 27 : 0;
      perms |= requestedPermissions.getPermissionInternal(20) != 997 ? flag >>> 29 : 0;
      return perms | (requestedPermissions.getPermissionInternal(21) != 997 ? flag >>> 31 : 0);
   }

   static final long convertApplicationPermissions(ApplicationPermissions appPerms) {
      long perms = 0;
      perms |= convertPermToFlag(appPerms, 0, 20);
      perms |= convertPermToFlag(appPerms, 11, 2);
      perms |= convertPermToFlagTernary(appPerms, 10, 3, 4);
      perms |= convertPermToFlagTernary(appPerms, 7, 5, 6);
      perms |= convertPermToFlag(appPerms, 13, 7);
      perms |= convertPermToFlagTernary(appPerms, 15, 8, 9);
      perms |= convertPermToFlag(appPerms, 5, 10);
      perms |= convertPermToFlag(appPerms, 16, 11);
      perms |= convertPermToFlag(appPerms, 2, 12);
      perms |= convertPermToFlag(appPerms, 6, 13);
      perms |= convertPermToFlag(appPerms, 1, 14);
      perms |= convertPermToFlag(appPerms, 9, 15);
      perms |= convertPermToFlag(appPerms, 12, 16);
      perms |= convertPermToFlagTernary(appPerms, 14, 17, 18);
      perms |= convertPermToFlag(appPerms, 18, 19);
      perms |= convertPermToFlag(appPerms, 8, 21);
      perms |= convertPermToFlag(appPerms, 4, 22);
      perms |= convertPermToFlagTernary(appPerms, 3, 23, 24);
      perms |= convertPermToFlagTernary(appPerms, 17, 25, 26);
      perms |= convertPermToFlagTernary(appPerms, 19, 27, 28);
      perms |= convertPermToFlagTernary(appPerms, 20, 29, 30);
      return perms | convertPermToFlagTernary(appPerms, 21, 31, 32);
   }

   private static final long convertPermToFlag(ApplicationPermissions appPerms, int appPerm, int allowFlag) {
      return appPerms.getPermissionInternal(appPerm) == 999 ? getPermissionFlags(allowFlag) : 0;
   }

   private static final long convertPermToFlagTernary(ApplicationPermissions appPerms, int appPerm, int allowFlag, int promptFlag) {
      int permSetting = appPerms.getPermissionInternal(appPerm);
      if (permSetting == 999) {
         return getPermissionFlags(allowFlag);
      } else {
         return permSetting == 998 ? getPermissionFlags(allowFlag, promptFlag) : 0;
      }
   }

   static final ApplicationPermissions buildPermissions(long permissions) {
      return buildPermissions(permissions, -1);
   }

   static final ApplicationPermissions buildPermissions(long permissions, long nonDefaults) {
      ApplicationPermissions appPerms = new ApplicationPermissions();
      addAppPermission(appPerms, 0, permissions, nonDefaults, 20);
      addAppPermission(appPerms, 11, permissions, nonDefaults, 2);
      addAppPermissionTernary(appPerms, 10, permissions, nonDefaults, 3, 4);
      addAppPermissionTernary(appPerms, 7, permissions, nonDefaults, 5, 6);
      addAppPermission(appPerms, 13, permissions, nonDefaults, 7);
      addAppPermissionTernary(appPerms, 15, permissions, nonDefaults, 8, 9);
      addAppPermission(appPerms, 5, permissions, nonDefaults, 10);
      addAppPermission(appPerms, 16, permissions, nonDefaults, 11);
      addAppPermission(appPerms, 2, permissions, nonDefaults, 12);
      addAppPermission(appPerms, 6, permissions, nonDefaults, 13);
      addAppPermission(appPerms, 1, permissions, nonDefaults, 14);
      addAppPermission(appPerms, 9, permissions, nonDefaults, 15);
      addAppPermission(appPerms, 12, permissions, nonDefaults, 16);
      addAppPermissionTernary(appPerms, 14, permissions, nonDefaults, 17, 18);
      addAppPermission(appPerms, 18, permissions, nonDefaults, 19);
      addAppPermission(appPerms, 8, permissions, nonDefaults, 21);
      addAppPermission(appPerms, 4, permissions, nonDefaults, 22);
      addAppPermissionTernary(appPerms, 3, permissions, nonDefaults, 23, 24);
      addAppPermissionTernary(appPerms, 17, permissions, nonDefaults, 25, 26);
      addAppPermissionTernary(appPerms, 19, permissions, nonDefaults, 27, 28);
      addAppPermissionTernary(appPerms, 20, permissions, nonDefaults, 29, 30);
      addAppPermissionTernary(appPerms, 21, permissions, nonDefaults, 31, 32);
      return appPerms;
   }

   private static final void addAppPermission(ApplicationPermissions appPerms, int appPerm, long permissions, long nonDefaults, int allowFlag) {
      if (isBitSet(nonDefaults, allowFlag)) {
         if (isBitSet(permissions, allowFlag)) {
            appPerms.addPermission(appPerm, 999);
            return;
         }

         appPerms.addPermission(appPerm, 997);
      }
   }

   private static final void addAppPermissionTernary(
      ApplicationPermissions appPerms, int appPerm, long permissions, long nonDefaults, int allowFlag, int promptFlag
   ) {
      if (isBitSet(nonDefaults, allowFlag)) {
         if (isBitSet(permissions, promptFlag)) {
            appPerms.addPermission(appPerm, 998);
            return;
         }

         if (isBitSet(permissions, allowFlag)) {
            appPerms.addPermission(appPerm, 999);
            return;
         }

         appPerms.addPermission(appPerm, 997);
      }
   }

   static final long getPermissionFlags(int allowFlag) {
      return Long.MIN_VALUE >>> allowFlag;
   }

   static final long getPermissionFlags(int allowFlag, int promptFlag) {
      return Long.MIN_VALUE >>> allowFlag | Long.MIN_VALUE >>> promptFlag;
   }

   static final boolean isBitSet(long mask, int flag) {
      return (mask & Long.MIN_VALUE >>> flag) != 0;
   }

   private static final long correspondingPromptFlag(int allowFlag) {
      return (1477252511105548288L & getPermissionFlags(allowFlag)) >>> 1;
   }

   private static final long combinePermissions(long itpolicy, long user) {
      return combinePermissions(itpolicy, user, 0);
   }

   private static final long combinePermissions(long itpolicy, long user, long dontPrompt) {
      long perms = 0;
      long internalAllowFlag = 1152921504606846976L;
      long internalPromptFlag = 576460752303423488L;
      long externalAllowFlag = 288230376151711744L;
      long externalPromptFlag = 144115188075855872L;
      long phoneAllowFlag = 36028797018963968L;
      long phonePromptFlag = 18014398509481984L;
      long lapiAllowFlag = 70368744177664L;
      long lapiPromptFlag = 35184372088832L;
      long deviceSettingsAllowFlag = 1099511627776L;
      long deviceSettingsPromptFlag = 549755813888L;
      long screenCaptureAllowFlag = 274877906944L;
      long screenCapturePromptFlag = 137438953472L;
      long wifiAllowFlag = 68719476736L;
      long wifiPromptFlag = 34359738368L;
      long idleTimerAllowFlag = 17179869184L;
      long idleTimerPromptFlag = 8589934592L;
      long mediaAllowFlag = 4294967296L;
      long mediaPromptFlag = 2147483648L;
      if ((itpolicy & 1) != 0) {
         perms = user | 1;
      } else {
         long orPerms = itpolicy | user;
         perms = itpolicy & user;
         if ((perms & internalAllowFlag) != 0) {
            perms |= orPerms & internalPromptFlag & (dontPrompt & internalPromptFlag ^ -1);
         }

         if ((perms & externalAllowFlag) != 0) {
            perms |= orPerms & externalPromptFlag & (dontPrompt & externalPromptFlag ^ -1);
         }

         if ((perms & phoneAllowFlag) != 0) {
            perms |= orPerms & phonePromptFlag & (dontPrompt & phonePromptFlag ^ -1);
         }

         if ((perms & lapiAllowFlag) != 0) {
            perms |= orPerms & lapiPromptFlag & (dontPrompt & lapiPromptFlag ^ -1);
         }

         if ((perms & deviceSettingsAllowFlag) != 0) {
            perms |= orPerms & deviceSettingsPromptFlag & (dontPrompt & deviceSettingsPromptFlag ^ -1);
         }

         if ((perms & screenCaptureAllowFlag) != 0) {
            perms |= orPerms & screenCapturePromptFlag & (dontPrompt & screenCapturePromptFlag ^ -1);
         }

         if ((perms & wifiAllowFlag) != 0) {
            perms |= orPerms & wifiPromptFlag & (dontPrompt & wifiPromptFlag ^ -1);
         }

         if ((perms & idleTimerAllowFlag) != 0) {
            perms |= orPerms & idleTimerPromptFlag & (dontPrompt & idleTimerPromptFlag ^ -1);
         }

         if ((perms & mediaAllowFlag) != 0) {
            perms |= orPerms & mediaPromptFlag & (dontPrompt & mediaPromptFlag ^ -1);
         }
      }

      return perms | itpolicy & -4611677222334365695L;
   }

   private static final boolean isMoreRestrictive(long oldPermission, long newPermission) {
      long grantedPermissions = getGrantedPermissions();
      oldPermission &= Integer.MIN_VALUE;
      newPermission &= Integer.MIN_VALUE;
      grantedPermissions |= (grantedPermissions & 1477252511105548288L) >> 1;
      long xored = oldPermission ^ newPermission;
      long removedAllowFlags = xored & oldPermission & 3873059760727130112L & grantedPermissions;
      long stillSetAllowFlags = oldPermission & newPermission & 1477252511105548288L & grantedPermissions;
      long newlySetFlags = xored & newPermission;
      return removedAllowFlags != 0 || (stillSetAllowFlags >>> 1 & newlySetFlags) != 0;
   }

   static final boolean isPolicyDataPresent() {
      return _permissions.isAppControlPolicyDataPresent();
   }

   static final void disableXmit() {
      _permissions.disableXmit();
   }

   static final boolean isXmitDisabled() {
      return _permissions.isXmitDisabled();
   }

   static final void logDenial(int moduleHandle, int flag) {
      String text = "d " + (moduleHandle == 0 ? "0?" : CodeModuleManager.getModuleName(moduleHandle)) + ":" + flag;
      EventLog.logEvent(-4492041993596154793L, System.currentTimeMillis(), (byte)5, text.getBytes());
   }

   private static final void logReset(String reason) {
      String text = "SCH RST: " + reason;
      System.out.println("Schedule Device Reset: " + reason);
      EventLog.logEvent(-4492041993596154793L, System.currentTimeMillis(), (byte)5, text.getBytes());
   }

   private static final boolean doSetModulePermissions(int moduleHandle, long permissions) {
      try {
         setModulePermissions(moduleHandle, permissions);
         return true;
      } catch (IllegalArgumentException iae) {
         return false;
      }
   }

   private static final long doGetModulePermissions(int moduleHandle) {
      try {
         return getModulePermissions(moduleHandle);
      } catch (IllegalArgumentException var2) {
         return 0;
      }
   }

   private static final int doGetCallingModulesPermission(
      long allowMask, long promptMask, long bypassMask, boolean checkProcess, int processModuleHandle, int[] additionalModules
   ) {
      try {
         return getCallingModulesPermission(allowMask, promptMask, bypassMask, checkProcess, processModuleHandle, additionalModules);
      } catch (IllegalArgumentException var10) {
         return 0;
      }
   }

   static final boolean isModuleSettingPresent(byte[] hash) {
      return hash != null && _permissions.isAppControlPolicyDataPresent() ? isSettingPresent(hash) : false;
   }

   static final String getInternalConnectionDomains(byte[] hash) {
      return getConnectionDomains(hash, (byte)1);
   }

   static final String getExternalConnectionDomains(byte[] hash) {
      return getConnectionDomains(hash, (byte)2);
   }

   static final String getBrowserFilterConnectionDomains(byte[] hash) {
      return getConnectionDomains(hash, (byte)3);
   }

   static final String getConnectionDomains(byte[] hash, byte type) {
      byte[] domains = getAllowedDomainsUTF8(hash, type);
      if (domains == null) {
         return null;
      }

      try {
         return new String(domains, "UTF8");
      } catch (UnsupportedEncodingException e) {
         return null;
      }
   }

   static final void scheduleDeviceReset(String reason) {
      logReset(reason);
      ResourceBundle rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");
      ForcedResetManager resetManager = ForcedResetManager.getInstance();
      resetManager.scheduleDeviceReset(rb.getString(19), true);
   }

   static final void scheduleDeviceReset(String reason, long timeBetweenResetWarnings) {
      logReset(reason);
      ResourceBundle rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");
      ForcedResetManager resetManager = ForcedResetManager.getInstance();
      resetManager.scheduleDeviceReset(rb.getString(19), timeBetweenResetWarnings, true);
   }

   static final void scheduleDeviceReset(String reason, int numResetWarnings, long timeBetweenResetWarnings) {
      logReset(reason);
      ResourceBundle rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");
      ForcedResetManager resetManager = ForcedResetManager.getInstance();
      resetManager.scheduleDeviceReset(rb.getString(19), numResetWarnings, timeBetweenResetWarnings, !_permissions.isXmitDisabled());
   }

   static final void doPromptWork(int ternary, ResourceBundleFamily rb, int rbKey, int allowFlag, int promptFlag) {
      if (rb == null) {
         throw new IllegalArgumentException();
      }

      switch (ternary) {
         case -1:
            return;
         case 0:
            return;
         case 1:
            throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[allowFlag]);
         case 2:
         default:
            Process currentProcess = Process.currentProcess();
            int processHandle = currentProcess.getModuleHandle();
            boolean somePermRestrictedByPolicy = false;
            int[] stackModules = TraceBack.getCallingModules();
            int numStackModulesToChange = 0;
            int[] stackModulesToChange = new int[stackModules.length];

            for (int i = 0; i < stackModules.length; i++) {
               int moduleHandle = stackModules[i];
               UserSetting moduleUserSetting = _userPermissions.getSetting(stackModules[i]);
               if (!isSignedWithRRI(moduleHandle)
                  && (getUserPermission(moduleUserSetting, allowFlag) == 0 || getUserPermission(moduleUserSetting, promptFlag) != 0)) {
                  byte[] hash = CodeModuleManager.getModuleHash(moduleHandle);
                  if (hash != null) {
                     if (getPolicyPermissionIgnoringDefaults(moduleHandle, hash, allowFlag) == 0
                        || getPolicyPermissionIgnoringDefaults(moduleHandle, hash, promptFlag) != 0) {
                        somePermRestrictedByPolicy = true;
                        break;
                     }

                     stackModulesToChange[numStackModulesToChange++] = moduleHandle;
                  }
               }
            }

            Array.resize(stackModulesToChange, numStackModulesToChange);
            if (stackModulesToChange.length == 0) {
               somePermRestrictedByPolicy = true;
            }

            if (somePermRestrictedByPolicy) {
               int stackResponse = _responseStackPermissions.getResponsePermission(processHandle, stackModules, allowFlag, promptFlag);
               if (stackResponse == 0) {
                  return;
               }

               if (stackResponse == 1) {
                  throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[allowFlag]);
               }

               stackModulesToChange = new int[0];
            }

            String message = MessageFormat.format(rb.getString(rbKey), new String[]{currentProcess.getModuleName()});
            PermissionDialog ptd = new PermissionDialog(message, CommonResource.getString(10094), stackModules, stackModulesToChange);
            if (ptd.getPermission()) {
               if (ptd.getUserOptionCheckBoxValue()) {
                  if (somePermRestrictedByPolicy) {
                     _responseStackPermissions.setResponse(processHandle, stackModules, allowFlag, promptFlag, true);
                  } else {
                     saveResponsePermissions(stackModulesToChange, allowFlag, promptFlag, true);
                  }
               }

               setGrantedPermissions(Long.MIN_VALUE >>> allowFlag | Long.MIN_VALUE >>> promptFlag);
            } else {
               if (ptd.getUserOptionCheckBoxValue()) {
                  if (somePermRestrictedByPolicy) {
                     _responseStackPermissions.setResponse(processHandle, stackModules, allowFlag, promptFlag, false);
                  } else {
                     saveResponsePermissions(stackModulesToChange, allowFlag, promptFlag, false);
                  }
               }

               throw new ControlledAccessException(null, ApplicationControlResource.PERMISSIONS_STRINGS[allowFlag]);
            }
      }
   }

   static final native int isDomainAllowed(byte[] var0, String var1, byte var2);

   private static final native boolean isFlagSet(byte[] var0, int var1);

   private static final native boolean isPermitted(byte[] var0, int var1);

   private static final native byte[] getAllowedDomainsUTF8(byte[] var0, byte var1);

   private static final native boolean isSettingPresent(byte[] var0);

   private static final native void setModulePermissions(int var0, long var1);

   private static final native long getModulePermissions(int var0);

   private static final native long getGrantedPermissions();

   private static final native void setGrantedPermissions(long var0);

   private static final native int getCallingModulesPermission(long var0, long var2, long var4, boolean var6, int var7, int[] var8);

   static {
      if (DebugSupport.isDesktopVM()) {
         _isDesktopVM = true;
      } else {
         _processModuleHandle = Process.currentProcess().getModuleHandle();
         if (_processModuleHandle != 0) {
            try {
               _processModuleHash = CodeModuleManager.getModuleHash(_processModuleHandle);
            } catch (IllegalArgumentException iae) {
               _processModuleHandle = 0;
            }
         }

         _ar = ApplicationRegistry.getApplicationRegistry();
         if ((_permissions = (SystemPermissions)_ar.getOrWaitFor(1222085937471710457L)) == null) {
            EventLog.registerApp(-4492041993596154793L, 2, "App Perms");
            _permissions = new SystemPermissions();
            _ar.put(1222085937471710457L, _permissions);
         }

         if ((_userPermissions = (UserPermissions)_ar.getOrWaitFor(1903692481843231563L)) == null) {
            _userPermissions = new UserPermissions();
            _ar.put(1903692481843231563L, _userPermissions);
            reloadModulePermissions();
         }

         if ((_responseStackPermissions = (StackTracePermissions)_ar.getOrWaitFor(978707214979453905L)) == null) {
            _responseStackPermissions = new StackTracePermissions();
            _ar.put(978707214979453905L, _responseStackPermissions);
         }
      }
   }
}
