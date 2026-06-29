package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESDatePrototype$ESDateFetchFunction extends ESDatePrototype$ESDateFunction {
   ESDatePrototype$ESDateFetchFunction(String name, int length) {
      super(name, length);
   }

   ESDatePrototype$ESDateFetchFunction(String name) {
      this(name, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         super._date = (ESDate)this.getThis();
         double cce = super._date.getValue();
         if (Double.isNaN(cce)) {
            return Value.NaN;
         }

         super._t = (long)cce;
         var4 = false;
      } finally {
         if (var4) {
            throw ThrownValue.typeError(Resources.getString(50), this.getName());
         }
      }

      return this.fetch();
   }

   long fetch() {
      throw null;
   }
}
