package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESRegExpPrototype$3 extends HostFunction {
   private final ESRegExpPrototype this$0;

   ESRegExpPrototype$3(ESRegExpPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      boolean var8 = false /* VF: Semaphore variable */;

      ESRegExp thiz;
      try {
         var8 = true;
         thiz = (ESRegExp)this.getThis();
         var8 = false;
      } finally {
         if (var8) {
            throw ThrownValue.typeError(Resources.getString(50), "toString");
         }
      }

      StringBuffer b = (StringBuffer)(new Object());
      b.append('/');
      String pattern = thiz.getPattern();
      int length = pattern.length();

      for (int i = 0; i < length; i++) {
         char ch = pattern.charAt(i);
         if (ch == '/') {
            b.append('\\');
         }

         b.append(ch);
      }

      b.append('/');
      if (thiz.getGlobal()) {
         b.append('g');
      }

      if (thiz.getIgnoreCase()) {
         b.append('i');
      }

      if (thiz.getMultiLine()) {
         b.append('m');
      }

      return Value.makeStringValue(b.toString());
   }
}
