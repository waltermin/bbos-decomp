package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESCharacterDataPrototype$1 extends JavaScriptHostFunction {
   private final ESCharacterDataPrototype this$0;

   ESCharacterDataPrototype$1(ESCharacterDataPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      try {
         int offset = Convert.toInt32(this.getParm(0));
         int count = Convert.toInt32(this.getParm(1));
         return JavaScriptEngine.makeStringValue(((ESCharacterData)this.getThis()).getCharacterData().substringData(offset, count));
      } catch (Throwable var4) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
