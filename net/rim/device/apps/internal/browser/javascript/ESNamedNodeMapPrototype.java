package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESNamedNodeMapPrototype extends ESObject {
   ESNamedNodeMapPrototype() {
      this.addHostFunction(new ESNamedNodeMapPrototype$1(this, Names.NamedNodeMap, "getNamedItem", 1));
      this.addHostFunction(new ESNamedNodeMapPrototype$2(this, Names.NamedNodeMap, "setNamedItem", 1));
      this.addHostFunction(new ESNamedNodeMapPrototype$3(this, Names.NamedNodeMap, "removeNamedItem", 1));
      this.addHostFunction(new ESNamedNodeMapPrototype$4(this, Names.NamedNodeMap, "item", 1));
      this.addHostFunction(new ESNamedNodeMapPrototype$5(this, Names.NamedNodeMap, "getNamedItemNS", 2));
      this.addHostFunction(new ESNamedNodeMapPrototype$6(this, Names.NamedNodeMap, "setNamedItemNS", 1));
      this.addHostFunction(new ESNamedNodeMapPrototype$7(this, Names.NamedNodeMap, "removeNamedItemNS", 2));
   }
}
