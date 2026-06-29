package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLAnchorElement;

class ESHTMLAnchorElementPrototype$3 extends JavaScriptHostFunction {
   private final ESHTMLAnchorElementPrototype this$0;

   ESHTMLAnchorElementPrototype$3(ESHTMLAnchorElementPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLAnchorElement input = ((ESHTMLAnchorElement)this.getThis()).getAnchorElement();
      return JavaScriptEngine.makeStringValue(input.getHref());
   }
}
