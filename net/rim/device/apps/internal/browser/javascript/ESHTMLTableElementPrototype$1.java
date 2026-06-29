package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLTableElement;

class ESHTMLTableElementPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLTableElementPrototype this$0;

   ESHTMLTableElementPrototype$1(ESHTMLTableElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLTableElement table = ((ESHTMLTableElement)this.getThis()).getTableElement();
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(table.createTHead());
   }
}
