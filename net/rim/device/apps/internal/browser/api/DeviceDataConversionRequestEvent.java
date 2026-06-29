package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class DeviceDataConversionRequestEvent extends Event {
   public DeviceDataConversionRequestEvent(Object src) {
      super(2, src);
   }
}
