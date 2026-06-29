package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLTableSectionElementPrototype extends ESObject {
   public ESHTMLTableSectionElementPrototype(ESElementPrototype elementProto) {
      this.setPrototype(elementProto);
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESHTMLTableSectionElementPrototype$1(this, Names.HTMLTableSectionElement, "insertRow", 1));
      this.addHostFunction(new ESHTMLTableSectionElementPrototype$2(this, Names.HTMLTableSectionElement, "deleteRow", 1));
   }
}
