package net.rim.device.internal.system;

import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.internal.proxy.Proxy;

public final class CodeModuleDeletionListener {
   private CodeModuleDeletionListener() {
   }

   public static final void CodeModuleDeletionListenerMain() {
      CodeModuleGroupManager.deleteEmptyGroups();
      Proxy proxy = Proxy.getInstance();
      proxy.addGlobalEventListener(new CodeModuleDeletionListener$CodeModuleDeletionListenerInternal(null));
   }
}
