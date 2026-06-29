package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;

class ESRegExpPrototype$1 extends HostFunction {
   private final ESRegExpPrototype this$0;

   ESRegExpPrototype$1(ESRegExpPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      GlobalObject global = this.getGlobalInstance();
      String s;
      if (this.getNumParms() == 0 && global.regExpInput != null) {
         s = global.regExpInput;
      } else {
         s = Convert.toString(this.getParm(0));
      }

      RegExp$MatchResult m = ESRegExpPrototype.doMatch("exec", this.getThis(), s);
      return ESRegExpPrototype.makeMatchArray(m, s);
   }
}
