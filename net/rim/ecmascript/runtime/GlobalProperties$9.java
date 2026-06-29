package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;

class GlobalProperties$9 extends HostFunction {
   GlobalProperties$9(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      String s = Convert.toString(this.getParm(0));
      Tokenizer t = new Tokenizer(s);
      int tok = t.getToken();
      boolean neg = false;
      switch (tok) {
         case 100:
            neg = true;
         case 132:
            tok = t.getToken();
         default:
            switch (tok) {
               case 201:
               default:
                  double d = t.tokenDouble();
                  return Value.makeDoubleValue(neg ? -d : d);
               case 202:
                  int i = t.tokenValue();
                  return Value.makeIntegerValue(neg ? -i : i);
               case 203:
                  String str = t.tokenString();
                  if (str.equals("Infinity")) {
                     if (neg) {
                        return Value.NEGATIVE_INFINITY;
                     }

                     return Value.POSITIVE_INFINITY;
                  }
               case 200:
                  return Value.NaN;
            }
      }
   }
}
