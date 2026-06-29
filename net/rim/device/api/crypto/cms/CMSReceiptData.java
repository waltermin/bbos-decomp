package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class CMSReceiptData implements Persistable {
   private byte[] _signedContentIdentifier;
   private byte[] _signatureValue;
   private OID _contentType;
   private byte[] _messageDigest;
   private byte[] _msgSigDigest;
   private Persistable _userData;

   CMSReceiptData(byte[] signedContentIdentifier, byte[] signatureValue, OID contentType, byte[] messageDigest, byte[] msgSigDigest) {
      if (signedContentIdentifier != null && signatureValue != null && contentType != null && messageDigest != null && msgSigDigest != null) {
         this._signedContentIdentifier = signedContentIdentifier;
         this._signatureValue = signatureValue;
         this._contentType = contentType;
         this._messageDigest = messageDigest;
         this._msgSigDigest = msgSigDigest;
      } else {
         throw new Object();
      }
   }

   final byte[] getSignedContentIdentifier() {
      return this._signedContentIdentifier;
   }

   final byte[] getSignatureValue() {
      return this._signatureValue;
   }

   final OID getContentType() {
      return this._contentType;
   }

   final byte[] getMessageDigest() {
      return this._messageDigest;
   }

   final byte[] getMsgSigDigest() {
      return this._msgSigDigest;
   }

   public final void setUserData(Persistable userData) {
      this._userData = userData;
   }

   public final Persistable getUserData() {
      return this._userData;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof CMSReceiptData)) {
         return false;
      }

      CMSReceiptData other = (CMSReceiptData)obj;
      return this._contentType.equals(other._contentType)
         && Arrays.equals(this._signedContentIdentifier, other._signedContentIdentifier)
         && Arrays.equals(this._signatureValue, other._signatureValue);
   }
}
