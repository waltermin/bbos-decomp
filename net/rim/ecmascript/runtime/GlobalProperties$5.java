package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;

class GlobalProperties$5 extends HostFunction {
   GlobalProperties$5(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      String str = Convert.toString(this.getParm(0));
      int len = str.length();
      StringBuffer b = new StringBuffer();

      for (int i = 0; i < len; i++) {
         char c = str.charAt(i);
         if (c == '%') {
            label53:
            try {
               if (str.charAt(i + 1) == 'u') {
                  int dig1 = Tokenizer.hexValue(str.charAt(i + 2));
                  int dig2 = Tokenizer.hexValue(str.charAt(i + 3));
                  int dig3 = Tokenizer.hexValue(str.charAt(i + 4));
                  int dig4 = Tokenizer.hexValue(str.charAt(i + 5));
                  if (dig1 >= 0 && dig2 >= 0 && dig3 >= 0 && dig4 >= 0) {
                     i += 5;
                     c = (char)dig1;
                     c = (char)(c << 4);
                     c = (char)(c + dig2);
                     c = (char)(c << 4);
                     c = (char)(c + dig3);
                     c = (char)(c << 4);
                     c = (char)(c + dig4);
                  }
               } else {
                  int dig1 = Tokenizer.hexValue(str.charAt(i + 1));
                  int dig2 = Tokenizer.hexValue(str.charAt(i + 2));
                  if (dig1 >= 0 && dig2 >= 0) {
                     i += 2;
                     c = (char)dig1;
                     c = (char)(c << 4);
                     c = (char)(c + dig2);
                  }
               }
            } finally {
               break label53;
            }
         }

         b.append(c);
      }

      return Value.makeStringValue(b.toString());
   }
}
