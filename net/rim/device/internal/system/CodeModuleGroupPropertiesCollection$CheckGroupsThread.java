package net.rim.device.internal.system;

import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;

final class CodeModuleGroupPropertiesCollection$CheckGroupsThread extends Thread {
   private boolean _shouldCheckAdded = false;
   private boolean _shouldCheckDeleted = false;

   private CodeModuleGroupPropertiesCollection$CheckGroupsThread() {
   }

   @Override
   public final void run() {
      boolean checkAdded;
      boolean checkDeleted;
      synchronized (CodeModuleGroupPropertiesCollection._lock) {
         checkAdded = this._shouldCheckAdded;
         this._shouldCheckAdded = false;
         checkDeleted = this._shouldCheckDeleted;
         this._shouldCheckDeleted = false;
      }

      boolean done;
      label69:
      do {
         done = true;

         try {
            CodeModuleGroup[] groups = CodeModuleGroupManager.loadAll();
            if (groups != null) {
               for (int i = 0; i < groups.length; i++) {
                  if (groups[i] == null || groups[i].getName() == null) {
                     done = false;
                     continue label69;
                  }
               }

               if (checkAdded) {
                  CodeModuleGroupPropertiesCollection.getInstance().checkAddedGroupInformation(groups);
               }

               if (checkDeleted) {
                  CodeModuleGroupPropertiesCollection.getInstance().checkDeletedGroupInformation(groups);
               }
            }

            synchronized (CodeModuleGroupPropertiesCollection._lock) {
               checkAdded = this._shouldCheckAdded;
               this._shouldCheckAdded = false;
               checkDeleted = this._shouldCheckDeleted;
               this._shouldCheckDeleted = false;
               done = !checkAdded && !checkDeleted;
            }
         } catch (Exception e) {
            done = false;
         }
      } while (!done);
   }

   public final void setFlag(long flag) {
      if (flag == 256826950193107649L) {
         this._shouldCheckAdded = true;
      } else {
         if (flag == -4232371946002803201L) {
            this._shouldCheckDeleted = true;
         }
      }
   }

   CodeModuleGroupPropertiesCollection$CheckGroupsThread(CodeModuleGroupPropertiesCollection$1 x0) {
      this();
   }
}
