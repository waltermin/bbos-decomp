package net.rim.wica.runtime.script.internal;

import net.rim.device.api.system.DeviceInfo;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.diagnostics.DebugServices;

class ESDebug$1 extends HostFunction {
   private final ESDebug this$0;

   ESDebug$1(ESDebug this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      if (DebugServices.isAttached()) {
         DebugServices.logConsole(this.this$0._context.getWiclet().getContext().getId(), this.this$0.constructMessage(this));
      } else if (DeviceInfo.isSimulator()) {
         System.out.println(this.this$0.constructMessage(this));
      }

      return Value.NULL;
   }
}
