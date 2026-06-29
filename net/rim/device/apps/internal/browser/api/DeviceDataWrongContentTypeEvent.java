package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class DeviceDataWrongContentTypeEvent extends Event {
   private String _newMIMEType;

   public DeviceDataWrongContentTypeEvent(Object src, String newMIMEType) {
      super(9, src);
      this._newMIMEType = newMIMEType;
   }

   public final String getNewMIMEType() {
      return this._newMIMEType;
   }
}
