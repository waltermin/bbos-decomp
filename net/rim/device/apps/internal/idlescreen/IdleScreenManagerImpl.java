package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.idlescreen.IdleScreenManager;

final class IdleScreenManagerImpl extends IdleScreenManager {
   static final void register() {
      IdleScreenManager.register(new IdleScreenManagerImpl());
   }

   @Override
   public final void show() {
      IdleScreenApplication.show(false);
   }

   @Override
   public final void hook(Application hook) {
      IdleScreenApplication.hook(hook);
   }

   @Override
   public final void unhook() {
      IdleScreenApplication.unhook();
   }
}
