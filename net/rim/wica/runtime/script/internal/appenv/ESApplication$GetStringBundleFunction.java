package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

final class ESApplication$GetStringBundleFunction extends HostFunction {
   private final ESApplication this$0;

   ESApplication$GetStringBundleFunction(ESApplication this$0) {
      super("application", "getStringBundle", 1);
      this.this$0 = this$0;
   }

   @Override
   public final long run() {
      return Value.makeObjectValue(new ESStringBundle(Convert.toString(this.getParm(0)), this.this$0._context));
   }
}
