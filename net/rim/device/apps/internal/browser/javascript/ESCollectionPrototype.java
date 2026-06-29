package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESCollectionPrototype extends ESObject {
   public ESCollectionPrototype() {
      this.addHostFunction(new ESCollectionPrototype$1(this, Names.Input, "item", 1));
      this.addHostFunction(new ESCollectionPrototype$2(this, Names.Input, "namedItem", 1));
   }
}
