package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

final class PKCS8_RIM_SymmetricKeyEncoder3 extends SymmetricKeyEncoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(SymmetricKey key) {
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         if (key.getAlgorithm().equals("RC2")) {
            RC2Key k = (RC2Key)key;
            ASN1OutputStream algorithmIdentifier = (ASN1OutputStream)(new Object());
            algorithmIdentifier.writeOID(OIDs.getOID(1536208895));
            algorithmIdentifier.writeInteger(k.getEffectiveBitLength());
            ASN1OutputStream symmetricKeyInfo = (ASN1OutputStream)(new Object());
            symmetricKeyInfo.writeInteger(0);
            symmetricKeyInfo.writeSequence(algorithmIdentifier);
            symmetricKeyInfo.writeOctetString(k.getData());
            ASN1OutputStream asn1Stream = (ASN1OutputStream)(new Object());
            asn1Stream.writeSequence(symmetricKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }

         if (key.getAlgorithm().equals("Skipjack")) {
            return PKCS8_RIM_SymmetricKeyEncoder2.encodeKey(key, 1536208911);
         }

         if (key.getAlgorithm().equals("CAST128")) {
            return PKCS8_RIM_SymmetricKeyEncoder2.encodeKey(key, 1536208871);
         }

         var7 = false;
      } finally {
         if (var7) {
            throw new Object();
         }
      }

      throw new Object();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"RC2", "Skipjack", "CAST128"};
   }
}
