package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESFunctionPrototype extends HostFunction {
   @Override
   public long run() {
      return Value.UNDEFINED;
   }

   ESFunctionPrototype() {
      super("Function", "Function", 0, false, true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static long callFunction(GlobalObject global, HostFunction hf, long[] callParms) throws ThrownValue {
      boolean var8 = false /* VF: Semaphore variable */;

      ESFunction func;
      try {
         var8 = true;
         func = (ESFunction)hf.getThis();
         var8 = false;
      } finally {
         if (var8) {
            throw ThrownValue.typeError(Resources.getString(50), "call");
         }
      }

      long thisValue = hf.getParm(0);
      ESObject thiz;
      switch (Value.getType(thisValue)) {
         case 1:
            thiz = Convert.toObject(thisValue);
            break;
         case 2:
         case 3:
         default:
            thiz = global;
      }

      return Context.callFunction(func, thiz, callParms, 0, callParms.length, false);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().functionConstructor));
      this.addHostFunction(new ESFunctionPrototype$1(this, "Function", "toString"));
      this.addHostFunction(new ESFunctionPrototype$2(this, "Function", "call", 1));
      this.addHostFunction(new ESFunctionPrototype$3(this, "Function", "apply", 2));
   }
}
