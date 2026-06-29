package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;

final class ESLogger extends ESObject {
   private static final String LevelDebug = "DEBUG";
   private static final String LevelError = "ERROR";
   private static final String LevelInformation = "INFORMATION";
   private static final String LevelWarning = "WARNING";
   private static final String[] Levels = new String[]{"DEBUG", "ERROR", "INFORMATION", "WARNING"};

   ESLogger() {
      super("Logger", GlobalObject.getInstance().getObjectPrototype());
      int length = Levels.length;
      this.setGrowthIncrement(length + 1);

      for (int i = 0; i < length; i++) {
         this.addField(Levels[i], 5, Value.makeStringValue(Levels[i]));
      }

      this.addHostFunction(new ESLogger$1(this, "Logger", "log", 3));
   }

   private final int getLevelByName(String name) {
      if (name == "ERROR") {
         return 2;
      } else if (name == "WARNING") {
         return 3;
      } else {
         return name == "DEBUG" ? 5 : 4;
      }
   }
}
