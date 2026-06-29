package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.internal.browser.util.RunnableThread;

final class WorkerThread extends RunnableThread {
   public WorkerThread() {
   }

   @Override
   public final void runItem(Object obj) {
      if (obj instanceof Object) {
         ((Runnable)obj).run();
      }
   }
}
