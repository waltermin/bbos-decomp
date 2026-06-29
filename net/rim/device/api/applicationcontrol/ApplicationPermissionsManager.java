package net.rim.device.api.applicationcontrol;

import java.util.Enumeration;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.ui.ApplicationControlRequestScreen;
import net.rim.device.apps.api.ui.ApplicationControlScreen;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public final class ApplicationPermissionsManager {
   private Object _closedSignal = new Object();
   private static final long DESCRIPTOR_ID = 3353949624016666498L;
   private static final long SCREEN_ID = 5402085941883890214L;
   private static ApplicationPermissionsManager _instance = new ApplicationPermissionsManager();

   private ApplicationPermissionsManager() {
   }

   public static final ApplicationPermissionsManager getInstance() {
      return _instance;
   }

   public final int getPermission(int permission) {
      switch (permission) {
         case -1:
            throw new Object();
         case 0:
         default:
            return this.convertValue(ApplicationControl.isAuthenticatorApiAllowed(true));
         case 1:
            return this.convertValue(ApplicationControl.isBluetoothSerialProfileAllowed(true));
         case 2:
            return this.convertValue(ApplicationControl.isBrowserFilterAllowed(null, true));
         case 3:
            return this.convertValue(ApplicationControl.isChangeDeviceSettingsAllowed(true));
         case 4:
            return this.convertValue(ApplicationControl.isCMMApiAllowed(true));
         case 5:
            return this.convertValue(ApplicationControl.isEmailAllowed(true));
         case 6:
            return this.convertValue(ApplicationControl.isEventInjectorAllowed(true));
         case 7:
            return this.convertValue(ApplicationControl.isExternalConnectionAllowed(null, true));
         case 8:
            return this.convertValue(ApplicationControl.isFileApiAllowed(true));
         case 9:
            return this.convertValue(ApplicationControl.isHandheldKeyStoreAllowed(true));
         case 10:
            return this.convertValue(ApplicationControl.isInternalConnectionAllowed(null, true));
         case 11:
            return this.convertValue(ApplicationControl.isIPCAllowed(true));
         case 12:
            return this.convertValue(ApplicationControl.isKeyStoreMediumSecurityAllowed(true));
         case 13:
            return this.convertValue(ApplicationControl.isLocalConnectionAllowed(true));
         case 14:
            return this.convertValue(ApplicationControl.isLocationApiAllowed(true));
         case 15:
            return this.convertValue(ApplicationControl.isPhoneAllowed(true));
         case 16:
            return this.convertValue(ApplicationControl.isPIMAllowed(true));
         case 17:
            return this.convertValue(ApplicationControl.isScreenCaptureAllowed(true));
         case 18:
            return this.convertValue(ApplicationControl.isThemeDataAllowed(TraceBack.getCallingModule(0)));
         case 19:
            return this.convertValue(ApplicationControl.isWiFiAllowed(true));
         case 20:
            return this.convertValue(ApplicationControl.isIdleTimerAllowed(true));
         case 21:
            return this.convertValue(ApplicationControl.isMediaAllowed(true));
      }
   }

   public final int getPermission(int permission, String domain) {
      switch (permission) {
         case 2:
            return this.convertValue(ApplicationControl.isBrowserFilterAllowed(domain, true));
         case 7:
            return this.convertValue(ApplicationControl.isExternalConnectionAllowed(domain, true));
         case 10:
            return this.convertValue(ApplicationControl.isInternalConnectionAllowed(domain, true));
         default:
            throw new Object();
      }
   }

   public final ApplicationPermissions getApplicationPermissions() {
      ApplicationPermissions permissions = (ApplicationPermissions)(new Object());
      permissions.addPermission(0, this.convertValue(ApplicationControl.isAuthenticatorApiAllowed(true)));
      permissions.addPermission(1, this.convertValue(ApplicationControl.isBluetoothSerialProfileAllowed(true)));
      permissions.addPermission(2, this.convertValue(ApplicationControl.isBrowserFilterAllowed(null, true)));
      permissions.addPermission(3, this.convertValue(ApplicationControl.isChangeDeviceSettingsAllowed(true)));
      permissions.addPermission(4, this.convertValue(ApplicationControl.isCMMApiAllowed(true)));
      permissions.addPermission(5, this.convertValue(ApplicationControl.isEmailAllowed(true)));
      permissions.addPermission(6, this.convertValue(ApplicationControl.isEventInjectorAllowed(true)));
      permissions.addPermission(7, this.convertValue(ApplicationControl.isExternalConnectionAllowed(null, true)));
      permissions.addPermission(8, this.convertValue(ApplicationControl.isFileApiAllowed(true)));
      permissions.addPermission(9, this.convertValue(ApplicationControl.isHandheldKeyStoreAllowed(true)));
      permissions.addPermission(11, this.convertValue(ApplicationControl.isIPCAllowed(true)));
      permissions.addPermission(10, this.convertValue(ApplicationControl.isInternalConnectionAllowed(null, true)));
      permissions.addPermission(12, this.convertValue(ApplicationControl.isKeyStoreMediumSecurityAllowed(true)));
      permissions.addPermission(13, this.convertValue(ApplicationControl.isLocalConnectionAllowed(true)));
      permissions.addPermission(14, this.convertValue(ApplicationControl.isLocationApiAllowed(true)));
      permissions.addPermission(15, this.convertValue(ApplicationControl.isPhoneAllowed(true)));
      permissions.addPermission(16, this.convertValue(ApplicationControl.isPIMAllowed(true)));
      permissions.addPermission(17, this.convertValue(ApplicationControl.isScreenCaptureAllowed(true)));
      permissions.addPermission(18, this.convertValue(ApplicationControl.isThemeDataAllowed(TraceBack.getCallingModule(0))));
      permissions.addPermission(19, this.convertValue(ApplicationControl.isWiFiAllowed(true)));
      permissions.addPermission(20, this.convertValue(ApplicationControl.isIdleTimerAllowed(true)));
      permissions.addPermission(21, this.convertValue(ApplicationControl.isMediaAllowed(true)));
      return permissions;
   }

   public final int getMaxAllowable(int permission) {
      switch (permission) {
         case -1:
            throw new Object();
         case 0:
         default:
            return this.convertValue(ApplicationControl.getPolicyPermission(20, true));
         case 1:
            return this.convertValue(ApplicationControl.getPolicyPermission(14, true));
         case 2:
            return this.convertValue(ApplicationControl.getPolicyPermission(12, true));
         case 3:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(23, 24, true));
         case 4:
            return this.convertValue(ApplicationControl.getPolicyPermission(22, true));
         case 5:
            return this.convertValue(ApplicationControl.getPolicyPermission(10, true));
         case 6:
            return this.convertValue(ApplicationControl.getPolicyPermission(13, true));
         case 7:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(5, 6, true));
         case 8:
            return this.convertValue(ApplicationControl.getPolicyPermission(21, true));
         case 9:
            return this.convertValue(ApplicationControl.getPolicyPermission(15, true));
         case 10:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(3, 4, true));
         case 11:
            return this.convertValue(ApplicationControl.getPolicyPermission(2, true));
         case 12:
            return this.convertValue(ApplicationControl.getPolicyPermission(16, true));
         case 13:
            return this.convertValue(ApplicationControl.getPolicyPermission(7, true));
         case 14:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(17, 18, true));
         case 15:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(8, 9, true));
         case 16:
            return this.convertValue(ApplicationControl.getPolicyPermission(11, true));
         case 17:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(25, 26, true));
         case 18:
            return this.convertValue(ApplicationControl.getPolicyPermission(19, true));
         case 19:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(27, 28, true));
         case 20:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(29, 30, true));
         case 21:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(31, 32, true));
      }
   }

   public final int getMaxAllowable(int permission, String domain) {
      switch (permission) {
         case 2:
            return this.convertValue(ApplicationControl.getPolicyPermission(domain, 12, true));
         case 7:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(domain, 5, 6, true));
         case 10:
            return this.convertValue(ApplicationControl.getPolicyPermissionTernary(domain, 3, 4, true));
         default:
            throw new Object();
      }
   }

   public final synchronized boolean invokePermissionsRequest(ApplicationPermissions requestedPermissions) {
      if (requestedPermissions == null) {
         throw new Object();
      }

      boolean permissionsSaved = false;
      ApplicationControlScreen appControlScreen = this.generateApplicationControlScreen(requestedPermissions);
      if (appControlScreen != null) {
         this.displayAppControlScreen(appControlScreen);
         permissionsSaved = appControlScreen.werePermissionsSavedEqualOrMorePermissive();
      }

      return permissionsSaved;
   }

   public final synchronized ApplicationPermissions invokePermissionsRequest(String displayName) {
      ApplicationPermissions permissions = null;
      ApplicationControlRequestScreen appControlScreen = this.generateApplicationControlRequestScreen(displayName);
      if (appControlScreen != null) {
         this.displayAppControlScreen(appControlScreen);
         permissions = appControlScreen.getRequestedPermissions();
      }

      return permissions;
   }

   private final synchronized void displayAppControlScreen(ApplicationControlScreen param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 03: astore 2
      // 04: aload 2
      // 05: ldc2_w 3353949624016666498
      // 08: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 0b: checkcast java/lang/Object
      // 0e: astore 3
      // 0f: aload 3
      // 10: ifnull 7a
      // 13: aload 1
      // 14: ifnull 7a
      // 17: bipush 51
      // 19: invokestatic net/rim/device/api/system/CodeSigningKey.getBuiltInKey (I)Lnet/rim/device/api/system/CodeSigningKey;
      // 1c: astore 4
      // 1e: aload 2
      // 1f: ldc2_w 5402085941883890214
      // 22: new java/lang/Object
      // 25: dup
      // 26: aload 1
      // 27: aload 4
      // 29: invokespecial net/rim/device/api/system/ControlledAccess.<init> (Ljava/lang/Object;Lnet/rim/device/api/system/CodeSigningKey;)V
      // 2c: invokevirtual net/rim/device/api/system/ApplicationRegistry.put (JLjava/lang/Object;)V
      // 2f: invokestatic net/rim/device/api/system/ApplicationManager.getApplicationManager ()Lnet/rim/device/api/system/ApplicationManager;
      // 32: aload 3
      // 33: invokevirtual net/rim/device/api/system/ApplicationManager.runApplication (Lnet/rim/device/api/system/ApplicationDescriptor;)I
      // 36: pop
      // 37: aload 0
      // 38: getfield net/rim/device/api/applicationcontrol/ApplicationPermissionsManager._closedSignal Ljava/lang/Object;
      // 3b: dup
      // 3c: astore 5
      // 3e: monitorenter
      // 3f: aload 0
      // 40: getfield net/rim/device/api/applicationcontrol/ApplicationPermissionsManager._closedSignal Ljava/lang/Object;
      // 43: invokevirtual java/lang/Object.wait ()V
      // 46: goto 4b
      // 49: astore 6
      // 4b: aload 5
      // 4d: monitorexit
      // 4e: goto 59
      // 51: astore 7
      // 53: aload 5
      // 55: monitorexit
      // 56: aload 7
      // 58: athrow
      // 59: aload 2
      // 5a: ldc2_w 5402085941883890214
      // 5d: invokevirtual net/rim/device/api/system/ApplicationRegistry.remove (J)Ljava/lang/Object;
      // 60: pop
      // 61: return
      // 62: astore 5
      // 64: aload 2
      // 65: ldc2_w 5402085941883890214
      // 68: invokevirtual net/rim/device/api/system/ApplicationRegistry.remove (J)Ljava/lang/Object;
      // 6b: pop
      // 6c: return
      // 6d: astore 8
      // 6f: aload 2
      // 70: ldc2_w 5402085941883890214
      // 73: invokevirtual net/rim/device/api/system/ApplicationRegistry.remove (J)Ljava/lang/Object;
      // 76: pop
      // 77: aload 8
      // 79: athrow
      // 7a: return
      // try (31 -> 34): 35 null
      // try (31 -> 38): 39 null
      // try (39 -> 42): 39 null
      // try (22 -> 44): 49 null
      // try (22 -> 44): 55 null
      // try (49 -> 50): 55 null
      // try (55 -> 56): 55 null
   }

   private final ApplicationControlRequestScreen generateApplicationControlRequestScreen(String appName) {
      ApplicationControlRequestScreen appControlScreen = (ApplicationControlRequestScreen)(new Object(appName));
      appControlScreen.setCloseListener(new ApplicationPermissionsManager$AppControlScreenClosedListener(this, null));
      return appControlScreen;
   }

   private final ApplicationControlScreen generateApplicationControlScreen(ApplicationPermissions requestedPermissions) {
      ApplicationControlScreen appControlScreen = null;
      Process currentProcess = Process.currentProcess();
      CodeModuleGroup cmg = null;

      label68:
      try {
         String processName = currentProcess.getModuleName();
         cmg = this.findCodeModuleGroup(processName);
      } finally {
         break label68;
      }

      if (cmg == null) {
         int processHandle = currentProcess.getModuleHandle();
         appControlScreen = (ApplicationControlScreen)(new Object(processHandle, requestedPermissions));
      } else {
         IntVector handles = (IntVector)(new Object());
         Enumeration modules = cmg.getModules();

         while (modules.hasMoreElements()) {
            int handle = CodeModuleManager.getModuleHandle((String)modules.nextElement());
            if (handle != 0
               && (
                  !ApplicationControl.isSignedWithRRI(handle)
                     || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(handle).startsWith("net_rim")
               )) {
               handles.addElement(handle);
            }
         }

         if (handles.size() < 1) {
            return null;
         }

         appControlScreen = (ApplicationControlScreen)(new Object(handles.toArray(), cmg.getFriendlyName(), requestedPermissions));
      }

      appControlScreen.setCloseListener(new ApplicationPermissionsManager$AppControlScreenClosedListener(this, null));
      return appControlScreen;
   }

   private final CodeModuleGroup findCodeModuleGroup(String moduleName) {
      CodeModuleGroup[] groups = CodeModuleGroupManager.loadAll();
      int numGroups = groups != null ? groups.length : 0;

      while (--numGroups >= 0) {
         CodeModuleGroup group = groups[numGroups];
         if (group.containsModule(moduleName)) {
            return group;
         }
      }

      return null;
   }

   private final int convertValue(int itPolicyValue) {
      switch (itPolicyValue) {
         case -1:
            throw new Object();
         case 0:
         default:
            return 999;
         case 1:
            return 997;
         case 2:
            return 998;
      }
   }

   private final int convertValue(boolean allow) {
      return allow ? 999 : 997;
   }
}
