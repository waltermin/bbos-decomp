package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

final class ESHistory extends ESObject {
   public ESHistory(JavaScriptEngine engine) {
      super("History", engine._historyPrototype);
      this.setGrowthIncrement(7);
      this.addNode(Names.current, "");
      this.addNode(Names.next, "");
      this.addNode(Names.previous, "");

      try {
         this.putIndex(Value.makeIntegerValue(0), JavaScriptEngine.makeStringValue(""));
         this.putIndex(Value.makeIntegerValue(1), JavaScriptEngine.makeStringValue(""));
         this.putIndex(Value.makeIntegerValue(2), JavaScriptEngine.makeStringValue(""));
         this.addField(Names.length, 5, Value.makeIntegerValue(3));
      } finally {
         return;
      }
   }

   private final void addNode(String name, String strValue) {
      long value = Value.NULL;
      if (strValue != null) {
         value = JavaScriptEngine.makeStringValue(strValue);
      }

      this.addField(name, 5, value);
   }
}
