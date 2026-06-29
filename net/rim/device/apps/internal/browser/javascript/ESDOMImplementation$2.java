package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.DOMImplementation;

class ESDOMImplementation$2 extends JavaScriptHostFunction {
   private final ESDOMImplementation this$0;

   ESDOMImplementation$2(ESDOMImplementation _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      DOMImplementation impl = this.this$0._doc.getImplementation();
      String qualifiedName = Convert.toString(this.getParm(0));
      String publicId = Convert.toString(this.getParm(1));
      String systemId = Convert.toString(this.getParm(2));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(impl.createDocumentType(qualifiedName, publicId, systemId));
      } catch (Throwable var7) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
