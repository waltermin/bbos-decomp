package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.util.MultiMap;

public final class SDPRedirectEvent extends RedirectEvent {
   private MultiMap _values;

   public SDPRedirectEvent(Object src, String location, Event originalEvent, MultiMap values) {
      super(src, location, originalEvent, 4);
      this._values = values;
   }

   public final MultiMap getValueTable() {
      return this._values;
   }
}
