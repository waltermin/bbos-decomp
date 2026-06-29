package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;

class ESNamedNodeMapPrototype$3 extends JavaScriptHostFunction {
   private final ESNamedNodeMapPrototype this$0;

   ESNamedNodeMapPrototype$3(ESNamedNodeMapPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      String str = Convert.toString(this.getParm(0));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((ESNamedNodeMap)this.getThis()).getNamedNodeMap().removeNamedItem(str));
      } catch (Throwable var4) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
