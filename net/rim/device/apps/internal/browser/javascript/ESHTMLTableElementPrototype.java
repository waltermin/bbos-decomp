package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLTableElementPrototype extends ESObject {
   public ESHTMLTableElementPrototype(ESElementPrototype elementProto) {
      this.setPrototype(elementProto);
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESHTMLTableElementPrototype$1(this, Names.HTMLTableElement, "createTHead", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$2(this, Names.HTMLTableElement, "deleteTHead", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$3(this, Names.HTMLTableElement, "createTFoot", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$4(this, Names.HTMLTableElement, "deleteTFoot", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$5(this, Names.HTMLTableElement, "createCaption", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$6(this, Names.HTMLTableElement, "deleteCaption", 0));
      this.addHostFunction(new ESHTMLTableElementPrototype$7(this, Names.HTMLTableElement, "insertRow", 1));
      this.addHostFunction(new ESHTMLTableElementPrototype$8(this, Names.HTMLTableElement, "deleteRow", 1));
   }
}
