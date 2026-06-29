package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLInputElementPrototype extends ESObject {
   public ESHTMLInputElementPrototype(ESElementPrototype nodeProto) {
      this.setPrototype(nodeProto);
      this.setGrowthIncrement(4);
      this.addHostFunction(new ESHTMLInputElementPrototype$1(this, Names.Input, "blur", 0));
      this.addHostFunction(new ESHTMLInputElementPrototype$2(this, Names.Input, "click", 0));
      this.addHostFunction(new ESHTMLInputElementPrototype$3(this, Names.Input, "select", 0));
      this.addHostFunction(new ESHTMLInputElementPrototype$4(this, Names.Input, "focus", 0));
   }
}
