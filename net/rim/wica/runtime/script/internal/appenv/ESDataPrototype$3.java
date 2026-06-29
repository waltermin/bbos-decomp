package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;

class ESDataPrototype$3 extends HostFunction {
   private final ESDataPrototype this$0;

   ESDataPrototype$3(ESDataPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESData thiz = (ESData)this.getThis();
      if (!thiz.getCollection().isSupported("sendEmail")) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(103));
      }

      int numParams = this.getNumParms();
      if (numParams != 0) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(76, "email send()"));
      }

      int returnValue = this.this$0._context.getAccessServ().sendEmail(thiz.getHandle());
      String returnString = null;
      switch (returnValue) {
         case -1:
            break;
         case 0:
         default:
            returnString = "ERROR_OCCURED";
            break;
         case 1:
            returnString = "SENT";
            break;
         case 2:
            returnString = "SAVED_DRAFT";
      }

      return EcmaUtilities.makeStringValue(returnString);
   }
}
