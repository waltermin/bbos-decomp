package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESNumberPrototype$4 extends HostFunction {
   private final ESNumberPrototype this$0;

   ESNumberPrototype$4(ESNumberPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      boolean var11 = false /* VF: Semaphore variable */;

      double d;
      try {
         var11 = true;
         d = ((ESNumber)this.getThis()).getValue();
         var11 = false;
      } finally {
         if (var11) {
            throw ThrownValue.typeError(Resources.getString(50), "valueOf");
         }
      }

      if (this.getNumParms() == 0) {
         return Value.makeStringValue(Convert.toString(Value.makeDoubleValue(d)));
      }

      int p = Convert.toInteger(this.getParm(0));
      if (p < 1) {
         throw ThrownValue.rangeError(Resources.getString(34), Integer.toString(p), "toPrecision");
      }

      StringBuffer b = (StringBuffer)(new Object());
      ParsedDouble fd = new ParsedDouble(d);
      if (fd.isNegative) {
         b.append('-');
      }

      if (fd.isExceptional) {
         b.append(fd.digits, 0, fd.nDigits);
         return Value.makeStringValue(b.toString());
      }

      fd.roundDigits(fd.nDigits, p);
      StringBuffer digits = (StringBuffer)(new Object());
      int e;
      if (d == 0L) {
         for (int i = 0; i < p; i++) {
            digits.append('0');
         }

         e = 0;
      } else {
         digits.append(fd.digits, 0, fd.nDigits);

         for (int i = fd.nDigits; i < p; i++) {
            digits.append('0');
         }

         e = fd.decExponent - 1;
      }

      char[] m = digits.toString().toCharArray();
      if (e < -6 || e >= p) {
         b.append(m[0]);
         if (p > 1) {
            b.append('.');
            b.append(m, 1, p - 1);
         }

         b.append('e');
         if (e < 0) {
            e = -e;
            b.append('-');
         } else {
            b.append('+');
         }

         b.append(Integer.toString(e));
      } else if (e == p - 1) {
         b.append(m);
      } else if (e >= 0) {
         b.append(m, 0, e + 1);
         b.append('.');
         b.append(m, e + 1, p - (e + 1));
      } else {
         b.append("0.");

         for (int i = 0; i < -(e + 1); i++) {
            b.append('0');
         }

         b.append(m);
      }

      return Value.makeStringValue(b.toString());
   }
}
