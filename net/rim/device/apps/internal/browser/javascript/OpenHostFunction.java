package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

final class OpenHostFunction extends JavaScriptHostFunction {
   public OpenHostFunction() {
      super(Names.XMLHttpRequest, "open", 2);
   }

   @Override
   public final long run() {
      String method = Convert.toString(this.getParm(0));
      String uri = Convert.toString(this.getParm(1));
      int numParams = this.getNumParms();
      boolean aSync = true;
      String username = null;
      String pwd = null;
      if (numParams > 2) {
         aSync = Convert.toBoolean(this.getParm(2));
      }

      if (numParams > 3) {
         username = Convert.toString(this.getParm(3));
      }

      if (numParams > 4) {
         pwd = Convert.toString(this.getParm(4));
      }

      ((ESXMLHttpRequest)this.getThis()).open(method, uri, aSync, username, pwd);
      return Value.DEFAULT;
   }
}
