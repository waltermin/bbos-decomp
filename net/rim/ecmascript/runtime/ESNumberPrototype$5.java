package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESNumberPrototype$5 extends HostFunction {
   private final ESNumberPrototype this$0;

   ESNumberPrototype$5(ESNumberPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      boolean var12 = false /* VF: Semaphore variable */;

      double d;
      try {
         var12 = true;
         d = ((ESNumber)this.getThis()).getValue();
         var12 = false;
      } finally {
         if (var12) {
            throw ThrownValue.typeError(Resources.getString(50), "valueOf");
         }
      }

      int f = Convert.toInteger(this.getParm(0));
      if (f < 0) {
         throw ThrownValue.rangeError(Resources.getString(34), Integer.toString(f), "toExponential");
      }

      f++;
      boolean fractionSpecified = this.getNumParms() != 0;
      ParsedDouble fd = new ParsedDouble(d);
      int n = fd.decExponent;
      int k = fd.nDigits;
      StringBuffer b = (StringBuffer)(new Object());
      if (fd.isNegative) {
         b.append('-');
      }

      if (fd.isExceptional) {
         b.append(fd.digits, 0, k);
         return Value.makeStringValue(b.toString());
      }

      if (k == 0) {
         k = 1;
      }

      if (fractionSpecified) {
         boolean haveDot = false;
         if (fd.roundDigits(k, f)) {
            n++;
            k = 1;
         }

         b.append(fd.digits[0]);
         if (k > f) {
            k = f;
         }

         if (k > 1) {
            haveDot = true;
            b.append('.');
            b.append(fd.digits, 1, k - 1);
         }

         for (int i = k; i < f; i++) {
            if (!haveDot) {
               b.append('.');
               haveDot = true;
            }

            b.append('0');
         }
      } else {
         if (fd.roundDigits(k, 17)) {
            n++;
            k = 1;
         }

         b.append(fd.digits[0]);
         if (k > 1) {
            b.append('.');
            b.append(fd.digits, 1, k - 1);
         }
      }

      b.append('e');
      if (--n >= 0) {
         b.append('+');
      } else {
         b.append('-');
      }

      b.append(Integer.toString(n < 0 ? -n : n));
      return Value.makeStringValue(b.toString());
   }
}
