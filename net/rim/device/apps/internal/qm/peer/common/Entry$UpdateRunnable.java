package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class Entry$UpdateRunnable implements Runnable {
   private final Entry this$0;

   Entry$UpdateRunnable(Entry _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      RibbonLauncher.getInstance().updateRegisteredAction(this.this$0.getUid());
   }
}
