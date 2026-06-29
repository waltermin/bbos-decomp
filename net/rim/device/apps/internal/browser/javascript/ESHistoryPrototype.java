package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHistoryPrototype extends ESObject {
   public ESHistoryPrototype() {
      this.addHostFunction(new ESHistoryPrototype$1(this, Names.History, "back", 0));
      this.addHostFunction(new ESHistoryPrototype$2(this, Names.History, "forward", 0));
      this.addHostFunction(new ESHistoryPrototype$3(this, Names.History, "go", 1));
   }
}
