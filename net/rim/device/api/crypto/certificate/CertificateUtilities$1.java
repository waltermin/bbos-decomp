package net.rim.device.api.crypto.certificate;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.NumberUtilities;

class CertificateUtilities$1 implements javax.microedition.pki.Certificate {
   private final Certificate val$cert;

   CertificateUtilities$1(Certificate _1) {
      this.val$cert = _1;
   }

   @Override
   public String getSubject() {
      return this.val$cert.getSubject().toString();
   }

   @Override
   public String getIssuer() {
      return CertificateUtilities.getFriendlyName(this.val$cert.getIssuer());
   }

   @Override
   public String getType() {
      return this.val$cert.getType().equals("X509") ? "X.509" : this.val$cert.getType();
   }

   @Override
   public String getVersion() {
      return Integer.toString(this.val$cert.getVersion());
   }

   @Override
   public String getSigAlgName() {
      return this.val$cert.getSignatureAlgorithm();
   }

   @Override
   public long getNotBefore() {
      return this.val$cert.getNotBefore();
   }

   @Override
   public long getNotAfter() {
      return this.val$cert.getNotAfter();
   }

   @Override
   public String getSerialNumber() {
      byte[] serialNumber = this.val$cert.getSerialNumber();
      int length = serialNumber.length;
      char[] printable = new char[length * 3 - 1];
      int i = 0;
      int j = 0;

      while (i < length) {
         printable[j++] = CharacterUtilities.toUpperCase(NumberUtilities.intToHexDigit(serialNumber[i] >> 4), 1701707776);
         printable[j++] = CharacterUtilities.toUpperCase(NumberUtilities.intToHexDigit(serialNumber[i]), 1701707776);
         if (i < length - 1) {
            printable[j++] = ':';
         }

         i++;
      }

      return (String)(new Object(printable));
   }
}
