package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESPopupScreen$1 extends HostFunction {
   private final WicaAppContext val$context;
   private final ESPopupScreen this$0;

   ESPopupScreen$1(ESPopupScreen this$0, String x0, String x1, int x2, WicaAppContext val$context) {
      super(x0, x1, x2);
      this.this$0 = this$0;
      this.val$context = val$context;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      int numParams = this.getNumParms();
      String message = null;
      int type = -1;
      if (numParams > 0) {
         label46:
         try {
            message = Convert.toString(this.getParm(0));
            if (numParams > 1) {
               type = Convert.toInt32(this.getParm(1));
            }
         } finally {
            break label46;
         }
      }

      this.val$context.getEngine().synchronousTaskStarted();
      boolean var9 = false /* VF: Semaphore variable */;

      long var4;
      try {
         var9 = true;
         var4 = Value.makeIntegerValue(this.this$0._context.displayDlg(message, type));
         var9 = false;
      } finally {
         if (var9) {
            this.val$context.getEngine().synchronousTaskCompleted();
         }
      }

      this.val$context.getEngine().synchronousTaskCompleted();
      return var4;
   }
}
