package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

final class ESScreen {
   private ESScreen() {
   }

   public static final ESObject getScreen() {
      ESObject instance = (ESObject)(new Object());
      instance.setGrowthIncrement(8);
      instance.addField(Names.width, 5, Value.makeIntegerValue(Display.getWidth()));
      instance.addField(Names.height, 5, Value.makeIntegerValue(Display.getHeight()));
      instance.addField("availHeight", 5, Value.makeIntegerValue(Display.getHeight()));
      instance.addField("availWidth", 5, Value.makeIntegerValue(Display.getWidth()));
      instance.addField("availTop", 5, Value.makeIntegerValue(0));
      instance.addField("availLeft", 5, Value.makeIntegerValue(0));
      int colours = Graphics.getNumColors();

      int count;
      for (count = 0; count < 32 && (colours & 1) != 1; count++) {
         colours >>= 1;
      }

      instance.addField("colorDepth", 5, Value.makeIntegerValue(count));
      instance.addField("pixelDepth", 5, Value.makeIntegerValue(count));
      return instance;
   }
}
