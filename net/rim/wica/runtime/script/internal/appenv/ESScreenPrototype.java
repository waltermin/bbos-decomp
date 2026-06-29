package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;

public final class ESScreenPrototype extends ESObject {
   private static final String Display;

   public ESScreenPrototype() {
      this.setGrowthIncrement(1);
      this.addHostFunction(new ESScreenPrototype$1(this, "MDSScreenElement", "display", 0));
   }
}
