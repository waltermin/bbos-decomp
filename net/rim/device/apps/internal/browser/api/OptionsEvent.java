package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public class OptionsEvent extends Event {
   private int _type;
   public static final int TYPE_DONT_PROMPT_ENABLE_JAVASCRIPT;
   public static final int TYPE_TURN_ON_JAVASCRIPT;

   public OptionsEvent(Object src, int type) {
      super(6, src);
      this._type = type;
   }

   public int getType() {
      return this._type;
   }
}
