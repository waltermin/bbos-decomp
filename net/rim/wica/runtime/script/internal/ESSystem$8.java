package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ManagementService;

class ESSystem$8 extends HostFunction {
   private final ESSystem this$0;

   ESSystem$8(ESSystem this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      long agId = ((Wiclet)this.this$0._context.getWiclet().getContext()).getAgId();
      ManagementService management = (ManagementService)this.this$0
         ._context
         .getRuntime()
         .getService(
            ESSystem.class$net$rim$wica$runtime$management$ManagementService == null
               ? (ESSystem.class$net$rim$wica$runtime$management$ManagementService = ESSystem.class$("net.rim.wica.runtime.management.ManagementService"))
               : ESSystem.class$net$rim$wica$runtime$management$ManagementService
         );
      AGInfo info = management.getAGInfo(agId);
      return EcmaUtilities.makeStringValue(info.getSPList());
   }
}
