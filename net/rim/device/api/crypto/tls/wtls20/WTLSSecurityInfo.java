package net.rim.device.api.crypto.tls.wtls20;

import javax.microedition.io.SecurityInfo;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;

class WTLSSecurityInfo implements SecurityInfo {
   private Certificate _certificate;
   private int _cipherSuite;
   private byte _keyAgreement;
   private int _keyAgreementSize;
   private int _keySizeBits;

   protected WTLSSecurityInfo(Certificate cert, byte keyAgreement, int cipherSuite, int keyAgreementSize, int keySizeBits) {
      this._certificate = cert;
      this._cipherSuite = cipherSuite;
      this._keyAgreement = keyAgreement;
      this._keyAgreementSize = keyAgreementSize;
      this._keySizeBits = keySizeBits;
   }

   @Override
   public javax.microedition.pki.Certificate getServerCertificate() {
      return this._certificate == null ? null : CertificateUtilities.convertCertificate(this._certificate);
   }

   @Override
   public String getProtocolVersion() {
      return "1";
   }

   @Override
   public String getProtocolName() {
      return "WTLS";
   }

   @Override
   public String getCipherSuite() {
      StringBuffer buffer = (StringBuffer)(new Object());
      boolean includedSize = false;
      switch (this._keyAgreement) {
         case 1:
         case 3:
         case 4:
         case 12:
         case 13:
            break;
         case 2:
            buffer.append("DH anon ");
            break;
         case 5:
         default:
            buffer.append("RSA anon ");
            break;
         case 6:
            buffer.append("RSA anon 512 ");
            includedSize = true;
            break;
         case 7:
            buffer.append("RSA anon 768 ");
            includedSize = true;
            break;
         case 8:
            buffer.append("RSA ");
            break;
         case 9:
            buffer.append("RSA 512 ");
            includedSize = true;
            break;
         case 10:
            buffer.append("RSA 768 ");
            includedSize = true;
            break;
         case 11:
            buffer.append("ECDH anon ");
            break;
         case 14:
            buffer.append("ECDH ECDSA ");
      }

      if (this._keyAgreementSize != 0 && !includedSize) {
         buffer.append(this._keyAgreementSize).append(' ');
      }

      switch (this._cipherSuite >> 8) {
         case -1:
         case 4:
         case 7:
         case 8:
         case 9:
            break;
         case 0:
         case 3:
         default:
            buffer.append("RC5 ").append(this._keySizeBits);
            break;
         case 1:
            buffer.append("RC5 40");
            break;
         case 2:
            buffer.append("RC5 56");
            break;
         case 5:
            buffer.append("DES 56");
            break;
         case 6:
            buffer.append("TripleDES 168");
            break;
         case 10:
            buffer.append("RC5 64");
      }

      return buffer.toString();
   }
}
