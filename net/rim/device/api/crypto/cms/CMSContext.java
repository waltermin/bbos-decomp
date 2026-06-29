package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public final class CMSContext {
   private CMSInputStream stream;

   CMSContext(InputStream inputStream) {
      ASN1InputStream asn1Stream = new ASN1InputStream(inputStream);
      ASN1InputStream cmsSequence = asn1Stream.readSequence();
      OID contentType = cmsSequence.readOID();
      if (contentType.equals(OIDs.getOID(541859388))) {
         this.stream = new CMSDataInputStream(cmsSequence.readOctetString(1, 0));
      } else if (contentType.equals(OIDs.getOID(542383676))) {
         this.stream = new CMSEnvelopedDataInputStream(cmsSequence.readStreamWithTag(0), null, null, true, false);
      } else if (contentType.equals(OIDs.getOID(542121532))) {
         this.stream = new CMSSignedDataInputStream(cmsSequence.readStreamWithTag(0), null, true, false);
      } else if (contentType.equals(OIDs.getOID(-1721352904))) {
         this.stream = new CMSCompressedDataInputStream(cmsSequence.readStreamWithTag(0), null, true, false);
      } else if (contentType.equals(OIDs.getOID(-1721352925))) {
         this.stream = new CMSSignedReceiptInputStream(cmsSequence.readStreamWithTag(0));
      } else if (contentType.equals(OIDs.getOID(-477712249))) {
         this.stream = new EMSAcceptRequestInputStream(cmsSequence.readStreamWithTag(0));
      } else {
         this.stream = null;
      }
   }

   final CMSInputStream getCMSInputStream(KeyStore keyStore, SymmetricKey sessionKey, boolean displayUI) {
      if (!(this.stream instanceof CMSSignedDataInputStream)) {
         if (!(this.stream instanceof CMSEnvelopedDataInputStream)) {
            if (this.stream instanceof CMSCompressedDataInputStream) {
               ((CMSCompressedDataInputStream)this.stream).continueInitialization(keyStore, displayUI);
            }
         } else {
            ((CMSEnvelopedDataInputStream)this.stream).continueInitialization(keyStore, sessionKey, displayUI);
         }
      } else {
         ((CMSSignedDataInputStream)this.stream).continueInitialization(keyStore, displayUI);
      }

      return this.stream;
   }

   public final CMSEntityIdentifier[] getEncryptionRecipients() {
      return this.getEncryptionRecipients(this.stream);
   }

   private final CMSEntityIdentifier[] getEncryptionRecipients(CMSInputStream inputStream) {
      if (!(inputStream instanceof CMSEnvelopedDataInputStream)) {
         return inputStream instanceof CMSInputStream ? this.getEncryptionRecipients(inputStream.getCMSInputStream()) : null;
      } else {
         return ((CMSEnvelopedDataInputStream)inputStream).getRecipients();
      }
   }

   public final CMSEntityIdentifier[] getSigners() {
      return this.getSigners(this.stream);
   }

   private final CMSEntityIdentifier[] getSigners(CMSInputStream inputStream) {
      if (!(inputStream instanceof CMSSignedDataInputStream)) {
         return inputStream instanceof CMSInputStream ? this.getSigners(inputStream.getCMSInputStream()) : null;
      } else {
         return ((CMSSignedDataInputStream)inputStream).getSigners();
      }
   }

   public final boolean isSigned() {
      return this.stream.isSigned();
   }

   public final boolean isEncrypted() {
      return this.stream.isEncrypted();
   }
}
