package net.rim.device.api.crypto.cms;

import java.util.Vector;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;

public final class CMSEntityIdentifier {
   private byte[] _subjectKeyIdentifier;
   private byte[] _serialNumber;
   private X509DistinguishedName _issuer;
   private Vector _attributes;
   private CMSInputStream _creator;
   private byte[] _digestAlgorithmIdentifier;
   private byte[] _signedAttributeArray;
   private byte[] _signatureEncoding;
   private Exception _exception;
   private boolean _verified;

   CMSEntityIdentifier(byte[] serialNumber, X509DistinguishedName issuer) {
      if (serialNumber != null && issuer != null) {
         this._serialNumber = serialNumber;
         this._issuer = issuer;
      } else {
         throw new Object();
      }
   }

   CMSEntityIdentifier(byte[] subjectKeyIdentifier) {
      if (subjectKeyIdentifier == null) {
         throw new Object();
      }

      this._subjectKeyIdentifier = subjectKeyIdentifier;
   }

   public final X509DistinguishedName getIssuer() {
      return this._issuer;
   }

   public final byte[] getSerialNumber() {
      return this._serialNumber;
   }

   public final byte[] getSubjectKeyIdentifier() {
      return this._subjectKeyIdentifier;
   }

   final void setSignerInfo(Vector attributes, CMSInputStream creator, byte[] digestAlgorithmIdentifier, byte[] signedAttributeArray, byte[] signatureEncoding) {
      this._attributes = attributes;
      this._creator = creator;
      this._digestAlgorithmIdentifier = digestAlgorithmIdentifier;
      this._signedAttributeArray = signedAttributeArray;
      this._signatureEncoding = signatureEncoding;
   }

   final void setLastException(Exception e) {
      this._exception = e;
   }

   final void setVerified(boolean verified) {
      this._verified = verified;
   }

   final Vector getAttributes() {
      return this._attributes;
   }

   final CMSInputStream getCreator() {
      return this._creator;
   }

   final byte[] getDigestAlgorithm() {
      return this._digestAlgorithmIdentifier;
   }

   final byte[] getSignedAttributeArray() {
      return this._signedAttributeArray;
   }

   final byte[] getSignatureEncoding() {
      return this._signatureEncoding;
   }

   final Exception getLastException() {
      return this._exception;
   }

   final boolean getVerified() {
      return this._verified;
   }
}
