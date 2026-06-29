package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLSelectElementPrototype extends ESObject {
   public ESHTMLSelectElementPrototype(ESElementPrototype elementProto) {
      this.setPrototype(elementProto);
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESHTMLSelectElementPrototype$1(this, Names.Select, "blur", 0));
      this.addHostFunction(new ESHTMLSelectElementPrototype$2(this, Names.Select, "focus", 0));
      this.addHostFunction(new ESHTMLSelectElementPrototype$3(this, Names.Select, "add", 2));
      this.addHostFunction(new ESHTMLSelectElementPrototype$4(this, Names.Select, "remove", 1));
   }
}
