package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESRegExpPrototype$4 extends HostFunction {
   private final ESRegExpPrototype this$0;

   ESRegExpPrototype$4(ESRegExpPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      boolean var10 = false /* VF: Semaphore variable */;

      ESRegExp thiz;
      try {
         var10 = true;
         thiz = (ESRegExp)this.getThis();
         var10 = false;
      } finally {
         if (var10) {
            throw ThrownValue.typeError(Resources.getString(50), "toString");
         }
      }

      String pattern = "";
      long parmPattern = this.getParm(0);
      if (Value.getType(parmPattern) != 2) {
         pattern = Convert.toString(this.getParm(0));
      }

      String flags = "";
      long parmFlags = this.getParm(1);
      if (Value.getType(parmFlags) != 2) {
         flags = Convert.toString(this.getParm(1));
      }

      ESRegExpPrototype$FlagParser fp = new ESRegExpPrototype$FlagParser();
      fp.parse(flags);
      thiz.compile(pattern, fp.global, fp.ignoreCase, fp.multiLine);
      return Value.makeObjectValue(thiz);
   }
}
