package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Constructor;
import net.rim.ecmascript.runtime.Value;

final class ESHTMLOptionElementPrototype$Constructor extends Constructor {
   ESHTMLOptionElementPrototype$Constructor() {
      super(Names.Option, JavaScriptEngine.getInstance()._optionPrototype);
   }

   @Override
   public final long run() {
      long text = this.getParm(0, Value.UNDEFINED);
      long value = this.getParm(1, Value.UNDEFINED);
      long defaultSelected = this.getParm(2, Value.FALSE);
      long selected = this.getParm(3, Value.FALSE);
      return Value.makeObjectValue(new ESHTMLOptionElement(text, value, defaultSelected, selected));
   }
}
