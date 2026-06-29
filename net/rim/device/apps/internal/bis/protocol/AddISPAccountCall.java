package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class AddISPAccountCall implements XMLCall, BISServiceConstants {
   private String _email;
   private String _server;
   private String _userName;
   private String _password;

   AddISPAccountCall(String email, String server, String userName, String password) {
      this._email = email;
      this._server = server;
      this._userName = userName;
      this._password = password;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "emailAccount");
      XMLUtils.writeSimpleElement(ostream, "hosted", "false");
      XMLUtils.writeSimpleElement(ostream, "email", this._email);
      XMLUtils.writeSimpleElement(ostream, "server", this._server);
      XMLUtils.writeSimpleElement(ostream, "userName", this._userName);
      XMLUtils.writeSimpleElement(ostream, "password", this._password);
      XMLUtils.endElement(ostream, "emailAccount");
   }
}
