package net.rim.wica.runtime.metadata.internal;

import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.ScriptMonitor;

class WicletRuntimeImpl$ScriptMonitorImpl implements ScriptMonitor {
   private final WicletRuntimeImpl this$0;
   private static final long DEFAULT_TIMEOUT;

   private WicletRuntimeImpl$ScriptMonitorImpl(WicletRuntimeImpl this$0) {
      this.this$0 = this$0;
   }

   @Override
   public long getTimeoutValue() {
      return 20000;
   }

   @Override
   public void scriptPreExecute() {
      this.this$0._eventService.dispatchEvent(this, 602);
   }

   @Override
   public void scriptPostExecute() {
      this.this$0._eventService.dispatchEvent(this, 603);
   }

   @Override
   public void scriptUnhandledError(String function, Exception e) {
      this.this$0._eventService.dispatchEvent(this, 605, 103, ((StringBuffer)(new Object())).append(function).append(": ").append(e.toString()).toString());
   }

   @Override
   public void scriptTimedOut() {
      if (this.this$0._uiService.displayModalDialog(2, RuntimeResources.getString(67)) == 2) {
         this.this$0.requestExclusiveTask(7, new WicletRuntimeImpl$StopRunnable(this.this$0, true));
         this.this$0._scriptEngine.stopExecution();
      }
   }

   WicletRuntimeImpl$ScriptMonitorImpl(WicletRuntimeImpl x0, WicletRuntimeImpl$1 x1) {
      this(x0);
   }
}
