package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESNavigation$1 extends HostFunction {
   private final ESNavigation this$0;

   ESNavigation$1(ESNavigation this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      int numParams = this.getNumParms();
      boolean saveChanges = false;
      if (numParams != 1 && numParams != 0) {
         EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(114, "Screen.close()"));
      }

      if (numParams == 1) {
         long ecmaIsCommit = this.getParm(0);
         if (Value.getType(ecmaIsCommit) == 4) {
            saveChanges = Value.getBooleanValue(ecmaIsCommit);
         }
      }

      this.this$0._context.getScreenManager().close(saveChanges);
      return Value.NULL;
   }
}
