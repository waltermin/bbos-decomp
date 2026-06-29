package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Arrays;

class PKCS1v2SignaturesFacadeImpl extends PKCS1v2SignaturesFacade {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public byte[] sign0(OID digestOid, Digest digest) {
      ASN1OutputStream asn1Stream = new ASN1OutputStream();
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         ASN1OutputStream e = new ASN1OutputStream();
         e.writeOID(digestOid);
         e.writeNull();
         ASN1OutputStream digestInfo = new ASN1OutputStream();
         digestInfo.writeSequence(e);
         digestInfo.writeOctetString(digest.getDigest(false));
         asn1Stream.writeSequence(digestInfo);
         var7 = false;
      } finally {
         if (var7) {
            throw new RuntimeException();
         }
      }

      return asn1Stream.toByteArray();
   }

   @Override
   public boolean verify0(byte[] encodedMessage, int encodedMessageOffset, byte[] message, OID digestOid) {
      byte[] messageToVerify;
      try {
         ASN1InputByteArray asn1Stream = new ASN1InputByteArray(encodedMessage, encodedMessageOffset);
         asn1Stream.readSequence();
         asn1Stream.readSequence();
         OID oid = asn1Stream.readOID();
         if (digestOid == null || !oid.equals(digestOid) || !asn1Stream.readNull()) {
            return false;
         }

         messageToVerify = asn1Stream.readOctetString();
         if (asn1Stream.peekNextTag() != -1) {
            return false;
         }
      } catch (ASN1EncodingException e) {
         return false;
      }

      return Arrays.equals(messageToVerify, 0, message, 0, message.length);
   }
}
