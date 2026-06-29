package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class LoadingImagesEvent extends Event {
   private int _state;
   public static final int STATE_INITIATE;
   public static final int STATE_DONE;

   public LoadingImagesEvent(Object src, int state) {
      super(1, src);
      this._state = state;
   }

   public final int getState() {
      return this._state;
   }
}
