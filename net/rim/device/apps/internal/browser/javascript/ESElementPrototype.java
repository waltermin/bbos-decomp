package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESElementPrototype extends ESObject {
   ESElementPrototype(ESNodePrototype nodeProto) {
      this.setPrototype(nodeProto);
      this.addHostFunction(new ESElementPrototype$1(this, Names.HTMLElement, "getAttribute", 1));
      this.addHostFunction(new ESElementPrototype$2(this, Names.HTMLElement, "setAttribute", 2));
      this.addHostFunction(new ESElementPrototype$3(this, Names.HTMLElement, "removeAttribute", 1));
      this.addHostFunction(new ESElementPrototype$4(this, Names.HTMLElement, "getAttributeNode", 1));
      this.addHostFunction(new ESElementPrototype$5(this, Names.HTMLElement, "setAttributeNode", 1));
      this.addHostFunction(new ESElementPrototype$6(this, Names.HTMLElement, "removeAttributeNode", 1));
      this.addHostFunction(new ESElementPrototype$7(this, Names.HTMLElement, "getElementsByTagName", 1));
      this.addHostFunction(new ESElementPrototype$8(this, Names.HTMLElement, "getAttributeNS", 2));
      this.addHostFunction(new ESElementPrototype$9(this, Names.HTMLElement, "setAttributeNS", 3));
      this.addHostFunction(new ESElementPrototype$10(this, Names.HTMLElement, "removeAttributeNS", 2));
      this.addHostFunction(new ESElementPrototype$11(this, Names.HTMLElement, "getAttributeNodeNS", 2));
      this.addHostFunction(new ESElementPrototype$12(this, Names.HTMLElement, "setAttributeNodeNS", 1));
      this.addHostFunction(new ESElementPrototype$13(this, Names.HTMLElement, "getElementsByTagNameNS", 2));
      this.addHostFunction(new ESElementPrototype$14(this, Names.HTMLElement, "hasAttribute", 1));
      this.addHostFunction(new ESElementPrototype$15(this, Names.HTMLElement, "hasAttributeNS", 2));
   }
}
