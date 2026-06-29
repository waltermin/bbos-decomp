package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.KEACryptoSystem;
import net.rim.device.api.crypto.KEAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

final class PKCS8_RIM_PrivateKeyEncoder3 extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PrivateKey key) {
      try {
         if (key.getAlgorithm().equals("EC")) {
            ECPrivateKey k = (ECPrivateKey)key;
            ASN1OutputStream ecPrivateKeySequence = (ASN1OutputStream)(new Object());
            ecPrivateKeySequence.writeInteger(1);
            ecPrivateKeySequence.writeOctetString(k.getPrivateKeyData());
            ASN1OutputStream algorithmIdentifier = (ASN1OutputStream)(new Object());
            algorithmIdentifier.writeOID(OIDs.getOID(-1487624216));
            algorithmIdentifier.writeOID(OIDs.getAssociatedOID(-3607261449824502613L, k.getECCryptoSystem().getName()));
            ASN1OutputStream privateKeyInfo = (ASN1OutputStream)(new Object());
            privateKeyInfo.writeInteger(0);
            privateKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream converter = (ASN1OutputStream)(new Object());
            converter.writeSequence(ecPrivateKeySequence);
            privateKeyInfo.writeOctetString(converter.toByteArray());
            ASN1OutputStream asn1Stream = (ASN1OutputStream)(new Object());
            asn1Stream.writeSequence(privateKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }

         if (key.getAlgorithm().equals("KEA")) {
            KEAPrivateKey k = (KEAPrivateKey)key;
            ASN1OutputStream algorithmIdentifier = (ASN1OutputStream)(new Object());
            algorithmIdentifier.writeOID(OIDs.getOID(545973096));
            KEACryptoSystem cryptoSystem = k.getKEACryptoSystem();
            ASN1OutputStream params = (ASN1OutputStream)(new Object());
            params.writeInteger(cryptoSystem.getP());
            params.writeInteger(cryptoSystem.getQ());
            params.writeInteger(cryptoSystem.getG());
            algorithmIdentifier.writeSequence(params);
            ASN1OutputStream privateKeyInfo = (ASN1OutputStream)(new Object());
            privateKeyInfo.writeInteger(0);
            privateKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream converter = (ASN1OutputStream)(new Object());
            converter.writeInteger(k.getPrivateKeyData());
            privateKeyInfo.writeOctetString(converter.toByteArray());
            ASN1OutputStream asn1Stream = (ASN1OutputStream)(new Object());
            asn1Stream.writeSequence(privateKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }
      } catch (Throwable var10) {
         throw new Object(e.toString());
      }

      throw new Object();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"EC", "KEA"};
   }
}
