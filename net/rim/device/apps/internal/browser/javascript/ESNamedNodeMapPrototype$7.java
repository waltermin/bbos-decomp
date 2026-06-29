package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESNamedNodeMapPrototype$7 extends JavaScriptHostFunction {
   private final ESNamedNodeMapPrototype this$0;

   ESNamedNodeMapPrototype$7(ESNamedNodeMapPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      String namespaceURI = Convert.toString(this.getParm(0));
      String localName = Convert.toString(this.getParm(1));

      try {
         return JavaScriptEngine.getInstance()
            .lookupElementToESObjectLong(((ESNamedNodeMap)this.getThis()).getNamedNodeMap().removeNamedItemNS(namespaceURI, localName));
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
