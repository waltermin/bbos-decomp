package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;
import net.rim.device.internal.browser.util.IdEncryptor;

final class PreAuthenticationCall implements XMLCall {
   private String _pin;
   private String _imei;
   private String _imsi;
   private String _iccid;
   private String _msisdn;

   PreAuthenticationCall(String pin, String imei, String imsi, String iccid, String msisdn) {
      this._pin = pin;
      this._imei = imei;
      this._imsi = imsi;
      this._iccid = iccid;
      this._msisdn = msisdn;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "device");
      XMLUtils.writeSimpleElement(ostream, "pin", IdEncryptor.encrypt(this._pin, 0));
      XMLUtils.writeSimpleElement(ostream, "imei", IdEncryptor.encrypt(this._imei, 0));
      if (this._imsi != null) {
         XMLUtils.writeSimpleElement(ostream, "imsi", IdEncryptor.encrypt(this._imsi, 0));
      }

      if (this._iccid != null) {
         XMLUtils.writeSimpleElement(ostream, "iccid", IdEncryptor.encrypt(this._iccid, 0));
      }

      if (this._msisdn != null) {
         XMLUtils.writeSimpleElement(ostream, "msisdn", IdEncryptor.encrypt(this._msisdn, 0));
      }

      XMLUtils.endElement(ostream, "device");
   }
}
