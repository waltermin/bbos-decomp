package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class SignupUserCall implements XMLCall, BISServiceConstants {
   private String _pin;
   private String _imei;
   private String _language;
   private String _userName;
   private String _password;
   private String _tcVersion;

   SignupUserCall(String pin, String imei, String language, String userName, String password, String tcVersion) {
      this._pin = pin;
      this._imei = imei;
      this._language = language;
      this._userName = userName;
      this._password = password;
      this._tcVersion = tcVersion;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "user");
      XMLUtils.writeSimpleElement(ostream, "pin", this._pin);
      XMLUtils.writeSimpleElement(ostream, "imei", this._imei);
      XMLUtils.writeSimpleElement(ostream, "language", this._language);
      XMLUtils.writeSimpleElement(ostream, "username", this._userName);
      XMLUtils.writeSimpleElement(ostream, "password", this._password);
      XMLUtils.writeSimpleElement(ostream, "tcversion", this._tcVersion);
      XMLUtils.endElement(ostream, "user");
   }
}
