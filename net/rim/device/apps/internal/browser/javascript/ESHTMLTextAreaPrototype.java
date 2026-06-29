package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLTextAreaPrototype extends ESObject {
   public ESHTMLTextAreaPrototype(ESElementPrototype nodeProto) {
      this.setPrototype(nodeProto);
      this.setGrowthIncrement(4);
      this.addHostFunction(new ESHTMLTextAreaPrototype$1(this, Names.HTMLTextAreaElement, "blur", 0));
      this.addHostFunction(new ESHTMLTextAreaPrototype$2(this, Names.HTMLTextAreaElement, "select", 0));
      this.addHostFunction(new ESHTMLTextAreaPrototype$3(this, Names.HTMLTextAreaElement, "focus", 0));
   }
}
