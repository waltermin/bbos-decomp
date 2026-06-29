package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class UIDirectionChangeEvent extends Event {
   private int _dir;
   public static final int DIR_LTR = 0;
   public static final int DIR_RTL = 1;

   public UIDirectionChangeEvent(int direction, Object src) {
      super(10012, src);
      this._dir = direction;
   }

   public final int getDirection() {
      return this._dir;
   }
}
