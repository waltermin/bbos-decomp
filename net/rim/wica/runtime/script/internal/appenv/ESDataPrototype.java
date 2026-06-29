package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class ESDataPrototype extends ESObject {
   private WicaAppContext _context;

   public ESDataPrototype(WicaAppContext context) {
      this._context = context;
      this.setGrowthIncrement(4);
      this.addHostFunction(new ESDataPrototype$1(this, "MDSData", "remove", 0));
      this.addHostFunction(new ESDataPrototype$2(this, "MDSData", "clone", 0));
      this.addHostFunction(new ESDataPrototype$3(this, "MDSData", "send", 0));
      this.addHostFunction(new ESDataPrototype$4(this, "MDSData", "toString", 0));
   }
}
