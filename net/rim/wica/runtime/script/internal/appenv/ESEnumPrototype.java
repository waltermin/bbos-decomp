package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;

public final class ESEnumPrototype extends ESObject {
   public ESEnumPrototype() {
      this.setGrowthIncrement(1);
      this.addHostFunction(new ESEnumPrototype$1(this, "Enumeration", "toString", 0));
   }
}
