package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class ESErrorPrototype$1 extends HostFunction {
   private final ESErrorPrototype this$0;

   ESErrorPrototype$1(ESErrorPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      boolean var5 = false /* VF: Semaphore variable */;

      ESError error;
      try {
         var5 = true;
         error = (ESError)this.getThis();
         var5 = false;
      } finally {
         if (var5) {
            throw ThrownValue.typeError(Resources.getString(50), "toString");
         }
      }

      String type = error._type;
      if (type == null) {
         type = "Error";
      }

      String message = error._message;
      if (error._message == null) {
         message = "";
      }

      return Value.makeStringValue(Misc.replace(Resources.getString(43), type, message));
   }
}
