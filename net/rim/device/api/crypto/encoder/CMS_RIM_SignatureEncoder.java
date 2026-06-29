package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.DSASignatureSigner;
import net.rim.device.api.crypto.ECDSASignatureSigner;
import net.rim.device.api.crypto.PKCS1SignatureSigner;
import net.rim.device.api.crypto.PSSSignatureSigner;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class CMS_RIM_SignatureEncoder extends SignatureEncoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedSignature encodeSignature(SignatureSigner signer) {
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         ASN1OutputStream e = new ASN1OutputStream();
         if (!(signer instanceof ECDSASignatureSigner)) {
            if (!(signer instanceof DSASignatureSigner)) {
               if (signer instanceof PKCS1SignatureSigner) {
                  PKCS1SignatureSigner rsaSigner = (PKCS1SignatureSigner)signer;
                  byte[] data = new byte[rsaSigner.getLength()];
                  rsaSigner.sign(data, 0);
                  OID oid = OIDs.getOID(541853244);
                  e.writeOID(oid);
                  e.writeNull();
                  ASN1OutputStream asn1Stream = new ASN1OutputStream();
                  asn1Stream.writeSequence(e);
                  asn1Stream.writeOctetString(data);
                  return new EncodedSignature(asn1Stream.toByteArray(), "CMS");
               }

               if (signer instanceof PSSSignatureSigner) {
                  PSSSignatureSigner rsaSigner = (PSSSignatureSigner)signer;
                  byte[] data = new byte[rsaSigner.getLength()];
                  rsaSigner.sign(data, 0);
                  e.writeOID(OIDs.getOID(544212540));
                  ASN1OutputStream parameters = new ASN1OutputStream();
                  ASN1OutputStream hashID = new ASN1OutputStream();
                  OID oid = OIDs.getAssociatedOID(3134008036018563479L, rsaSigner.getDigestAlgorithm());
                  hashID.writeOID(oid);
                  hashID.writeNull();
                  parameters.writeSequence(hashID);
                  ASN1OutputStream mgf = new ASN1OutputStream();
                  mgf.writeOID(OIDs.getOID(544212539));
                  ASN1OutputStream mgfHash = new ASN1OutputStream();
                  mgfHash.writeOID(oid);
                  mgfHash.writeNull();
                  mgf.writeSequence(mgfHash);
                  parameters.writeSequence(mgf);
                  parameters.writeInteger(20);
                  parameters.writeInteger(1);
                  e.writeSequence(parameters);
                  ASN1OutputStream asn1Stream = new ASN1OutputStream();
                  asn1Stream.writeSequence(e);
                  asn1Stream.writeOctetString(data);
                  return new EncodedSignature(asn1Stream.toByteArray(), "CMS");
               }

               var12 = false;
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
                  asn1Stream.writeOctetString(tempStream.toByteArray());
                  return new EncodedSignature(asn1Stream.toByteArray(), "CMS");
               }

               var12 = false;
            }
         } else {
            ECDSASignatureSigner ecdsa_signer = (ECDSASignatureSigner)signer;
            if (ecdsa_signer.getDigestAlgorithm().equals("SHA1")) {
               byte[] r = new byte[ecdsa_signer.getRLength()];
               byte[] s = new byte[ecdsa_signer.getSLength()];
               ecdsa_signer.sign(r, 0, s, 0);
               e.writeOID(OIDs.getOID(-1487362072));
               ASN1OutputStream asn1Stream = new ASN1OutputStream();
               asn1Stream.writeSequence(e);
               ASN1OutputStream ECDSASigSequence = new ASN1OutputStream();
               ECDSASigSequence.writeInteger(r);
               ECDSASigSequence.writeInteger(s);
               ASN1OutputStream tempStream = new ASN1OutputStream();
               tempStream.writeSequence(ECDSASigSequence);
               asn1Stream.writeOctetString(tempStream.toByteArray());
               return new EncodedSignature(asn1Stream.toByteArray(), "CMS");
            }

            var12 = false;
         }
      } finally {
         if (var12) {
            throw new RuntimeException();
         }
      }

      throw new IllegalArgumentException();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "CMS";
   }

   @Override
   protected final String[] getSignatureAlgorithms() {
      return new String[]{"ECDSA", "DSA", "RSA", "RSA_PKCS1", "RSA_PKCS1_V15", "RSA_PKCS1_V20", "RSA_PSS"};
   }
}
