package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class SimpleCall implements XMLCall {
   private String _element;
   private String _value;

   SimpleCall(String element, String value) {
      this._element = element;
      this._value = value;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.writeSimpleElement(ostream, this._element, this._value);
   }
}
