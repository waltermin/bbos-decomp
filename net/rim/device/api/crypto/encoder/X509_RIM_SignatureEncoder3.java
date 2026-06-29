package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.ECDSASignatureSigner;
import net.rim.device.api.crypto.PSSSignatureSigner;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class X509_RIM_SignatureEncoder3 extends SignatureEncoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedSignature encodeSignature(SignatureSigner signer) {
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         ASN1OutputStream e = new Object();
         if (!(signer instanceof ECDSASignatureSigner)) {
            if (signer instanceof PSSSignatureSigner) {
               PSSSignatureSigner rsaSigner = (PSSSignatureSigner)signer;
               byte[] data = new byte[rsaSigner.getLength()];
               rsaSigner.sign(data, 0);
               ((ASN1OutputStream)e).writeOID(OIDs.getOID(544212540));
               ASN1OutputStream parameters = (ASN1OutputStream)(new Object());
               ASN1OutputStream hashID = (ASN1OutputStream)(new Object());
               OID oid = OIDs.getAssociatedOID(3134008036018563479L, rsaSigner.getDigestAlgorithm());
               hashID.writeOID(oid);
               hashID.writeNull();
               parameters.writeSequence(hashID);
               ASN1OutputStream mgf = (ASN1OutputStream)(new Object());
               mgf.writeOID(OIDs.getOID(544212539));
               ASN1OutputStream mgfHash = (ASN1OutputStream)(new Object());
               mgfHash.writeOID(oid);
               mgfHash.writeNull();
               mgf.writeSequence(mgfHash);
               parameters.writeSequence(mgf);
               parameters.writeInteger(20);
               parameters.writeInteger(1);
               ((ASN1OutputStream)e).writeSequence(parameters);
               ASN1OutputStream asn1Stream = (ASN1OutputStream)(new Object());
               asn1Stream.writeSequence((ASN1OutputStream)e);
               asn1Stream.writeBitString(data);
               return (EncodedSignature)(new Object(asn1Stream.toByteArray(), "X509"));
            }

            var12 = false;
         } else {
            ECDSASignatureSigner ecdsa_signer = (ECDSASignatureSigner)signer;
            if (ecdsa_signer.getDigestAlgorithm().equals("SHA1")) {
               byte[] r = new byte[ecdsa_signer.getRLength()];
               byte[] s = new byte[ecdsa_signer.getSLength()];
               ecdsa_signer.sign(r, 0, s, 0);
               ((ASN1OutputStream)e).writeOID(OIDs.getOID(-1487362072));
               ASN1OutputStream asn1Stream = (ASN1OutputStream)(new Object());
               asn1Stream.writeSequence((ASN1OutputStream)e);
               ASN1OutputStream ECDSASigSequence = (ASN1OutputStream)(new Object());
               ECDSASigSequence.writeInteger(r);
               ECDSASigSequence.writeInteger(s);
               ASN1OutputStream tempStream = (ASN1OutputStream)(new Object());
               tempStream.writeSequence(ECDSASigSequence);
               asn1Stream.writeBitString(tempStream.toByteArray());
               return (EncodedSignature)(new Object(asn1Stream.toByteArray(), "X509"));
            }

            var12 = false;
         }
      } finally {
         if (var12) {
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
      return new String[]{"ECDSA", "RSA_PSS"};
   }
}
