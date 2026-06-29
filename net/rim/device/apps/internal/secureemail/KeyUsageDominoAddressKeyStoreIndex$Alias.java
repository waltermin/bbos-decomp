package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;

class KeyUsageDominoAddressKeyStoreIndex$Alias {
   private int _usage;
   private X509DistinguishedName _dominoAddressDN;
   private int _hashCode;

   public KeyUsageDominoAddressKeyStoreIndex$Alias(int usage, String dominoAddress) {
      this._usage = usage;
      this._dominoAddressDN = DominoAddressUtilities.createDominoAddressDN(dominoAddress);
      if (this._dominoAddressDN != null) {
         this._hashCode = this._usage ^ DominoAddressUtilities.computeDominoAddressHashCode(this._dominoAddressDN);
      }
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }
}
