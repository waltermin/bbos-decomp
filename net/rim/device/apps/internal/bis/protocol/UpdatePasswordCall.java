package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class UpdatePasswordCall implements XMLCall {
   private String _oldPassword;
   private String _newPassword;

   UpdatePasswordCall(String oldPassword, String newPassword) {
      this._oldPassword = oldPassword;
      this._newPassword = newPassword;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "password");
      XMLUtils.writeSimpleElement(ostream, "oldpassword", this._oldPassword);
      XMLUtils.writeSimpleElement(ostream, "newpassword", this._newPassword);
      XMLUtils.endElement(ostream, "password");
   }
}
