package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;

public final class ESMessagePrototype extends ESObject {
   public ESMessagePrototype() {
      this.setGrowthIncrement(1);
      this.addHostFunction(new ESMessagePrototype$1(this, "MDSMessage", "send", 0));
   }
}
