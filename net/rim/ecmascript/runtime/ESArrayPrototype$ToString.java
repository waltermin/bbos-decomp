package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class ESArrayPrototype$ToString extends ESArrayPrototype$Join {
   ESArrayPrototype$ToString(String name) {
      super(0);
      super._name = name;
   }

   @Override
   public long run() throws ThrownValue {
      if (!(this.getThis() instanceof ESArray)) {
         throw ThrownValue.typeError(Resources.getString(50), "toString");
      }

      if (this.getVersion() != 120) {
         return super.run();
      }

      ESArray a = (ESArray)this.getThis();
      long length = Convert.toUint32(this.getThis().getField("length"));
      String separator = ", ";
      StringBuffer b = (StringBuffer)(new Object(Misc.separatedArraySize(length, separator.length())));
      b.append('[');
      boolean missingFinalElement = false;

      for (long l = 0; l < length; l += 1) {
         if (l > 0) {
            b.append(separator);
         }

         if (!a.hasOwnIndex(l)) {
            missingFinalElement = true;
         } else {
            long value = a.getIndex(l);
            if (Value.getType(value) == 2) {
               missingFinalElement = true;
            } else {
               missingFinalElement = false;
               b.append(Convert.toJoinString(a.getIndex(l), false));
            }
         }
      }

      if (missingFinalElement) {
         b.append(separator);
      }

      b.append(']');
      return Value.makeStringValue(b.toString());
   }
}
