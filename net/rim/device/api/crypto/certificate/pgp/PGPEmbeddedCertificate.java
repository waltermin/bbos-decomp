package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.internal.crypto.pgp.PGPSignaturePacket;
import net.rim.vm.Persistable;

class PGPEmbeddedCertificate implements Persistable {
   private byte[] _parentKeyID;
   private PGPSignaturePacket _signaturePacket;

   public PGPEmbeddedCertificate(byte[] parentKeyID, PGPSignaturePacket signaturePacket) {
      this._parentKeyID = parentKeyID;
      this._signaturePacket = signaturePacket;
   }

   public Certificate extractX509Certificate() {
      return this._signaturePacket.extractX509Certificate();
   }

   public byte[] getParentID() {
      return this._parentKeyID;
   }
}
