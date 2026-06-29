package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.HostFunction;

final class ESDebug extends ESObject {
   private WicaAppContext _context;

   ESDebug(WicaAppContext context) {
      this._context = context;
      this.setGrowthIncrement(1);
      this.addHostFunction(new ESDebug$1(this, "Debug", "write", 1));
   }

   private final String constructMessage(HostFunction function) {
      int count = function.getNumParms();
      StringBuffer out = new StringBuffer();

      for (int i = 0; i < count; i++) {
         out.append(Convert.toString(function.getParm(i)));
      }

      return out.toString();
   }
}
