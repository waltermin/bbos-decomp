package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;

public final class ESMDSArrayPrototype extends ESObject {
   public ESMDSArrayPrototype() {
      this.setGrowthIncrement(5);
      this.addHostFunction(new ESMDSArrayPrototype$1(this, "MDSArray", "contains", 1));
      this.addHostFunction(new ESMDSArrayPrototype$2(this, "MDSArray", "remove", 1));
      this.addHostFunction(new ESMDSArrayPrototype$3(this, "MDSArray", "push", 1));
      this.addHostFunction(new ESMDSArrayPrototype$4(this, "MDSArray", "pop", 1));
      this.addHostFunction(new ESMDSArrayPrototype$5(this, "MDSArray", "toString", 0));
   }
}
