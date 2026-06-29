package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLTableRowElementPrototype extends ESObject {
   public ESHTMLTableRowElementPrototype(ESElementPrototype elementProto) {
      this.setPrototype(elementProto);
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESHTMLTableRowElementPrototype$1(this, Names.HTMLTableRowElement, "insertCell", 1));
      this.addHostFunction(new ESHTMLTableRowElementPrototype$2(this, Names.HTMLTableRowElement, "deleteCell", 1));
   }
}
