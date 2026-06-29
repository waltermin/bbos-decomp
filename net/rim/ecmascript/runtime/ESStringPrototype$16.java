package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;

class ESStringPrototype$16 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$16(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESRegExp regExp = Convert.toRegExp(this.getParm(0));
      String s = ESStringPrototype.toString(this.getThis());
      RegExp$MatchResult m = regExp.match(s, 0);
      return m == null ? Value.MINUS_ONE : Value.makeIntegerValue(m.startIndex);
   }
}
