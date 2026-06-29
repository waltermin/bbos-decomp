package net.rim.device.api.crypto.cms;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public final class CMSSigner {
   private SignatureSigner _signer;
   private Certificate[] _certificate;
   private Digest _digest;
   private boolean _writeShortForm;
   private Vector _attributes;

   public CMSSigner(SignatureSigner signer, Certificate certificate) {
      this(signer, new Object[]{certificate}, false);
   }

   public CMSSigner(SignatureSigner signer, Certificate certificate, boolean writeShortForm) {
      this(signer, new Object[]{certificate}, writeShortForm);
   }

   public CMSSigner(SignatureSigner signer, Certificate[] certificateChain) {
      this(signer, certificateChain, false);
   }

   public CMSSigner(SignatureSigner signer, Certificate[] certificateChain, boolean writeShortForm) {
      if (signer != null && certificateChain != null && certificateChain.length >= 1 && CMSUtilities.isCertificateAllowed(certificateChain[0], 1, 2)) {
         this._signer = signer;
         this._certificate = certificateChain;
         this._attributes = (Vector)(new Object());
         this._writeShortForm = writeShortForm;
      } else {
         throw new Object();
      }
   }

   public final SignatureSigner getSigner() {
      return this._signer;
   }

   public final boolean isWriteShortForm() {
      return this._writeShortForm;
   }

   public final Certificate[] getCertificateChain() {
      return this._certificate;
   }

   public final Certificate getCertificate() {
      return this._certificate == null ? null : this._certificate[0];
   }

   public final boolean addAttribute(CMSAttribute attribute) {
      if (attribute == null) {
         return false;
      }

      OID oid = attribute.getOID();
      if (attribute.isSigned()) {
         if (oid.equals(OIDs.getOID(543172156))) {
            return false;
         }

         this._attributes.addElement(attribute);
         return true;
      } else if (!OIDs.getOID(542385724).equals(oid)
         && !OIDs.getOID(542647868).equals(oid)
         && !OIDs.getOID(542910012).equals(oid)
         && !OIDs.getOID(545531452).equals(oid)
         && !OIDs.getOID(-1721342672).equals(oid)
         && !OIDs.getOID(-1721344720).equals(oid)
         && !OIDs.getOID(-1721346768).equals(oid)
         && !OIDs.getOID(-1721361104).equals(oid)
         && !OIDs.getOID(-1721354960).equals(oid)
         && !OIDs.getOID(-1721359056).equals(oid)
         && !OIDs.getOID(-1721363152).equals(oid)
         && !OIDs.getOID(-1721340624).equals(oid)) {
         this._attributes.addElement(attribute);
         return true;
      } else {
         return false;
      }
   }

   public final Enumeration getSignedAttributes() {
      int size = this._attributes.size();
      Vector signedAttributes = (Vector)(new Object());

      for (int i = 0; i < size; i++) {
         CMSAttribute attribute = (CMSAttribute)this._attributes.elementAt(i);
         if (attribute.isSigned()) {
            signedAttributes.addElement(attribute);
         }
      }

      return signedAttributes.elements();
   }

   public final Enumeration getUnsignedAttributes() {
      int size = this._attributes.size();
      Vector unsignedAttributes = (Vector)(new Object());

      for (int i = 0; i < size; i++) {
         CMSAttribute attribute = (CMSAttribute)this._attributes.elementAt(i);
         if (!attribute.isSigned()) {
            unsignedAttributes.addElement(attribute);
         }
      }

      return unsignedAttributes.elements();
   }

   public final Enumeration getAttributes() {
      return this._attributes.elements();
   }

   final void setDigest(Digest digest) {
      this._digest = digest;
   }

   final Digest getDigest() {
      return this._digest;
   }
}
