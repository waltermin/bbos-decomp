package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Process;

final class TimerInvokerThread extends Thread {
   private Verb _onTimer;

   TimerInvokerThread(Verb onTimer) {
      this._onTimer = onTimer;
   }

   @Override
   public final void run() {
      Process.killProcessIfThisThreadDies(true);
      this._onTimer.invoke(new ContextObject(64));
   }
}
