package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

class CertificateContainer implements Persistable {
   private byte[] _encoding;
   private String _type;

   public CertificateContainer(byte[] encoding, String type) {
      this._encoding = encoding;
      this._type = type;
   }

   public byte[] getEncoding() {
      return this._encoding;
   }

   public String getType() {
      return this._type;
   }

   @Override
   public int hashCode() {
      return HashCodeCalculator.getCRC32(this._encoding);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof Certificate)) {
         if (!(o instanceof CertificateContainer)) {
            return false;
         }

         CertificateContainer certificateContainer = (CertificateContainer)o;
         return Arrays.equals(certificateContainer.getEncoding(), this._encoding);
      } else {
         Certificate certificate = (Certificate)o;
         return Arrays.equals(certificate.getEncoding(), this._encoding);
      }
   }
}
