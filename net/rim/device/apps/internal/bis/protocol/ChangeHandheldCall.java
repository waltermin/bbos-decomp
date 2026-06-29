package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class ChangeHandheldCall implements XMLCall {
   private String _pin;
   private String _imei;

   ChangeHandheldCall(String pin, String imei) {
      this._pin = pin;
      this._imei = imei;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "device");
      XMLUtils.writeSimpleElement(ostream, "pin", this._pin);
      XMLUtils.writeSimpleElement(ostream, "imei", this._imei);
      XMLUtils.endElement(ostream, "device");
   }
}
