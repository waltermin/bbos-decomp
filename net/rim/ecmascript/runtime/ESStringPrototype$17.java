package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;

class ESStringPrototype$17 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$17(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESRegExp regExp = Convert.toRegExp(this.getParm(0));
      String s = ESStringPrototype.toString(this.getThis());
      if (!regExp.getGlobal()) {
         return ESRegExpPrototype.makeMatchArray(ESRegExpPrototype.doMatch(regExp, s), s);
      }

      long lastIndex = Value.ZERO;
      ESArray a = new ESArray();
      int i = 0;

      while (true) {
         regExp.setLastIndex(lastIndex);
         RegExp$MatchResult m = ESRegExpPrototype.doMatch(regExp, s);
         if (m == null) {
            return Value.makeObjectValue(a);
         }

         long newLastIndex = regExp.getLastIndex();
         if (Expr.eq(lastIndex, newLastIndex)) {
            lastIndex = Expr.inc(lastIndex);
         } else {
            lastIndex = newLastIndex;
         }

         a.putIndex(i, Value.makeStringValue(m.captures[0]));
         i++;
      }
   }
}
