package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESArrayPrototype$Join extends HostFunction {
   ESArrayPrototype$Join(int length) {
      super("Array", "join", length);
   }

   ESArrayPrototype$Join() {
      this(1);
   }

   @Override
   public long run() {
      boolean locale = this.getName() == "toLocaleString";
      long length = Convert.toUint32(this.getThis().getField("length"));
      long parm = this.getParm(0);
      String separator;
      if (Value.getType(parm) == 2) {
         separator = ",";
      } else {
         separator = Convert.toString(parm);
      }

      StringBuffer result = (StringBuffer)(new Object(Misc.separatedArraySize(length, separator.length())));
      if (length != 0) {
         result.append(Convert.toJoinString(this.getThis().getIndex(0), locale));

         for (long k = 1; k < length; k += 1) {
            result.append(separator);
            result.append(Convert.toJoinString(this.getThis().getIndex(k), locale));
         }
      }

      return Value.makeStringValue(result.toString());
   }
}
