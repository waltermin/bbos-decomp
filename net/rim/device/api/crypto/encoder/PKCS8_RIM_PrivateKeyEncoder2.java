package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

final class PKCS8_RIM_PrivateKeyEncoder2 extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PrivateKey key) {
      try {
         if (key.getAlgorithm().equals("RSA")) {
            RSAPrivateKey k = (RSAPrivateKey)key;
            ASN1OutputStream rsaPrivateKeySequence = new ASN1OutputStream();
            rsaPrivateKeySequence.writeInteger(0);
            rsaPrivateKeySequence.writeInteger(k.getN());
            rsaPrivateKeySequence.writeInteger(k.getE());
            rsaPrivateKeySequence.writeInteger(k.getD());
            rsaPrivateKeySequence.writeInteger(k.getP());
            rsaPrivateKeySequence.writeInteger(k.getQ());
            rsaPrivateKeySequence.writeInteger(k.getDModPm1());
            rsaPrivateKeySequence.writeInteger(k.getDModQm1());
            rsaPrivateKeySequence.writeInteger(k.getQInvModP());
            ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
            algorithmIdentifier.writeOID(OIDs.getOID(541853244));
            algorithmIdentifier.writeNull();
            ASN1OutputStream privateKeyInfo = new ASN1OutputStream();
            privateKeyInfo.writeInteger(0);
            privateKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream tempSequence = new ASN1OutputStream();
            tempSequence.writeSequence(rsaPrivateKeySequence);
            privateKeyInfo.writeOctetString(tempSequence.toByteArray());
            ASN1OutputStream asn1Stream = new ASN1OutputStream();
            asn1Stream.writeSequence(privateKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }

         if (key.getAlgorithm().equals("DH")) {
            DHPrivateKey k = (DHPrivateKey)key;
            ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
            algorithmIdentifier.writeOID(OIDs.getOID(-1487623704));
            DHCryptoSystem cryptoSystem = k.getDHCryptoSystem();
            ASN1OutputStream params = new ASN1OutputStream();
            params.writeInteger(cryptoSystem.getP());
            params.writeInteger(cryptoSystem.getG());
            byte[] q = cryptoSystem.getQ();
            if (q != null) {
               params.writeInteger(q);
            } else {
               params.writeInteger(0);
            }

            algorithmIdentifier.writeSequence(params);
            ASN1OutputStream privateKeyInfo = new ASN1OutputStream();
            privateKeyInfo.writeInteger(0);
            privateKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream converter = new ASN1OutputStream();
            converter.writeInteger(k.getPrivateKeyData());
            privateKeyInfo.writeOctetString(converter.toByteArray());
            ASN1OutputStream asn1Stream = new ASN1OutputStream();
            asn1Stream.writeSequence(privateKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }

         if (key.getAlgorithm().equals("DSA")) {
            DSAPrivateKey k = (DSAPrivateKey)key;
            ASN1OutputStream algorithmIdentifier = new ASN1OutputStream();
            algorithmIdentifier.writeOID(OIDs.getOID(-1487364632));
            DSACryptoSystem cryptoSystem = k.getDSACryptoSystem();
            ASN1OutputStream params = new ASN1OutputStream();
            params.writeInteger(cryptoSystem.getP());
            params.writeInteger(cryptoSystem.getQ());
            params.writeInteger(cryptoSystem.getG());
            algorithmIdentifier.writeSequence(params);
            ASN1OutputStream privateKeyInfo = new ASN1OutputStream();
            privateKeyInfo.writeInteger(0);
            privateKeyInfo.writeSequence(algorithmIdentifier);
            ASN1OutputStream converter = new ASN1OutputStream();
            converter.writeInteger(k.getPrivateKeyData());
            privateKeyInfo.writeOctetString(converter.toByteArray());
            ASN1OutputStream asn1Stream = new ASN1OutputStream();
            asn1Stream.writeSequence(privateKeyInfo);
            return (EncodedKey)(new Object(asn1Stream.toByteArray(), "PKCS8"));
         }
      } catch (Throwable var11) {
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
      return new String[]{"RSA", "DH", "DSA"};
   }
}
