package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESNavigation$2 extends HostFunction {
   private final ESNavigation this$0;

   ESNavigation$2(ESNavigation this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      int numParams = this.getNumParms();
      if (numParams != 0) {
         EcmaUtilities.throwESError(this.getObjectClass(), RuntimeResources.getString(76, "Screen.refresh()"));
      }

      ScreenModel screen = this.this$0._context.getCurrentScreenModel();
      if (screen != null) {
         screen.invalidateUI(true);
      }

      return Value.NULL;
   }
}
