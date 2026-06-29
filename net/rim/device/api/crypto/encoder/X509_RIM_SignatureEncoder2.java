package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.DSASignatureSigner;
import net.rim.device.api.crypto.PKCS1SignatureSigner;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class X509_RIM_SignatureEncoder2 extends SignatureEncoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedSignature encodeSignature(SignatureSigner signer) {
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         ASN1OutputStream e = new ASN1OutputStream();
         if (!(signer instanceof Object)) {
            if (signer instanceof Object) {
               PKCS1SignatureSigner rsaSigner = (PKCS1SignatureSigner)signer;
               byte[] data = new byte[rsaSigner.getLength()];
               rsaSigner.sign(data, 0);
               OID oid = OIDs.getAssociatedOID(-5979163936319872658L, rsaSigner.getAlgorithm());
               e.writeOID(oid);
               ASN1OutputStream asn1Stream = new ASN1OutputStream();
               asn1Stream.writeSequence(e);
               asn1Stream.writeBitString(data);
               return (EncodedSignature)(new Object(asn1Stream.toByteArray(), "X509"));
            }

            var10 = false;
         } else {
            DSASignatureSigner dsa_signer = (DSASignatureSigner)signer;
            if (dsa_signer.getDigestAlgorithm().equals("SHA1")) {
               byte[] r = new byte[dsa_signer.getRLength()];
               byte[] s = new byte[dsa_signer.getSLength()];
               dsa_signer.sign(r, 0, s, 0);
               e.writeOID(OIDs.getOID(-1487364624));
               ASN1OutputStream asn1Stream = new ASN1OutputStream();
               asn1Stream.writeSequence(e);
               ASN1OutputStream DSASigSequence = new ASN1OutputStream();
               DSASigSequence.writeInteger(r);
               DSASigSequence.writeInteger(s);
               ASN1OutputStream tempStream = new ASN1OutputStream();
               tempStream.writeSequence(DSASigSequence);
               asn1Stream.writeBitString(tempStream.toByteArray());
               return (EncodedSignature)(new Object(asn1Stream.toByteArray(), "X509"));
            }

            var10 = false;
         }
      } finally {
         if (var10) {
            throw new Object();
         }
      }

      throw new Object();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"DSA", "RSA", "RSA_PKCS1", "RSA_PKCS1_V15", "RSA_PKCS1_V20"};
   }
}
