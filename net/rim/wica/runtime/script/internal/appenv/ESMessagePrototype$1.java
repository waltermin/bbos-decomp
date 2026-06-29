package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Msg;

class ESMessagePrototype$1 extends HostFunction {
   private final ESMessagePrototype this$0;

   ESMessagePrototype$1(ESMessagePrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      int numParams = this.getNumParms();
      Msg message = ((ESMessage)this.getThis()).getMessage();
      if (numParams > 0) {
         message.send(Convert.toString(this.getParm(0)));
      } else {
         message.send();
      }

      return Value.NULL;
   }
}
