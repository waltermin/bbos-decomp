package net.rim.wica.runtime.script.internal.appenv;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESStringBundlePrototype$2 extends HostFunction {
   private final ESStringBundlePrototype this$0;

   ESStringBundlePrototype$2(ESStringBundlePrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESStringBundle thiz = (ESStringBundle)this.getThis();
      String key = Convert.toString(this.getParm(0));
      if (key == null) {
         return Value.DEFAULT;
      }

      int length = this.getNumParms();
      String[] args = new Object[length - 1];

      for (int i = 1; i < length; i++) {
         args[i - 1] = Convert.toString(this.getParm(i));
      }

      String format = thiz.getString(key);
      return format == null ? Value.NULL : Value.makeStringValue(MessageFormat.format(format, args));
   }
}
