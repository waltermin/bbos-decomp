package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class AddSimpleAccountCall implements XMLCall, BISServiceConstants {
   private String _email;
   private String _password;
   private Boolean _aolAccountIntegrationPermitted;

   AddSimpleAccountCall(String email, String password, Boolean aolAccountIntegrationPermitted) {
      this._email = email;
      this._password = password;
      this._aolAccountIntegrationPermitted = aolAccountIntegrationPermitted;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "emailAccount");
      XMLUtils.writeSimpleElement(ostream, "hosted", "false");
      XMLUtils.writeSimpleElement(ostream, "email", this._email);
      XMLUtils.writeSimpleElement(ostream, "password", this._password);
      if (this._aolAccountIntegrationPermitted != null) {
         XMLUtils.writeSimpleElement(ostream, "aolIntegrationPermitted", this._aolAccountIntegrationPermitted.toString());
      }

      XMLUtils.endElement(ostream, "emailAccount");
   }
}
