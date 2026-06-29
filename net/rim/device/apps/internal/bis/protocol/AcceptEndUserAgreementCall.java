package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class AcceptEndUserAgreementCall implements XMLCall {
   private String _tcversion;
   private String _tclocale;

   AcceptEndUserAgreementCall(String tcversion, String tclocale) {
      this._tcversion = tcversion;
      this._tclocale = tclocale;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "user");
      XMLUtils.writeSimpleElement(ostream, "tcversion", this._tcversion);
      XMLUtils.writeSimpleElement(ostream, "tclocale", this._tclocale);
      XMLUtils.endElement(ostream, "user");
   }
}
