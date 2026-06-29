package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESNodeListPrototype extends ESObject {
   public ESNodeListPrototype() {
      this.addHostFunction(new ESNodeListPrototype$1(this, Names.Input, "item", 1));
   }
}
