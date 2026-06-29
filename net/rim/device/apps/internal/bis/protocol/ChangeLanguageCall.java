package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class ChangeLanguageCall implements XMLCall {
   private String _language;
   private String _country;

   ChangeLanguageCall(String language, String country) {
      this._language = language;
      this._country = country;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "user");
      XMLUtils.writeSimpleElement(ostream, "language", this._language);
      if (null != this._country) {
         XMLUtils.writeSimpleElement(ostream, "country", this._country);
      }

      XMLUtils.endElement(ostream, "user");
   }
}
