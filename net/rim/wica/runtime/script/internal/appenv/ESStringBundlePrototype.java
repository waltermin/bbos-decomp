package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.RedirectedObject;

public final class ESStringBundlePrototype extends RedirectedObject {
   public ESStringBundlePrototype() {
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESStringBundlePrototype$1(this, "StringBundle", "getString", 1));
      this.addHostFunction(new ESStringBundlePrototype$2(this, "StringBundle", "getFormattedString", 2));
   }
}
