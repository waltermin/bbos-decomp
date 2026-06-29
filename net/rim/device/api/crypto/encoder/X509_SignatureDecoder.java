package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;
import net.rim.device.api.crypto.RIMFactoryUtilities;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class X509_SignatureDecoder extends SignatureDecoder {
   @Override
   protected DecodedSignature decodeSignature(InputStream encodedSignature, String signatureAlgorithm, String digestAlgorithm) throws InvalidSignatureEncodingException {
      if (encodedSignature == null) {
         throw new IllegalArgumentException();
      }

      try {
         ASN1InputStream asn1Stream = new ASN1InputStream(encodedSignature);
         ASN1InputByteArray realStream = new ASN1InputByteArray(asn1Stream.readFieldAsByteArray());
         realStream.readSequence();
         OID algorithm = realStream.readOID();
         String encodedSignatureAlgorithm = OIDs.getAssociatedString(-5979163936319872658L, algorithm);
         if (encodedSignatureAlgorithm == null) {
            encodedSignatureAlgorithm = signatureAlgorithm;
         }

         if (encodedSignatureAlgorithm == null) {
            throw new InvalidSignatureEncodingException();
         }

         String encodedDigestAlgorithm = RIMFactoryUtilities.stripLeftMostSubAlgorithm(encodedSignatureAlgorithm);
         if (encodedDigestAlgorithm == null) {
            encodedDigestAlgorithm = digestAlgorithm;
         }

         encodedSignatureAlgorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(encodedSignatureAlgorithm);
         byte[] encodedSignatureArray = asn1Stream.readBitString().toByteArray();
         X509_SignatureDecoder decoder = (X509_SignatureDecoder)SignatureDecoder.getDecoder("X509", encodedSignatureAlgorithm);
         return decoder.decodeSignature(realStream, encodedSignatureArray, encodedSignatureAlgorithm, encodedDigestAlgorithm);
      } catch (ASN1EncodingException var13) {
      } finally {
         throw new InvalidSignatureEncodingException();
      }

      throw new InvalidSignatureEncodingException();
   }

   protected DecodedSignature decodeSignature(ASN1InputByteArray parameters, byte[] encodedSignature, String signatureAlgorithm, String digestAlgorithm) {
      throw new RuntimeException();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected String[] getSignatureAlgorithms() {
      return null;
   }
}
