package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLAnchorElementPrototype extends ESObject {
   public ESHTMLAnchorElementPrototype(ESElementPrototype nodeProto) {
      this.setPrototype(nodeProto);
      this.setGrowthIncrement(3);
      this.addHostFunction(new ESHTMLAnchorElementPrototype$1(this, Names.HTMLAnchorElement, "blur", 0));
      this.addHostFunction(new ESHTMLAnchorElementPrototype$2(this, Names.HTMLAnchorElement, "focus", 0));
      this.addHostFunction(new ESHTMLAnchorElementPrototype$3(this, Names.HTMLAnchorElement, Names.toString));
   }
}
