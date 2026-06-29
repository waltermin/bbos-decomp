package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLFormElementPrototype extends ESObject {
   public ESHTMLFormElementPrototype(ESElementPrototype elementPrototype) {
      this.setPrototype(elementPrototype);
      this.addHostFunction(new ESHTMLFormElementPrototype$1(this, Names.Form, "submit", 0));
      this.addHostFunction(new ESHTMLFormElementPrototype$2(this, Names.Form, "reset", 0));
   }
}
