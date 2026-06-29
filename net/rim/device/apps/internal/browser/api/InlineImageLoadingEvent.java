package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class InlineImageLoadingEvent extends Event {
   private boolean _fragments;

   public InlineImageLoadingEvent(Object src, boolean fragments) {
      super(5, src);
      this._fragments = fragments;
   }

   public final boolean isFragments() {
      return this._fragments;
   }
}
