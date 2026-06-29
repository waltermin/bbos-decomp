package net.rim.device.api.crypto.tls;

import javax.microedition.io.SecurityInfo;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.i18n.ResourceBundle;

public class TLSSecurityInfo implements SecurityInfo {
   private Certificate _certificate;
   private int _remoteVersion;
   private int _cipherSuite;
   private int _keyExchangeSize;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   public Certificate getRIMServerCertificate() {
      return this._certificate;
   }

   @Override
   public String getProtocolVersion() {
      if (this._remoteVersion == 768) {
         return "3.0";
      } else if (this._remoteVersion == 769) {
         return "3.1";
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public String getProtocolName() {
      if (this._remoteVersion == 768) {
         return "SSL";
      } else if (this._remoteVersion == 769) {
         return "TLS";
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public String getCipherSuite() {
      StringBuffer buffer = new StringBuffer();
      switch (this._cipherSuite) {
         case 0:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 47:
         case 53:
            buffer.append("RSA ");
            break;
         case 11:
         case 12:
         case 13:
         case 48:
         case 54:
            buffer.append("DH/DSS ");
            break;
         case 14:
         case 15:
         case 16:
         case 49:
         case 55:
            buffer.append("DH/RSA ");
            break;
         case 17:
         case 18:
         case 19:
         case 50:
         case 56:
            buffer.append("DHE/DSS ");
            break;
         case 20:
         case 21:
         case 22:
         case 51:
         case 57:
            buffer.append("DHE/RSA ");
            break;
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 52:
         case 58:
         default:
            buffer.append("DH anon ");
            break;
         case 72:
         case 73:
         case 74:
            buffer.append("ECDH ECDSA ");
            break;
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
            buffer.append("ECDH Anon ");
      }

      if (this._keyExchangeSize != 0) {
         buffer.append(this._keyExchangeSize).append(' ');
      }

      switch (this._cipherSuite) {
         case -1:
         case 7:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
            buffer.append(_rb.getString(18));
         case 0:
         case 1:
         case 2:
            break;
         case 3:
         case 23:
         case 90:
            buffer.append("RC4 40");
            break;
         case 4:
         case 5:
         case 24:
         case 72:
         case 86:
            buffer.append("RC4 128");
            break;
         case 6:
            buffer.append("RC2 40");
            break;
         case 8:
         case 11:
         case 14:
         case 17:
         case 20:
         case 25:
         case 89:
         default:
            buffer.append("DES 40");
            break;
         case 9:
         case 12:
         case 15:
         case 18:
         case 21:
         case 26:
         case 73:
         case 87:
            buffer.append("DES 56");
            break;
         case 10:
         case 13:
         case 16:
         case 19:
         case 22:
         case 27:
         case 74:
         case 88:
            buffer.append("3DES 168");
            break;
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
            buffer.append("AES 128");
            break;
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            buffer.append("AES 256");
      }

      return buffer.toString();
   }

   @Override
   public javax.microedition.pki.Certificate getServerCertificate() {
      return CertificateUtilities.convertCertificate(this._certificate);
   }

   public TLSSecurityInfo(Certificate certificate, int remoteVersion, int cipherSuite, int keyExchangeSize) {
      this._certificate = certificate;
      this._remoteVersion = remoteVersion;
      this._cipherSuite = cipherSuite;
      this._keyExchangeSize = keyExchangeSize;
   }
}
