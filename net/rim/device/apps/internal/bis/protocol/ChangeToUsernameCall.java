package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class ChangeToUsernameCall implements XMLCall, BISServiceConstants {
   private String _userName;
   private String _password;
   private String _failedIntegrationType;
   private String _failedIntegrationEmail;

   ChangeToUsernameCall(String userName, String password, String failedIntegrationType, String failedIntegrationEmail) {
      this._userName = userName;
      this._password = password;
      this._failedIntegrationType = failedIntegrationType;
      this._failedIntegrationEmail = failedIntegrationEmail;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "user");
      XMLUtils.writeSimpleElement(ostream, "username", this._userName);
      XMLUtils.writeSimpleElement(ostream, "password", this._password);
      if (this._failedIntegrationType != null && this._failedIntegrationEmail != null) {
         XMLUtils.writeSimpleElement(ostream, "emailMessageKey", this._failedIntegrationType);
         XMLUtils.writeSimpleElement(ostream, "emailMessageAddress", this._failedIntegrationEmail);
      }

      XMLUtils.endElement(ostream, "user");
   }
}
