package net.rim.device.internal.system;

import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.GlobalEventListener;

final class CodeModuleDeletionListener$CodeModuleDeletionListenerInternal implements GlobalEventListener {
   private CodeModuleDeletionListener$CodeModuleDeletionListenerInternal() {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4232371946002803201L) {
         CodeModuleGroupManager.deleteEmptyGroups();
      }
   }

   CodeModuleDeletionListener$CodeModuleDeletionListenerInternal(CodeModuleDeletionListener$1 x0) {
      this();
   }
}
