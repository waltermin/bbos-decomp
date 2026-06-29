package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESNumberPrototype$3 extends HostFunction {
   private final ESNumberPrototype this$0;

   ESNumberPrototype$3(ESNumberPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      boolean var10 = false /* VF: Semaphore variable */;

      double d;
      try {
         var10 = true;
         d = ((ESNumber)this.getThis()).getValue();
         var10 = false;
      } finally {
         if (var10) {
            throw ThrownValue.typeError(Resources.getString(50), "valueOf");
         }
      }

      int f = Convert.toInteger(this.getParm(0));
      if (f < 0) {
         throw ThrownValue.rangeError(Resources.getString(34), Integer.toString(f), "toFixed");
      }

      StringBuffer b = new StringBuffer();
      ParsedDouble fd = new ParsedDouble(d);
      if (fd.isNegative) {
         b.append('-');
      }

      if (fd.isExceptional) {
         b.append(fd.digits, 0, fd.nDigits);
         return Value.makeStringValue(b.toString());
      }

      if (d > 4921056587992461136L) {
         return Value.makeStringValue(Convert.toString(Value.makeDoubleValue(d)));
      }

      int n = fd.decExponent;
      int k = fd.nDigits;

      while (true) {
         if (n < 0) {
            if (f + n <= 0 || !fd.roundDigits(fd.nDigits, f + n)) {
               b.append('0');
               if (f != 0) {
                  b.append('.');

                  for (int i = 0; i < -n && i < f; i++) {
                     b.append(0);
                  }

                  f += n;

                  for (int i = 0; i < k && i < f; i++) {
                     b.append(fd.digits[i]);
                  }
               }
               break;
            }

            n++;
            k = 1;
         } else {
            if (n >= k) {
               b.append(fd.digits, 0, k);

               for (int i = 0; i < n - k; i++) {
                  b.append('0');
               }

               if (f != 0) {
                  b.append('.');

                  for (int i = 0; i < f; i++) {
                     b.append('0');
                  }
               }
               break;
            }

            if (k - n < f) {
               if (n == 0) {
                  b.append('0');
               } else {
                  b.append(fd.digits, 0, n);
               }

               if (f != 0) {
                  b.append('.');
                  b.append(fd.digits, n, k - n);

                  for (int i = 0; i < f - (k - n); i++) {
                     b.append('0');
                  }
               }
               break;
            }

            if (!fd.roundDigits(fd.nDigits, f + n)) {
               if (n == 0) {
                  b.append('0');
               } else {
                  b.append(fd.digits, 0, n);
               }

               if (f != 0) {
                  b.append('.');
                  b.append(fd.digits, n, f);
               }
               break;
            }

            n++;
            k = 1;
         }
      }

      return Value.makeStringValue(b.toString());
   }
}
