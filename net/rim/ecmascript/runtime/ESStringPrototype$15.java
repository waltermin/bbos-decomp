package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;

class ESStringPrototype$15 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$15(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      boolean isVersion12 = this.getVersion() == 120;
      boolean omitDelimiters = false;
      String s = ESStringPrototype.toString(this.getThis());
      ESArray a = new ESArray();
      long aValue = Value.makeObjectValue(a);
      long limitValue = this.getParm(1);
      long limit;
      if (Value.getType(limitValue) == 2) {
         limit = Long.MAX_VALUE;
      } else {
         limit = Convert.toUint32(limitValue);
      }

      int length = s.length();
      int p = 0;
      long separatorValue = this.getParm(0);
      ESObject obj = Value.checkIfObjectValue(separatorValue);
      ESRegExp regExp = null;
      String regExpString = null;
      if (obj != null && obj instanceof ESRegExp) {
         regExp = (ESRegExp)obj;
      } else {
         regExpString = Convert.toString(separatorValue);
         if (isVersion12 && regExpString.length() == 1 && regExpString.charAt(0) == ' ') {
            regExpString = null;
            regExp = new ESRegExp(
               "[\\u1680\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200A\\u200B\\u202F\\u3000\\u000d\\u000a\\u2028\\u2029\\u0009\\u000b\\u000c\\u0020\\u00a0]+"
            );
            omitDelimiters = true;
         }
      }

      if (isVersion12 && length == 0 && regExp == null) {
         return aValue;
      }

      if (limit == 0) {
         return aValue;
      }

      if (this.getNumParms() == 0) {
         a.append(Value.makeStringValue(s));
         return aValue;
      }

      if (length == 0) {
         RegExp$MatchResult z = ESStringPrototype.splitMatch(regExp, regExpString, s, 0);
         if (z != null) {
            return aValue;
         }

         a.append(Value.makeStringValue(s));
         return aValue;
      } else {
         int q = p;

         while (q != length) {
            RegExp$MatchResult z = ESStringPrototype.splitMatch(regExp, regExpString, s, q);
            if (z != null && z.endIndex != p) {
               if (!omitDelimiters || p != 0 || q != 0) {
                  a.append(Value.makeStringValue(s.substring(p, q)));
                  if (a.getArrayLength() == limit) {
                     return aValue;
                  }
               }

               p = z.endIndex;

               for (int i = 1; i < z.captures.length; i++) {
                  String str = z.captures[i];
                  if (str == null) {
                     a.append(Value.UNDEFINED);
                  } else {
                     a.append(Value.makeStringValue(str));
                  }

                  if (a.getArrayLength() == limit) {
                     return aValue;
                  }
               }

               q = p;
            } else {
               q++;
            }
         }

         if ((!isVersion12 || p != q || limit != Long.MAX_VALUE) && (!omitDelimiters || p != length)) {
            a.append(Value.makeStringValue(s.substring(p, length)));
         }

         return aValue;
      }
   }
}
