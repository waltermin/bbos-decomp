package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.internal.browser.util.RunnableThread;

class BSMThread extends RunnableThread {
   public BSMThread() {
      this.setKillProcessIfThisThreadDies(false);
   }

   @Override
   protected void runItem(Object obj) {
      if (obj instanceof BSMRequest) {
         ((BSMRequest)obj).run();
      }
   }
}
