package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Constructor;
import net.rim.ecmascript.runtime.Value;

final class ESHTMLImageElementPrototype$Constructor extends Constructor {
   ESHTMLImageElementPrototype$Constructor() {
      super(Names.Image, JavaScriptEngine.getInstance()._htmlImagePrototype);
   }

   @Override
   public final long run() {
      return Value.makeObjectValue(new ESHTMLImageElement());
   }
}
