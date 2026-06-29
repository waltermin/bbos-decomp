package net.rim.device.api.system;

import java.util.Enumeration;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public class CodeModuleGroupManager {
   private CodeModuleGroupManager() {
   }

   private static void assertPermission() {
      ApplicationControl.assertCMMApiAllowed(true);
   }

   public static CodeModuleGroup load(String groupName) {
      return load(getGroupHandle(groupName));
   }

   public static CodeModuleGroup load(int groupHandle) {
      if (groupHandle == 0) {
         return null;
      }

      CodeModuleGroup group = new CodeModuleGroup(groupHandle);
      return group.load() ? group : null;
   }

   public static CodeModuleGroup[] loadAll() {
      int[] handles = getGroupHandles();
      if (handles == null) {
         return null;
      }

      int numGroups = handles.length;
      if (handles.length == 0) {
         return null;
      }

      CodeModuleGroup[] groups = new CodeModuleGroup[numGroups];

      for (int i = 0; i < numGroups; i++) {
         groups[i] = new CodeModuleGroup(handles[i]);

         try {
            groups[i].load();
         } catch (IllegalArgumentException var5) {
         } catch (Throwable var6) {
         }
      }

      return groups;
   }

   public static void deleteEmptyGroups() {
      assertPermission();
      CodeModuleGroup[] allGroups = loadAll();
      if (allGroups != null) {
         int numberOfGroups = allGroups.length;
         CodeModuleGroup currGroup = null;
         Enumeration modules = null;
         String moduleName = null;

         for (int i = 0; i < numberOfGroups; i++) {
            currGroup = allGroups[i];
            modules = currGroup.getModules();
            boolean moduleExists = false;

            while (modules.hasMoreElements()) {
               moduleName = (String)modules.nextElement();
               if (CodeModuleManager.getModuleHandle(moduleName) != 0) {
                  moduleExists = true;
                  break;
               }
            }

            if (!moduleExists) {
               currGroup.delete();
            }
         }
      }
   }

   public static native int getNumberOfGroups();

   public static native byte[] getGroupData(int var0);

   public static int createGroup(byte[] data) {
      assertPermission();
      return createGroupImpl(data);
   }

   private static native int createGroupImpl(byte[] var0);

   private static native int[] getGroupHandles();

   private static native int getGroupHandle(String var0);
}
