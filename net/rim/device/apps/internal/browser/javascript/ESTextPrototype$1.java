package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.Text;

class ESTextPrototype$1 extends JavaScriptHostFunction {
   private final ESTextPrototype this$0;

   ESTextPrototype$1(ESTextPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      try {
         int index = Convert.toInt32(this.getParm(0));
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((Text)((ESText)this.getThis()).getNode()).splitText(index));
      } catch (Throwable var3) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
