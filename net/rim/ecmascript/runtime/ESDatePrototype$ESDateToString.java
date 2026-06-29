package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESDatePrototype$ESDateToString extends ESDatePrototype$ESDateFunction {
   ESDatePrototype$ESDateToString(String name) {
      super(name, 0);
   }

   @Override
   public long run() throws ThrownValue {
      try {
         super._date = (ESDate)this.getThis();
         double value = super._date.getValue();
         return Double.isNaN(value) ? Value.makeStringValue(Resources.getString(51)) : Value.makeStringValue(this.makeString((long)super._date.getValue()));
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), this.getName());
      }
   }

   String makeString(long _1) {
      throw null;
   }
}
