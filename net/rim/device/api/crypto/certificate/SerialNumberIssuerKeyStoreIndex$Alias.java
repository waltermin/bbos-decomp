package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.util.Arrays;

class SerialNumberIssuerKeyStoreIndex$Alias {
   public byte[] _serialNumber;
   public DistinguishedName _issuer;
   private int _hashCode;

   public SerialNumberIssuerKeyStoreIndex$Alias(byte[] serialNumber, DistinguishedName issuer) {
      this._serialNumber = serialNumber;
      this._issuer = issuer;
      this._hashCode = HashCodeCalculator.getCRC32(this._serialNumber) ^ this._issuer.hashCode();
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof SerialNumberIssuerKeyStoreIndex$Alias)) {
         return false;
      }

      SerialNumberIssuerKeyStoreIndex$Alias a = (SerialNumberIssuerKeyStoreIndex$Alias)obj;
      return Arrays.equals(this._serialNumber, a._serialNumber) && this._issuer.equals(a._issuer);
   }
}
