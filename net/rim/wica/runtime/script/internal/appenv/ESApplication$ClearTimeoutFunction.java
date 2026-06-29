package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

final class ESApplication$ClearTimeoutFunction extends HostFunction {
   private final ESApplication this$0;

   ESApplication$ClearTimeoutFunction(ESApplication this$0) {
      super("application", "clearTimeout", 1);
      this.this$0 = this$0;
   }

   @Override
   public final long run() {
      this.this$0._engine.getTimer().clearTimeout(Convert.toInt32(this.getParm(0)));
      return Value.FALSE;
   }
}
