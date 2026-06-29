package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;
import net.rim.device.internal.browser.util.IdEncryptor;

final class LoginCall implements XMLCall, BISServiceConstants {
   private String _pin;
   private String _imei;
   private String _password;
   private String _acceptPinChange;
   private boolean _autoAuth;

   LoginCall(String password, String pin, String imei, String acceptPinChange, boolean autoAuth) {
      this._pin = pin;
      this._imei = imei;
      this._password = password;
      this._acceptPinChange = acceptPinChange;
      this._autoAuth = autoAuth;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "login");
      if (this._password != null) {
         XMLUtils.writeSimpleElement(ostream, "password", this._password);
      }

      XMLUtils.writeSimpleElement(ostream, "pin", IdEncryptor.encrypt(this._pin, 0));
      XMLUtils.writeSimpleElement(ostream, "imei", IdEncryptor.encrypt(this._imei, 0));
      if (this._acceptPinChange != null) {
         XMLUtils.writeSimpleElement(ostream, "acceptPinChange", this._acceptPinChange);
      }

      XMLUtils.writeSimpleElement(ostream, "autoAuth", this._autoAuth ? "true" : "false");
      XMLUtils.endElement(ostream, "login");
   }
}
