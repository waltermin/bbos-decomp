package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.internal.browser.util.Pipe;

public final class DeviceDataConversionEvent extends Event {
   Pipe _pipe;
   String _contentType;
   String _encoding;

   public DeviceDataConversionEvent(Object src, Pipe pipe, String contentType, String encoding) {
      super(0, src);
      this._pipe = pipe;
      this._contentType = contentType;
      this._encoding = encoding;
   }

   public final Pipe getPipe() {
      return this._pipe;
   }

   public final String getContentType() {
      return this._contentType;
   }

   public final String getEncoding() {
      return this._encoding;
   }
}
